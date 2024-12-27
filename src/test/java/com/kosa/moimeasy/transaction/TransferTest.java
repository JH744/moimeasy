//package com.kosa.moimeasy.transaction;
//
//import com.kosa.moimeasy.moeim.entity.Moeim;
//import com.kosa.moimeasy.moeim.repository.MoeimRepository;
//import com.kosa.moimeasy.transaction.dto.TransferRequestDTO;
//import com.kosa.moimeasy.transaction.entity.TransactionSample;
//import com.kosa.moimeasy.transaction.repository.TransactionRepositorySample;
//import com.kosa.moimeasy.transaction.service.TransferService;
//import com.kosa.moimeasy.user.entity.User;
//import com.kosa.moimeasy.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class TransferServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private MoeimRepository moeimRepository;
//
//    @Mock
//    private TransactionRepositorySample transactionRepositorySample;
//
//    @InjectMocks
//    private TransferService transferService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void executeTransfer_successfulTransfer() {
//        // Given: Mock 데이터 설정
//        Long userId = 1L;
//        Long moeimId = 2L;
//        double transferAmount = 1000;
//
//        User user = new User();
//        user.setUserId(userId);
//        user.setBalance(5000);
//
//        Moeim moeim = new Moeim();
//        moeim.setMoeimId(moeimId);
//        moeim.setBalance(2000);
//
//        TransferRequestDTO requestDTO = new TransferRequestDTO();
//        requestDTO.setUserId(userId);
//        requestDTO.setMoeimId(moeimId);
//        requestDTO.setTransferAmount(transferAmount);
//        requestDTO.setContent("Test Transfer");
//
//        // Mock 리포지토리 동작
//        when(userRepository.findAccountNumberById(userId)).thenReturn(Optional.of(user));
//        when(moeimRepository.findAccountNumberById(moeimId)).thenReturn(Optional.of(moeim));
//
//        // When: 이체 실행
//        transferService.executeTransfer(requestDTO);
//
//        // Then: 출금 및 입금 확인
//        verify(userRepository, atLeastOnce()).findAccountNumberById(userId); // 최소 1번 이상 호출 확인
//        verify(moeimRepository, atLeastOnce()).findAccountNumberById(moeimId);
//
//        System.out.println("User Balance: " + user.getBalance());
//        System.out.println("Moeim Balance: " + moeim.getBalance());
//        // 회원 잔액 감소
//        assert(user.getBalance() == 4000);
//        // 모임 잔액 증가
//        assert(moeim.getBalance() == 3000);
//
//        // 거래 내역 저장 확인
//        verify(transactionRepositorySample, atLeastOnce()).save(any(TransactionSample.class));
//    }
//
//    @Test
//    void executeTransfer_userNotFound() {
//        // Given: Mock 데이터 설정
//        Long userId = 1L;
//        Long moeimId = 2L;
//        double transferAmount = 1000;
//
//        TransferRequestDTO requestDTO = new TransferRequestDTO();
//        requestDTO.setUserId(userId);
//        requestDTO.setMoeimId(moeimId);
//        requestDTO.setTransferAmount(transferAmount);
//
//        // Mock 동작: 회원 계좌 없음
//        when(userRepository.findAccountNumberById(userId)).thenReturn(Optional.empty());
//
//        // When & Then: 예외 발생 확인
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
//                transferService.executeTransfer(requestDTO)
//        );
//
//        // Exception 메시지 검증 (필요시 메시지 추가)
//        verify(userRepository, atLeastOnce()).findAccountNumberById(userId);
//        verifyNoInteractions(moeimRepository); // 모임 계좌와 거래 내역 저장은 호출되지 않아야 함
//        verifyNoInteractions(transactionRepositorySample);
//    }
//}
