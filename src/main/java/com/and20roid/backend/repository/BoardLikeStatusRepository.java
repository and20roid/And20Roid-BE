package com.and20roid.backend.repository;

import com.and20roid.backend.entity.BoardLikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeStatusRepository extends JpaRepository<BoardLikeStatus, Long> {

    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    void deleteByBoardIdAndUserId(Long boardId, Long userId);
}
