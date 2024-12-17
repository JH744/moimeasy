//package com.kosa.moimeasy.transaction;
//
//import com.kosa.moimeasy.transaction.entity.Transaction;
//import com.kosa.moimeasy.transaction.repository.TransactionRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//public class TransactionRepositoryTest {
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    @Test
//    void testFindByMoeimAccountIdAndTransactedAtBetween() {
//        Long moeimId = 1L;
//        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
//        LocalDateTime endDate = LocalDateTime.now();
//
//        List<Transaction> transactions = transactionRepository.findByMoeimAccount_MoeimIdAndTransactedAtBetween(
//                moeimId, startDate, endDate
//        );
//
//        assertNotNull(transactions);
//        transactions.forEach(transaction -> {
//            assertEquals(moeimId, transaction.getMoeimAccount().getMoeimId());
//        });
//    }
//}