package com.and20roid.backend.repository;

import com.and20roid.backend.entity.AppIntroductionImage;
import com.and20roid.backend.entity.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppIntroductionImageRepository extends JpaRepository<AppIntroductionImage, Long> {

    List<AppIntroductionImage> findAllByBoard(Board board);

    @Modifying
    @Query("delete from AppIntroductionImage a where a.board.user.id = :userId")
    void deleteAllByUserId(@Param("userId") long userId);
}
