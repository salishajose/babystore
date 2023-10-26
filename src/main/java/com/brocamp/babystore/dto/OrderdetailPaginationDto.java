package com.brocamp.babystore.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderdetailPaginationDto {
    private long pageNo;
    private long totalPages;
    private long totalItems;
    private List<OrderDetailsDTO> orderDetailsDTOList;
}
