package com.and20roid.backend.service;

import com.and20roid.backend.entity.Board;
import com.and20roid.backend.entity.ParticipationStatus;
import com.and20roid.backend.repository.BoardRepository;
import com.and20roid.backend.repository.ParticipationStatusRepository;
import com.and20roid.backend.vo.ReadParticipateBoardsResponse;
import com.and20roid.backend.vo.ReadUploadBoardsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final BoardRepository boardRepository;
    private final ParticipationStatusRepository participationStatusRepository;

    public ReadUploadBoardsResponse readUploadBoards(Long lastBoardId, int pageSize, long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(0, pageSize, sort);

        Page<Board> page = null;

        if (lastBoardId == null || lastBoardId < 1) {
            page = boardRepository.findByUserId(userId, pageRequest);
        } else {
            page = boardRepository.findByIdLessThanAndUserId(lastBoardId, userId, pageRequest);
        }

        return new ReadUploadBoardsResponse(page.getContent());

    }

    public ReadParticipateBoardsResponse readParticipateBoards(Long lastBoardId, int pageSize, long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(0, pageSize, sort);

        Page<Board> page = null;

//        List<ParticipationStatus> participationStatuses = participationRepository.findByUserIdAndStatus(userId,
//                BOARD_PARTICIPATION_IN_PROGRESS);

        List<ParticipationStatus> participationStatuses = participationStatusRepository.findByUserId(userId);

        List<Long> participateBoardIdList = participationStatuses.stream()
                .map(participationStatus -> participationStatus.getBoard().getId())
                .toList();

        if (lastBoardId == null || lastBoardId < 1) {
            page = boardRepository.findByIdIn(participateBoardIdList, pageRequest);
        } else {
            page = boardRepository.findByIdLessThanAndIdIn(lastBoardId, participateBoardIdList, pageRequest);
        }

        return new ReadParticipateBoardsResponse(page.getContent());
    }
}
