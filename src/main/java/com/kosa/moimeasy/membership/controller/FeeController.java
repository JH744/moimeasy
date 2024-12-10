//package com.kosa.moimeasy.membership.controller;
//
//import com.kosa.moimeasy.membership.dto.PayDTO;
//import com.kosa.moimeasy.membership.entity.Fee;
//import com.kosa.moimeasy.membership.service.FeeService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/vi/membership") // 기본 경로
//@CrossOrigin(origins = "http://localhost:3000") // Axis 요청
//public class FeeController {
//
//    private final FeeService feeService;
//
//    // 회비 납부
//    @PostMapping("/pay")
//    public String payFee(@RequestBody PayDTO payDTO){
//        feeService.saveFee(payDTO);
//        return "회비 납부";
//    }
//
//    @GetMapping("/feeList")
//    public List<Fee> getFeeList(){
//        return feeService
//    }
//}
