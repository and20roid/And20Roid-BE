package com.and20roid.backend.repository;

import com.and20roid.backend.entity.UserInteractionStatus;
import com.and20roid.backend.vo.ReadUserInteractionCountQuery;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInteractionStatusRepository extends JpaRepository<UserInteractionStatus, Long> {

    @Query("SELECT u.uploader.id as id, count(*) as count FROM UserInteractionStatus u " +
            "WHERE (u.uploader.id IN :userIdList AND u.tester.id = :userId)"
            + "group by u.uploader.id")
    List<ReadUserInteractionCountQuery> readUserInteractionAsTesterCount(@Param("userId") Long userId, @Param("userIdList") List<Long> userIdList);

    @Query("SELECT u.tester.id as id, count(*) as count FROM UserInteractionStatus u " +
            "WHERE (u.uploader.id = :userId AND u.tester.id IN :userIdList)"
            + "group by u.tester.id")
    List<ReadUserInteractionCountQuery> readUserInteractionAsUploaderCount(@Param("userId") Long userId, @Param("userIdList") List<Long> userIdList);

}
