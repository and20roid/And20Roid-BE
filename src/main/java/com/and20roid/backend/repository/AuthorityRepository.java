package com.and20roid.backend.repository;

import com.and20roid.backend.entity.Authority;
import com.and20roid.backend.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    List<Authority> findAllByUser(User user);

    @Modifying
    @Query("delete from Authority a where a.user.id = :userId")
    void deleteAllByUserId(@Param("userId") long userId);
}
