package com.and20roid.backend.repository;

import com.and20roid.backend.entity.AppIntroductionImage;
import com.and20roid.backend.entity.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppIntroductionImageRepository extends JpaRepository<AppIntroductionImage, Long> {

    List<AppIntroductionImage> findAllByBoard(Board board);

}
