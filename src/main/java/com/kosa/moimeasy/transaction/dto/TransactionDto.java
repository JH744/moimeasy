package com.kosa.moimeasy.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.kosa.moimeasy.transaction.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionDto {

    private Long id;

    private String transactionTargetName;

    private double amount;

    private TransactionType type;

    @JsonFormat(shape = Shape.STRING,  pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime transactedAt;
}