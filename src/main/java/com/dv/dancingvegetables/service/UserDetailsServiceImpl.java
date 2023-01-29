package com.dv.dancingvegetables.service;

import com.dv.dancingvegetables.domain.Member;
import com.dv.dancingvegetables.domain.UserDetailsImpl;
import com.dv.dancingvegetables.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findByNickname(nickname);
        return member
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("이메일 혹은 비밀번호가 일치하지 않습니다."));
    }
}