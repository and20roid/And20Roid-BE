package com.and20roid.backend.security;

import com.and20roid.backend.entity.Authority;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.AuthorityRepository;
import com.and20roid.backend.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new UsernameNotFoundException(uid + " -> 데이터베이스에 존재하지 않습니다."));

        List<Authority> authorities = authorityRepository.findAllByUser(user);

        return createUser(user, authorities);
    }

    private org.springframework.security.core.userdetails.User createUser(User user, List<Authority> authorities) {
        List<GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getId().toString(),
                "",
                grantedAuthorities);
    }
}
