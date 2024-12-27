package com.kosa.moimeasy.transaction.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailDto { // 초기 데이터

    private String userName;
    private String userAccount;
    private String moeimName;
    private String moeimAccount;

}
