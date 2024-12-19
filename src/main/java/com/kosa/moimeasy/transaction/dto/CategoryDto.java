package com.kosa.moimeasy.transaction.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private String categoryName;

    private double categoryMoney; // 각 카테고리의 금액

}