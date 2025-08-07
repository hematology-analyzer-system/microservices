package com.example.user.dto.role;

import lombok.Data;

@Data
public class RoleRequest {
    private String sort;     // "name" hoặc "code"
    private String filter;   // keyword (VD: "admin")
    private int page_num;    // current page
    private int page_size;  
}
