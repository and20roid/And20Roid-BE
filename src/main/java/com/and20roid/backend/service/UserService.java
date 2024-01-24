package com.and20roid.backend.service;

import static com.and20roid.backend.common.constant.Constant.BOARD_PARTICIPATION_COMPLETED;

import com.and20roid.backend.common.exception.CustomException;
import com.and20roid.backend.common.response.CommonCode;
import com.and20roid.backend.entity.Authority;
import com.and20roid.backend.entity.FcmToken;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.AuthorityRepository;
import com.and20roid.backend.repository.BoardRepository;
import com.and20roid.backend.repository.FcmTokenRepository;
import com.and20roid.backend.repository.ParticipationStatusRepository;
import com.and20roid.backend.repository.UserRepository;
import com.and20roid.backend.vo.ReadRankQuery;
import com.and20roid.backend.vo.ReadUserTestingStats;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private static final String ROLE_USER = "ROLE_USER";

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final BoardRepository boardRepository;
    private final ParticipationStatusRepository participationStatusRepository;
    private final FcmTokenRepository fcmTokenRepository;

    public String signup(String uid, String nickname) {
        log.info("start signup by uid[{}], nickname[{}]", uid, nickname);

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

    public ReadUserTestingStats readUserTestingStats(Long userId) {
        log.info("start readUserTestingStats by userId[{}]", userId);

        int completedTestCount = 0;
        Integer rank = null;
        String nickname = null;

        int uploadBoardCount = boardRepository.countByUserId(userId);
        ReadRankQuery readRankQuery = participationStatusRepository.readRankAndCompletedTestCountByUserId(userId);

        if (readRankQuery != null) {
            completedTestCount = readRankQuery.getCompletedTestCount();
            rank = readRankQuery.getRank();
            nickname = readRankQuery.getNickname();
        }

        return new ReadUserTestingStats(completedTestCount, uploadBoardCount, rank, nickname);
    }

    public void createFcmToken(String token, long userId) {
        log.info("start createFcmToken by token[{}], userId[{}]", token, userId);

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
        log.info("start deleteFcmToken by userId[{}]", userId);

        fcmTokenRepository.deleteAllByUserId(userId);
    }

    public void updateUser(String nickname, long userId) {
        log.info("start updateUser by nickname: [{}], userId: [{}]", nickname, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(CommonCode.ALREADY_EXIST_NICKNAME);
        }

        user.updateNickname(nickname);

        userRepository.save(user);
    }
}
