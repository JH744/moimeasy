//package com.kosa.moimeasy.transaction.controller;
//
//import com.kosa.moimeasy.transaction.dto.TransferRequestDTO;
//import com.kosa.moimeasy.transaction.dto.response.ListResponseResult;
//import com.kosa.moimeasy.transaction.dto.response.ResponseResult;
//import com.kosa.moimeasy.transaction.service.TransactionServiceSamle;
//import com.kosa.moimeasy.transaction.service.TransferService;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//@ApiResponses({
//        @ApiResponse(responseCode = "200", description = "응답이 성공적으로 반환되었습니다."),
//        @ApiResponse(responseCode = "400", description = "응답이 실패하였습니다.",
//                content = @Content(schema = @Schema(implementation = ResponseResult.class)))})
//@Slf4j
//@Tag(name = "Transaction Controller", description = "거래내역 컨트롤러")
//@RequiredArgsConstructor
//@RestController
//@RequestMapping(value = "/api/v1/transaction")
//public class TransactionControllerSample {
//
//    private final TransactionServiceSamle transactionService;
//    private final TransferService transferService;
//
//
//    @PostMapping("/pay")
//    public ResponseResult executeTransfer(@RequestBody @Valid TransferRequestDTO transferRequestDTO) {
//        try {
//            log.info("TransferController -> Initiating transfer for userId: {} and moeimId: {}",
//                    transferRequestDTO.getUserId(), transferRequestDTO.getMoeimId());
//            transferService.executeTransfer(transferRequestDTO);
//            return ResponseResult.successResponse;
//        } catch (Exception e) {
//            log.error("TransferController -> Error occurred during transfer: {}", e.getMessage(), e);
//            return ResponseResult.failResponse;
//        }
//    }
//
//    @GetMapping("{moeimId}")
//    public ResponseResult getAllTransaction(@Valid @PathVariable Long moeimId) {
//        return new ListResponseResult<>(transactionService.findByMoeimId(moeimId));
//    }
//
//    @GetMapping("/{moeimId}/{idx}")
//    public ResponseResult getTransactionList(
//            @PathVariable(name = "moeimId") Long moeimId,
//            @PathVariable(name = "idx") int idx) {
//        log.info("모임 계좌 거래 내역 조회");
//        return new ListResponseResult<>(transactionService.getTransactionList(moeimId, idx));
//    }
//
//    @GetMapping("/category/{moeimId}")
//    public ResponseResult getTransactionListByCategory(
//            @PathVariable(name = "moeimId") Long moeimId,
//            @RequestParam(name = "year") int year,
//            @RequestParam(name = "month") int month) {
//        log.info("모임 계좌의 카테고리별 소비내역 조회");
//        return new ListResponseResult<>(transactionService.getTransactionListByCategory(moeimId, year, month));
//    }
//
//}
