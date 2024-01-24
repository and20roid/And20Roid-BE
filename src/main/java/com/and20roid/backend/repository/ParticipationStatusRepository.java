package com.and20roid.backend.repository;

import com.and20roid.backend.entity.ParticipationStatus;
import com.and20roid.backend.vo.ReadRankQuery;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipationStatusRepository extends JpaRepository<ParticipationStatus, Long> {

    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    @Query(value = "SELECT user_id as userId, nickname, completedTestCount, " +
            "RANK() OVER (ORDER BY completedTestCount DESC) AS 'rank' " +
            "FROM ( " +
            "   SELECT p.user_id, u.nickname, COUNT(*) AS completedTestCount " +
            "   FROM participation_status p " +
            "   LEFT JOIN user u ON p.user_id = u.id " +
            "   WHERE p.status = '테스트_완료' " +
            "   GROUP BY p.user_id, u.nickname " +
            ") AS subquery " +
            "ORDER BY completedTestCount DESC " +
            "LIMIT 50", nativeQuery = true)
    List<ReadRankQuery> readRank();

    @Query(value = "SELECT * FROM ( "
            + "SELECT user_id as userId, nickname, completedTestCount, RANK() OVER (ORDER BY completedTestCount DESC) AS 'rank' " +
            "FROM ( " +
            "   SELECT p.user_id, u.nickname, COUNT(*) AS completedTestCount " +
            "   FROM participation_status p " +
            "   LEFT JOIN user u ON p.user_id = u.id " +
            "   WHERE p.status = '테스트_완료' " +
            "   GROUP BY p.user_id, u.nickname " +
            ") AS completedTestQuery " +
            ") AS rankQuery " +
            "where userId = :userId", nativeQuery = true)
    ReadRankQuery readRankAndCompletedTestCountByUserId(@Param("userId") Long userId);

//    int countByUserIdAndStatus(Long userId, String status);

//    List<ParticipationStatus> findByUserIdAndStatus(Long userId, String status);

    List<ParticipationStatus> findByUserId(Long userId);

    List<ParticipationStatus> findByBoardId(Long boardId);
}
