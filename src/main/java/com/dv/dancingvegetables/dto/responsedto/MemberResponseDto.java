package com.dv.dancingvegetables.dto.responsedto;

//import com.example.demo.shared.Authority;
import com.dv.dancingvegetables.shared.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto  {
    private Long id;
    private Long memberId;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String email;
    private String address;
    private Authority role;
}