package com.and20roid.backend.repository;

import com.and20roid.backend.entity.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<ParticipationStatus, Long> {

    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

}
