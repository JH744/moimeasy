package com.kosa.moimeasy.security.service;

import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.security.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final MemberRepository memberRepository;

    public CustomUserDetailsService(@Lazy MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    // 기존 이메일 기반 메서드
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email: {}", email);
        User user
                = memberRepository.findByEmailWithRole(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.debug("User found: {}", user.getEmail());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getRoleName()))
        );
    }

    // userId 기반으로 사용자 로드하는 메서드
    @Transactional
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        log.debug("Attempting to load user by userId: {}", userId);
        User user = memberRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with userId: {}", userId);
                    return new UsernameNotFoundException("User not found with userId: " + userId);
                });

        log.debug("User found: {}", user.getUserId());

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getUserId()), // userId를 username으로 사용
                user.getPassword(), // 사용자의 비밀번호
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName()))
        );

    }
}
