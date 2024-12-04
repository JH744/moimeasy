package com.kosa.moimeasy.moeim.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoeimDTO {
    private Long moeimid;         
    private Long userId;     
    private String moeimName; 
    private String moeimCode; 
}
