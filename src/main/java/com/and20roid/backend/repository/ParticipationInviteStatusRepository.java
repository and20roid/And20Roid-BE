package com.and20roid.backend.repository;

import com.and20roid.backend.entity.ParticipationInviteStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipationInviteStatusRepository extends JpaRepository<ParticipationInviteStatus, Long> {
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    @Modifying
    @Query(value = "delete from ParticipationInviteStatus where user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    List<ParticipationInviteStatus> findAllByUserId(long userId);
}
