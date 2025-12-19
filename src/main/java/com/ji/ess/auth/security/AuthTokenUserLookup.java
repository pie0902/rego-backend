package com.ji.ess.auth.security;

import com.ji.ess.global.jwt.TokenUserLookup;
import com.ji.ess.user.entity.User;
import com.ji.ess.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenUserLookup implements TokenUserLookup, UserDetailsService {

    private final UserRepository userRepository;

    public AuthTokenUserLookup(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByLoginId(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + loginId));
        return new CustomUserDetails(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByLoginId(username);
    }
}
