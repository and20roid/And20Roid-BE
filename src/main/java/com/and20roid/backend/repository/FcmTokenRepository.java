package com.and20roid.backend.repository;

import com.and20roid.backend.entity.FcmToken;
import com.and20roid.backend.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    @Modifying
    @Query(value = "delete from FcmToken where user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    Optional<FcmToken> findByUserId(Long userId);

    boolean existsByUserIdAndToken(Long userId, String token);
}
