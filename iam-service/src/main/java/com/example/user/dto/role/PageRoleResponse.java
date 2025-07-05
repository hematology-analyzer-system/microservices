package com.example.user.dto.role;

import lombok.Data;

import java.util.List;

@Data
public class PageRoleResponse {
    private List<RoleResponse> roles;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private String sortBy;
    private String sortDirection;
    private boolean empty;
    private String message;

    public PageRoleResponse() {}

    public PageRoleResponse(List<RoleResponse> roles, long totalElements, int currentPage, int pageSize,
                            String sortBy, String sortDirection) {
        this.roles = roles;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.empty = roles == null || roles.isEmpty();
        this.message = empty ? "No Data" : String.format("Found %d role(s)", totalElements);
    }

    public static PageRoleResponse empty(int currentPage, int pageSize, String sortBy, String sortDirection) {
        PageRoleResponse response = new PageRoleResponse();
        response.roles = List.of();
        response.totalElements = 0;
        response.totalPages = 0;
        response.currentPage = currentPage;
        response.pageSize = pageSize;
        response.hasNext = false;
        response.hasPrevious = false;
        response.sortBy = sortBy;
        response.sortDirection = sortDirection;
        response.empty = true;
        response.message = "No Data";
        return response;
    }
}

