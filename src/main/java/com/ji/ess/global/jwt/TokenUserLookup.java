package com.ji.ess.global.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface TokenUserLookup {
    UserDetails loadUserByLoginId(String loginId) throws UsernameNotFoundException;
}
