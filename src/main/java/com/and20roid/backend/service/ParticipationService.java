package com.and20roid.backend.service;

import com.and20roid.backend.common.exception.CustomException;
import com.and20roid.backend.common.response.CommonCode;
import com.and20roid.backend.entity.Board;
import com.and20roid.backend.entity.ParticipationStatus;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.BoardRepository;
import com.and20roid.backend.repository.ParticipationRepository;
import com.and20roid.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public void createParticipation(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_BOARD));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        if (participationRepository.existsByBoardIdAndUserId(boardId, user.getId())) {
            throw new CustomException(CommonCode.ALREADY_PARTICIPATE_BOARD);
        }


        participationRepository.save(new ParticipationStatus(user, board, false));
    }
}
