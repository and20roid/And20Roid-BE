package com.and20roid.backend.repository;

import com.and20roid.backend.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByIdLessThan(Long lastBoardId, PageRequest pageRequest);
}
