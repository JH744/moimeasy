package com.kosa.moimeasy.transaction.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailDTO {

    private String userName;
    private String userAccount;
    private String moeimName;
    private String moeimAccount;

}
