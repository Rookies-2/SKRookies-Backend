package com.agit.peerflow.security.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(identifier) // email 기준
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // username 대신 email
                user.getPassword(),
                authorities
        );
    }

}
