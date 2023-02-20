package com.dv.dancingvegetables.controller;

import com.dv.dancingvegetables.dto.requestdto.LankDto;
import com.dv.dancingvegetables.dto.responsedto.ResponseDto;
import com.dv.dancingvegetables.service.LankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/lanks")
public class LankController {
        private final LankService lankService;

        //랭크 포인트 갱신 및 저장
    @PostMapping("/points")
    public ResponseDto<?> lankPointSave(@RequestBody LankDto lankPoint){
        return lankService.lankPointSave(lankPoint);
    }
    
    //랭킹 10위 까지 추출
    @GetMapping("/points/{id}")
    public ResponseDto<?>LankPoints(@PathVariable Long id){
        return lankService.lankPoint(id);
    }
}
