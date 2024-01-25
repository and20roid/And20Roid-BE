package com.and20roid.backend.repository;

import com.and20roid.backend.entity.FcmMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FcmMessageRepository extends JpaRepository<FcmMessage, Long> {

    Page<FcmMessage> findByUserId(long userId, PageRequest pageRequest);
    Page<FcmMessage> findByIdLessThanAndUserId(Long lastMessageId, Long userId, PageRequest pageRequest);

    @Modifying
    @Query("delete from FcmMessage f where f.userId = :userId")
    void deleteAllByUserId(@Param("userId") long userId);
}
