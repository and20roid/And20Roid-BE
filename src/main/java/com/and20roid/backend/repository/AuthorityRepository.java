package com.and20roid.backend.repository;

import com.and20roid.backend.entity.Authority;
import com.and20roid.backend.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    List<Authority> findAllByUser(User user);

}
