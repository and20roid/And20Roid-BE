package com.and20roid.backend.service;

import static com.and20roid.backend.common.constant.Constant.BOARD_PARTICIPATION_PENDING;
import static com.and20roid.backend.common.constant.Constant.BOARD_STATE_OPEN;
import static com.and20roid.backend.common.constant.Constant.BOARD_STATE_PENDING;
import static com.and20roid.backend.common.constant.Constant.MESSAGE_TYPE_JOIN;
import static com.and20roid.backend.common.constant.Constant.MESSAGE_TYPE_REQUEST;

import com.and20roid.backend.common.exception.CustomException;
import com.and20roid.backend.common.response.CommonCode;
import com.and20roid.backend.entity.Board;
import com.and20roid.backend.entity.ParticipationInviteStatus;
import com.and20roid.backend.entity.ParticipationStatus;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.BoardRepository;
import com.and20roid.backend.repository.ParticipationInviteStatusRepository;
import com.and20roid.backend.repository.ParticipationStatusRepository;
import com.and20roid.backend.repository.UserInteractionStatusRepository;
import com.and20roid.backend.repository.UserRepository;
import com.and20roid.backend.vo.CreateMessageRequest;
import com.and20roid.backend.vo.ReadBoardWithInviteInfosResponse;
import com.and20roid.backend.vo.ReadParticipantsResponse;
import com.and20roid.backend.vo.ReadRankQuery;
import com.and20roid.backend.vo.ReadRankingResponse;
import com.and20roid.backend.vo.ReadBoardWithInviteInfoQuery;
import com.and20roid.backend.vo.ReadUserInteractionCountQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationService {

    private final ParticipationStatusRepository participationStatusRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final UserInteractionStatusRepository userInteractionStatusRepository;
    private final ParticipationInviteStatusRepository participationInviteStatusRepository;


    private final FcmService fcmService;

    private final MessageSource messageSource;

    @Transactional
    public void createParticipation(Long boardId, String email, Long userId) {
        log.info("start createParticipation by boardId: [{}], userId: [{}]", boardId, userId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_BOARD));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        // 이미 참여한 유저인 경우 예외처리
        if (participationStatusRepository.existsByBoardIdAndUserId(boardId, user.getId())) {
            throw new CustomException(CommonCode.ALREADY_PARTICIPATE_BOARD);
        }

        // 자신의 테스트에 참여하는 경우 예외처리
        if (board.getUser().getId().equals(user.getId())) {
            throw new CustomException(CommonCode.CANNOT_PARTICIPATE_OWN_BOARD);
        }

        // 모집자 수를 다 채운 경우 예외처리
        if (board.getParticipantNum() >= board.getRecruitmentNum()) {
            throw new CustomException(CommonCode.OVER_CAPACITY);
        }

        participationStatusRepository.save(new ParticipationStatus(user, board, BOARD_PARTICIPATION_PENDING, email));
        board.addParticipantNum();

        // 참여자 수 == 모집자 수인 경우 모집 마감으로 상태 변경
        if (board.getParticipantNum() == board.getRecruitmentNum()) {
            board.updateStatus(BOARD_STATE_PENDING);
        }

        boardRepository.save(board);

        fcmService.sendMessage(new CreateMessageRequest(board.getUser().getId(),
                messageSource.getMessage("TITLE_002", null, Locale.getDefault()),
                messageSource.getMessage("CONTENT_002", new String[]{user.getNickname()}, Locale.getDefault()),
                MESSAGE_TYPE_JOIN,
                board
        ));
    }

    public ReadRankingResponse readRanking(long userId) {
        log.info("start readRanking by userId: [{}]", userId);
        List<ReadRankQuery> readRankQueries = participationStatusRepository.readRank();

        List<Long> userIdList = readRankQueries.stream()
                .map(ReadRankQuery::getUserId)
                .toList();

        List<ReadUserInteractionCountQuery> readUserInteractionTesterQueries = userInteractionStatusRepository.readUserInteractionAsTesterCount(
                userId, userIdList);

        Map<Long, Integer> interactionCountAsTesterMap = readUserInteractionTesterQueries
                .stream()
                .collect(Collectors.toMap(ReadUserInteractionCountQuery::getId, ReadUserInteractionCountQuery::getCount));

        List<ReadUserInteractionCountQuery> readUserInteractionUploaderQueries = userInteractionStatusRepository.readUserInteractionAsUploaderCount(
                userId, userIdList);

        Map<Long, Integer> interactionCountAsUploaderMap = readUserInteractionUploaderQueries
                .stream()
                .collect(Collectors.toMap(ReadUserInteractionCountQuery::getId, ReadUserInteractionCountQuery::getCount));

        return new ReadRankingResponse(readRankQueries, interactionCountAsTesterMap, interactionCountAsUploaderMap);
    }

    public ReadBoardWithInviteInfosResponse readBoardWithInviteInfos(Long invitedUserId, long userId, Long lastBoardId, int pageSize) {
        log.info("start readBoardWithInviteInfos by invitedUserId: [{}], userId: [{}]", invitedUserId, userId);

        List<ReadBoardWithInviteInfoQuery> readBoardWithInviteInfoQueries;

        if (lastBoardId == null || lastBoardId < 1) {
            readBoardWithInviteInfoQueries = boardRepository.findBoardsWithInviteInfoByUserId(invitedUserId, userId, pageSize);
        } else {
            readBoardWithInviteInfoQueries = boardRepository.findBoardsWithInviteInfoByUserId(invitedUserId, userId, lastBoardId, pageSize);
        }

        return new ReadBoardWithInviteInfosResponse(readBoardWithInviteInfoQueries);
    }

    public void createParticipateInvite(Long boardId, Long userId, Long invitedUserId) {
        log.info("start createParticipation by boardId: [{}], userId: [{}], invitedUserId: [{}]", boardId, userId, invitedUserId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        User invitedUser = userRepository.findById(invitedUserId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_BOARD));

        // 모집 중이 아닌 경우 예외처리
        if (!board.getState().equals(BOARD_STATE_OPEN)) {
            throw new CustomException(CommonCode.INVALID_BOARD_STATE_FOR_INVITE);
        }

        // 이미 참여한 경우 예외처리
        if (participationStatusRepository.existsByBoardIdAndUserId(board.getId(), invitedUserId)) {
            throw new CustomException(CommonCode.ALREADY_PARTICIPATE_USER);
        }

        // 이미 초대한 경우 예외처리
        if (participationInviteStatusRepository.existsByBoardIdAndUserId(board.getId(), invitedUserId)) {
            throw new CustomException(CommonCode.ALREADY_INVITED_USER);
        }

        fcmService.sendMessage(new CreateMessageRequest(invitedUserId,
                messageSource.getMessage("TITLE_001", null, Locale.getDefault()),
                messageSource.getMessage("CONTENT_001", new String[]{user.getNickname()}, Locale.getDefault()),
                MESSAGE_TYPE_REQUEST,
                board));

        participationInviteStatusRepository.save(new ParticipationInviteStatus(board, invitedUser));
    }

    public ReadParticipantsResponse readParticipants(Long boardId) {
        log.info("start readParticipant by boardId: [{}]", boardId);

        List<ParticipationStatus> participationStatuses = participationStatusRepository.findByBoardId(boardId);

        return new ReadParticipantsResponse(participationStatuses);
    }
}
