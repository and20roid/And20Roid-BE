package com.and20roid.backend.service;

import static com.and20roid.backend.common.constant.Constant.BOARD_PARTICIPATION_PENDING;

import com.and20roid.backend.common.exception.CustomException;
import com.and20roid.backend.common.response.CommonCode;
import com.and20roid.backend.entity.Board;
import com.and20roid.backend.entity.ParticipationStatus;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.BoardRepository;
import com.and20roid.backend.repository.ParticipationRepository;
import com.and20roid.backend.repository.UserInteractionStatusRepository;
import com.and20roid.backend.repository.UserRepository;
import com.and20roid.backend.vo.ReadRankQuery;
import com.and20roid.backend.vo.ReadRankingResponse;
import com.and20roid.backend.vo.ReadUserInteractionCountQuery;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final UserInteractionStatusRepository userInteractionStatusRepository;

    public void createParticipation(Long boardId, String email, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_BOARD));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        if (participationRepository.existsByBoardIdAndUserId(boardId, user.getId())) {
            throw new CustomException(CommonCode.ALREADY_PARTICIPATE_BOARD);
        }

        participationRepository.save(new ParticipationStatus(user, board, BOARD_PARTICIPATION_PENDING, email));
    }

    public ReadRankingResponse readRanking(long userId) {
        List<ReadRankQuery> readRankQueries = participationRepository.readRank();

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
}
