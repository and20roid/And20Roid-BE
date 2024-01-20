package com.and20roid.backend.repository;

import com.and20roid.backend.entity.FcmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmMessageRepository extends JpaRepository<FcmMessage, Long> {
}
