package com.and20roid.backend.service;

import static com.and20roid.backend.common.constant.Constant.BOARD_STATE_OPEN;
import static com.and20roid.backend.common.constant.Constant.BOARD_STATE_PENDING;

import com.and20roid.backend.common.exception.CustomException;
import com.and20roid.backend.common.response.CommonCode;
import com.and20roid.backend.entity.Authority;
import com.and20roid.backend.entity.Board;
import com.and20roid.backend.entity.BoardLikeStatus;
import com.and20roid.backend.entity.FcmToken;
import com.and20roid.backend.entity.ParticipationStatus;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.AppIntroductionImageRepository;
import com.and20roid.backend.repository.AuthorityRepository;
import com.and20roid.backend.repository.BoardLikeStatusRepository;
import com.and20roid.backend.repository.BoardRepository;
import com.and20roid.backend.repository.FcmMessageRepository;
import com.and20roid.backend.repository.FcmTokenRepository;
import com.and20roid.backend.repository.ParticipationInviteStatusRepository;
import com.and20roid.backend.repository.ParticipationStatusRepository;
import com.and20roid.backend.repository.UserInteractionStatusRepository;
import com.and20roid.backend.repository.UserRepository;
import com.and20roid.backend.vo.ReadRankQuery;
import com.and20roid.backend.vo.ReadUserTestingStats;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserInteractionStatusRepository userInteractionStatusRepository;
    private final ParticipationInviteStatusRepository participationInviteStatusRepository;
    private final FcmMessageRepository fcmMessageRepository;
    private final BoardLikeStatusRepository boardLikeStatusRepository;
    private final AppIntroductionImageRepository appIntroductionImageRepository;

    private static final String ROLE_USER = "ROLE_USER";
    public static final String USER_INFO_REQUEST_TYPE_MY = "MY_INFO";
    public static final String USER_INFO_REQUEST_TYPE_ANOTHER = "ANOTHER_USER_INFO";

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final BoardRepository boardRepository;
    private final ParticipationStatusRepository participationStatusRepository;
    private final FcmTokenRepository fcmTokenRepository;

    public String signup(String uid, String nickname) {
        // 이미 존재하는 유저인 경우 예외처리
        if (userRepository.existsByUid(uid)) {
            throw new CustomException(CommonCode.ALREADY_EXIST_USER);
        }

        // 이미 존재하는 닉네임인 경우 예외처리
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(CommonCode.ALREADY_EXIST_NICKNAME);
        }

        User savedUser = userRepository.save(new User(uid, nickname));
        authorityRepository.save(new Authority(savedUser, ROLE_USER));

        return "성공";
    }

    public ReadUserTestingStats readUserTestingStats(Long userId, Long myUserId, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        int completedTestCount = 0;
        Integer rank = null;
        String nickname = user.getNickname();

        int uploadBoardCount = boardRepository.countByUserId(userId);
        ReadRankQuery readRankQuery = participationStatusRepository.readRankAndCompletedTestCountByUserId(userId);

        if (readRankQuery != null) {
            completedTestCount = readRankQuery.getCompletedTestCount();
            rank = readRankQuery.getRank();
        }

        // 다른 유저 정보의 경우에는 서로 도움 횟수까지 Response
        if (type.equals(USER_INFO_REQUEST_TYPE_ANOTHER)) {
            long interactionCounts = userInteractionStatusRepository.countUserInteractionStatusByUserId(myUserId, userId);
            return new ReadUserTestingStats(completedTestCount, uploadBoardCount, rank, nickname, interactionCounts);
        }

        return new ReadUserTestingStats(completedTestCount, uploadBoardCount, rank, nickname, null);
    }

    public void createFcmToken(String token, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        // 중복 데이터 저장되지 않도록
        if (!fcmTokenRepository.existsByUserIdAndToken(userId, token)) {
            log.info("FCM 토큰 생성 by userId: [{}], token: [{}]", userId, token);
            fcmTokenRepository.save(new FcmToken(user, token));
        }
    }

    @Transactional
    public void deleteFcmToken(long userId) {
        fcmTokenRepository.deleteAllByUserId(userId);
    }

    public void updateUser(String nickname, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(CommonCode.ALREADY_EXIST_NICKNAME);
        }

        user.updateNickname(nickname);

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        appIntroductionImageRepository.deleteAllByUserId(userId);
        authorityRepository.deleteAllByUserId(userId);
        fcmMessageRepository.deleteAllByUserId(userId);
        fcmTokenRepository.deleteAllByUserId(userId);
        participationInviteStatusRepository.deleteAllByUserId(userId);
        userInteractionStatusRepository.deleteAllByUserId(userId);

        // 참여한 게시물
        List<ParticipationStatus> participationStatuses = participationStatusRepository.findAllByUserId(userId);
        List<Board> boards = new ArrayList<>();

        for (ParticipationStatus participationStatus : participationStatuses) {
            Board board = participationStatus.getBoard();
            board.subtractParticipantNum();

            if (board.getState().equals(BOARD_STATE_PENDING)) {
                board.updateStatus(BOARD_STATE_OPEN);
            }

            boards.add(board);
        }

        if (!boards.isEmpty()) {
            boardRepository.saveAll(boards);
        }

        participationStatusRepository.deleteAllByUserId(userId);

        // 좋아요 누른 게시물
        List<BoardLikeStatus> boardLikeStatuses = boardLikeStatusRepository.findAllByUserId(userId);
        boards = new ArrayList<>();

        for (BoardLikeStatus boardLikeStatus : boardLikeStatuses) {
            Board board = boardLikeStatus.getBoard();
            board.cancelLikes();

            boards.add(board);
        }

        if (!boards.isEmpty()) {
            boardRepository.saveAll(boards);
        }

        boardLikeStatusRepository.deleteAllByUserId(userId);

        List<Board> uploadBoards = boardRepository.findAllByUserId(userId);

        if (uploadBoards != null && !uploadBoards.isEmpty()) {
            boardRepository.saveAll(uploadBoards.stream()
                    .map(Board::withdrawalUser)
                    .toList());
        }

        userRepository.delete(user);
    }
}
