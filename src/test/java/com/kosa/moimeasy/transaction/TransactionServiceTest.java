//package com.kosa.moimeasy.transaction;
//
//import com.kosa.moimeasy.moeim.entity.Moeim;
//import com.kosa.moimeasy.moeim.repository.MoeimRepository;
//import com.kosa.moimeasy.security.provider.JwtTokenProvider;
//import com.kosa.moimeasy.transaction.dto.DepositDto;
//import com.kosa.moimeasy.transaction.dto.RemittanceDto;
//import com.kosa.moimeasy.transaction.dto.WithdrawDto;
//import com.kosa.moimeasy.transaction.entity.Transaction;
//import com.kosa.moimeasy.transaction.exception.CustomException;
//import com.kosa.moimeasy.transaction.repository.TransactionRepository;
//import com.kosa.moimeasy.transaction.service.TransactionService;
//import com.kosa.moimeasy.transaction.type.ErrorCode;
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
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class TransactionServiceTest {
//
//    @InjectMocks
//    private TransactionService transactionService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private MoeimRepository moeimRepository;
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @Mock
//    private JwtTokenProvider tokenProvider;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void deposit_ValidAccount_ShouldSucceed() {
//        // Given
//        DepositDto.Request request = new DepositDto.Request() {{
//            setAccountNumber("12345678");
//            setAmount(1000L);
//            setDepositName("John Doe");
//        }};
//
//        Moeim mockMoeim = mock(Moeim.class);
//        when(mockMoeim.getAccountNumber()).thenReturn("12345678");
//        when(mockMoeim.getAmount()).thenReturn((double) 5000L);
//
//        when(moeimRepository.findByAccountNumber("12345678"))
//                .thenReturn(Optional.of(mockMoeim));
//
//        // When
//        DepositDto.Response response = transactionService.deposit(request);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("12345678", response.getAccountNumber());
//        assertEquals(1000L, response.getAmount());
//        verify(transactionRepository, times(1)).save(any(Transaction.class));
//    }
//
//    @Test
//    void deposit_InvalidAccount_ShouldThrowException() {
//        // Given
//        DepositDto.Request request = new DepositDto.Request() {{
//            setAccountNumber("12345678");
//            setAmount(1000L);
//            setDepositName("John Doe");
//        }};
//
//        when(moeimRepository.findByAccountNumber("12345678"))
//                .thenReturn(Optional.empty());
//
//        // When
//        CustomException exception = assertThrows(CustomException.class, () ->
//                transactionService.deposit(request));
//
//        // Then
//        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//
//    @Test
//    void withdraw_InsufficientBalance_ShouldThrowException() {
//        // Given
//        String token = "mockToken";
//        WithdrawDto.Request request = new WithdrawDto.Request() {{
//            setAccountNumber("12345678");
//            setAmount(2000L);
//            setWithdrawName("Jane Doe");
//        }};
//
//        Moeim mockMoeim = mock(Moeim.class);
//        User mockUser = mock(User.class);
//
//        when(mockMoeim.getAccountNumber()).thenReturn("12345678");
//        when(mockMoeim.getAmount()).thenReturn (1000.0);
//        when(mockMoeim.getUser()).thenReturn(mockUser);
//        when(mockUser.getUserId()).thenReturn(1L);
//
//        when(tokenProvider.getUserIdFromJWT(token)).thenReturn(1L);
//        when(moeimRepository.findByAccountNumber("12345678"))
//                .thenReturn(Optional.of(mockMoeim));
//
//        // When
//        CustomException exception = assertThrows(CustomException.class, () ->
//                transactionService.withdraw(token, request));
//
//        // Then
//        assertEquals(ErrorCode.BALANCE_NOT_ENOUGH, exception.getErrorCode());
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//
//    @Test
//    void remittance_InvalidSentAccount_ShouldThrowException() {
//        // Given
//        String token = "mockToken";
//        RemittanceDto.Request request = new RemittanceDto.Request() {{
//            setSentAccountNumber("11111111");
//            setReceivedAccountNumber("22222222");
//            setAmount(1000L);
//        }};
//
//        when(userRepository.findByAccountNumber("11111111"))
//                .thenReturn(Optional.empty());
//
//        // When
//        CustomException exception = assertThrows(CustomException.class, () ->
//                transactionService.remittance(token, request));
//
//        // Then
//        assertEquals(ErrorCode.SENT_ACCOUNT_NOT_FOUND, exception.getErrorCode());
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//}
