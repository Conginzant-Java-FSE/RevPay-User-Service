package com.revpay.userservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    public ApiResponse(boolean success, String message) { this.success = success; this.message = message; }
}
