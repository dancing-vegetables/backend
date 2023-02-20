package com.dv.dancingvegetables.service;

import com.dv.dancingvegetables.domain.Lank;
import com.dv.dancingvegetables.domain.Member;
import com.dv.dancingvegetables.dto.requestdto.LankDto;
import com.dv.dancingvegetables.dto.responsedto.ResponseDto;
import com.dv.dancingvegetables.repository.LankRepository;
import com.dv.dancingvegetables.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class LankService {
    private final MemberRepository memberRepository;
    private final LankRepository lankRepository;

    //랭크 포인트 갱신 밑 저장
    @Transactional
    public ResponseDto<?> lankPointSave(LankDto lankPoint) {
        Member member = memberRepository.findById(lankPoint.getMemberId()).orElse(null);
        Lank lankIsEmpty = lankRepository.findByMember(member).orElse(null);
        if (lankIsEmpty==null) {
            Lank lank = Lank.builder()
                    .member(member)
                    .lankPoint(lankPoint.getLankPoint())
                    .build();
            lankRepository.save(lank);
        } else {
            System.out.println(Objects.requireNonNull(lankIsEmpty).getLankPoint());
            System.out.println("업데이트");
            System.out.println("새로운 랭크 포인트 " + lankPoint.getLankPoint());

            lankIsEmpty.update(lankPoint.getLankPoint());
        }
        return ResponseDto.success("success");
    }

    //랭킹 포인트 순위 10위
    public ResponseDto<?> lankPoint(Long id){
        ArrayList<Lank> lankTen=lankRepository.LankTen().orElse(null);

        return ResponseDto.success(lankTen);
    }
}
