package com.dv.dancingvegetables.service;
import com.dv.dancingvegetables.domain.UserDetailsImpl;
import com.dv.dancingvegetables.dto.responsedto.EmailAuthResponseDto;
import com.dv.dancingvegetables.dto.responsedto.NicknameAuthResponseDto;
import com.dv.dancingvegetables.dto.requestdto.*;
import com.dv.dancingvegetables.dto.responsedto.MemberResponseDto;
import com.dv.dancingvegetables.dto.responsedto.ResponseDto;
import com.dv.dancingvegetables.domain.Member;
import com.dv.dancingvegetables.jwt.TokenProvider;
import com.dv.dancingvegetables.repository.MemberRepository;
import com.dv.dancingvegetables.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;

import java.io.IOException;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입 전 닉네임 인증 체크
    @Transactional
    public ResponseDto<?> nickname(@Valid NicknameAuthRequestDto requestDto) {
        if(null != isPresentNickname(requestDto.getNickname())) {
            return ResponseDto.fail("DUPLICATED_NICKNAME","중복된 닉네임입니다.");
        }
        if (requestDto.getNickname().equals("")) {
            return ResponseDto.success("닉네임을 입력해주세요.");
        }
        Member member = Member.builder()
                .nickname(requestDto.getNickname())
                .build();
        return ResponseDto.success(
                    NicknameAuthResponseDto.builder()
                        .nickname(member.getNickname())
                        .build()
        );
    }

    // 닉네임 인증
    @Transactional
    public Object isPresentNickname(String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        return optionalMember.orElse(null);
    }

    // 이메일 인증
    @Transactional
    public ResponseDto<?> emailAuth(EmailAuthRequestDto requestDto) {
        //이메일 중복 체크
        if (null != isPresentMember(requestDto.getEmail())) {
            return ResponseDto.fail("DUPLICATED_EMAIL",
                    "중복된 이메일입니다.");
        }

        if(!requestDto.getEmail().contains("@")) {
            return ResponseDto.fail("INVALID_EMAIL",
                    "이메일 형식을 확인해주세요.");
        }

        if (requestDto.getEmail().equals("")) {
            return ResponseDto.success("이메일을 입력해주세요.");
        }

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .build();
        return ResponseDto.success(
                EmailAuthResponseDto.builder()
                        .email(member.getEmail())
                        .build()
        );
    }

    // 회원가입
    @Transactional
    public ResponseDto<?> createMember(MemberRequestDto requestDto) throws IOException {

        //이메일 중복 체크
        if (null != isPresentMember(requestDto.getEmail())) {
            return ResponseDto.fail("DUPLICATED_EMAIL",
                    "중복된 이메일 입니다.");
        }

        // 이메일 형식 체크
        if(!requestDto.getEmail().contains("@")) {
            return ResponseDto.fail("INVALID_EMAIL",
                    "이메일 형식을 확인해주세요.");
        }

        // 닉네임 중복 체크
        if(null != isPresentNickname(requestDto.getNickname())) {
            return ResponseDto.fail("INVALID_NICKNAME",
                    "중복된 닉네임입니다.");
        }

        //패스워드 일치 체크
        if(!Objects.equals(requestDto.getPasswordConfirm(), requestDto.getPassword())){
            return ResponseDto.fail("PASSWORD_CONFIRM_FAIL",
                    "패스워드가 일치하지 않습니다.");
        }

        Member member = Member.builder()
                .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .email(requestDto.getEmail())
                .address(requestDto.getAddress())
                .build();
        memberRepository.save(member);

        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(member.getId())
                        .nickname(member.getNickname())
                        .createdAt(member.getCreatedAt())
                        .modifiedAt(member.getModifiedAt())
                        .email(member.getEmail())
                        .address(member.getAddress())
                        .build()
        );
    }

    // 로그인
    @Transactional
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        Member member = isPresentMember(requestDto.getEmail());

        // null값 사용자 유효성 체크
        if (null == member) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "이메일 혹은 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 사용자 유효성 체크
        if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
            return ResponseDto.fail("INVALID_MEMBER", "이메일 혹은 비밀번호가 일치하지 않습니다.");
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(member.getId())
                        .nickname(member.getNickname())
                        .createdAt(member.getCreatedAt())
                        .modifiedAt(member.getModifiedAt())
                        .address(member.getAddress())
                        .email(member.getEmail())
                        .build()
        );
    }

    // 회원 이메일 유효성 인증
    @Transactional
    public Member isPresentMember(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElse(null);
    }

    // 로그아웃
    public ResponseDto<?> logout(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        Member member = (Member) tokenProvider.getMemberFromAuthentication();

        if (null == member) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "이메일 혹은 비밀번호가 일치하지 않습니다.");
        }
        return tokenProvider.deleteRefreshToken(member);
    }

    // 헤더에 담기는 토큰
    private void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh_Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

    // 리이슈
    public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Member member = refreshTokenRepository.findByValue(request.getHeader("Refresh_Token")).get().getMember();

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenToHeaders(tokenDto, response);
        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(member.getId())
                        .nickname(member.getNickname())
                        .createdAt(member.getCreatedAt())
                        .address(member.getAddress())
                        .modifiedAt(member.getModifiedAt())
                        .email(member.getEmail())
                        .build()
        );
    }

    // 회원 탈퇴
    @Transactional
    public ResponseDto<?> withdrawMember(Long memberId, UserDetailsImpl userDetails) {
        Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                () ->new IllegalArgumentException("등록되지 않은 회원입니다.")
        );

        refreshTokenRepository.deleteByMemberId(memberId);
        memberRepository.deleteById(memberId);

        return ResponseDto.success("회원 탈퇴가 완료되었습니다.");
    }
}