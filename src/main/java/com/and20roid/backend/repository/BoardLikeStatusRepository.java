package com.and20roid.backend.repository;

import com.and20roid.backend.entity.BoardLikeStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardLikeStatusRepository extends JpaRepository<BoardLikeStatus, Long> {

    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    void deleteByBoardIdAndUserId(Long boardId, Long userId);

    @Modifying
    @Query(value = "delete from BoardLikeStatus where user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    List<BoardLikeStatus> findAllByUserId(long userId);
}
