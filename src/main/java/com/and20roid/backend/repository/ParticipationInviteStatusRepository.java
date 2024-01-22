package com.and20roid.backend.repository;

import com.and20roid.backend.entity.ParticipationInviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationInviteStatusRepository extends JpaRepository<ParticipationInviteStatus, Long> {
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
}
