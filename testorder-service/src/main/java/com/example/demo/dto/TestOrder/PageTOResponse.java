package com.example.demo.dto.TestOrder;

import lombok.Data;

import java.util.List;

@Data
public class PageTOResponse {
    private List<TOResponse> list;

    private Long totalElements;

    private Integer totalPages;

    private Integer currentPage;

    private Integer totalSize;

    private Boolean hasNext;
    private Boolean hasPrevious;

    private String sortBy;
    private String sortOrder;

    private Boolean empty;

    private String message;

    public PageTOResponse(){

    }

    public PageTOResponse(List<TOResponse> list, Long totalElements
            , Integer currentPage, Integer totalSize, String sortBy, String sortOrder){
        this.list = list;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.totalPages = (int) Math.ceil((double) totalElements / totalSize);
        this.totalSize = totalSize;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;

        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;

        this.empty = list == null || list.isEmpty();
        this.message = empty ? "No Data" : String.format("Found %d TestOrder(s)", totalElements);
    }

    public static PageTOResponse empty(int currentPage, int totalSize,  String sortBy, String sortOrder){
        PageTOResponse response = new PageTOResponse();
        response.list = List.of();
        response.totalElements = 0L;
        response.totalPages = 0;
        response.totalSize = totalSize;
        response.currentPage = currentPage;

        response.hasNext = false;
        response.hasPrevious = false;

        response.sortBy = sortBy;
        response.sortOrder = sortOrder;

        response.empty = true;
        response.message = "No Data";

        return response;

    }
}
