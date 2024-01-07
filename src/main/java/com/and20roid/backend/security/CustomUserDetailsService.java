package com.and20roid.backend.security;

import com.and20roid.backend.entity.Authority;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.AuthorityRepository;
import com.and20roid.backend.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    /**
     * 파이어베이스 user_id를 기반으로 User 객체 생성 후 UserDetails 객체 반환하는 메서드
     */
    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        User user = userRepository.findByToken(uid)
                .orElseThrow(() -> new UsernameNotFoundException(uid + " -> 데이터베이스에 존재하지 않습니다."));

        List<Authority> authorities = authorityRepository.findAllByUser(user);

        log.info("authorities: [{}]", authorities.toString());

        return null;

    }
}
