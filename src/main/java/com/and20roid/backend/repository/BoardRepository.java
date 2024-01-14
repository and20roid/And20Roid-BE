package com.and20roid.backend.repository;

import com.and20roid.backend.entity.Board;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByIdLessThan(Long lastBoardId, PageRequest pageRequest);

    int countByUserId(Long userId);

    Page<Board> findByIdLessThanAndUserId(Long lastBoardId, Long userId, PageRequest pageRequest);

    Page<Board> findByUserId(Long userId, PageRequest pageRequest);

    Page<Board> findByIdIn(List<Long> boardIdList, PageRequest pageRequest);

    Page<Board> findByIdLessThanAndIdIn(Long lastBoardId, List<Long> boardIdList, PageRequest pageRequest);
}
