package com.dv.dancingvegetables.configuration;

import com.dv.dancingvegetables.jwt.JwtFilter;
import com.dv.dancingvegetables.jwt.TokenProvider;

import com.dv.dancingvegetables.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfiguration  extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final String SECRET_KEY;
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        JwtFilter customJwtFilter = new JwtFilter(SECRET_KEY, tokenProvider, userDetailsService);
        httpSecurity.addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}