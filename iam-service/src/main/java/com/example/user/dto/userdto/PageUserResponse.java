package com.example.user.dto.userdto;
import java.util.List;
import lombok.Data;
@Data
public class PageUserResponse {
    private List<UserResponse> roles;
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

    // Constructors
    public PageUserResponse() {
    }

    public PageUserResponse(List<UserResponse> roles, long totalElements, int currentPage, int pageSize,
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

    // Static factory method for empty response
    public static PageUserResponse empty(int currentPage, int pageSize, String sortBy, String sortDirection) {
        PageUserResponse response = new PageUserResponse();
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
