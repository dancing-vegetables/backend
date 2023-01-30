package com.dv.dancingvegetables.controller;
import com.dv.dancingvegetables.domain.UserDetailsImpl;
import com.dv.dancingvegetables.dto.requestdto.LoginRequestDto;
import com.dv.dancingvegetables.dto.requestdto.MemberRequestDto;
import com.dv.dancingvegetables.dto.responsedto.ResponseDto;
import com.dv.dancingvegetables.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/members")
public class memberController {
    private final MemberService memberService;

    //회원가입
    @PostMapping(value="/signup")
    public ResponseDto<?> signup(@RequestBody @Valid MemberRequestDto requestDto) throws IOException {
        return memberService.createMember(requestDto);
    }

    //로그인
    @PostMapping(value = "/login")
    public ResponseDto<?> login(@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse response) {
        return memberService.login(requestDto, response);
    }

    //로그아웃
    @PostMapping(value = "/logout")
    public ResponseDto<?> logout(HttpServletRequest request) {
        return memberService.logout(request);
    }

    //토큰재발급
    @PostMapping(value = "/reissue")
    public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return memberService.reissue(request, response);
    }

    //회원탈퇴
    @DeleteMapping(value="/withdrawl/{memberId}")
    public ResponseDto<?> withdrawal(@PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memberService.withdrawMember(memberId, userDetails);
    }
}
