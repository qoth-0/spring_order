package com.encore.ordering.order.dto;

import lombok.Data;

import java.util.List;

//@Data
//public class OrderReqDto {
//    private List<Long> itemIds;
//    private List<Long> counts;

//}
// 예시 데이터
/*
{
    "itemIds" : [1, 2], "counts" : [10, 20]
}
 */

@Data
public class OrderReqDto {
    private Long itemId;
    private int count;

}
/* 컨트롤러에서 리스트로 변환 필요
[
        {"itemId" : 1, "count" : 10},
        {"itemId" : 2, "count" : 15},
]
 */