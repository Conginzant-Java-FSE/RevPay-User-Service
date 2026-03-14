package com.revpay.userservice.dto;

import com.revpay.userservice.enums.AccountType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String fullName, email, phone, username, mtPin;
    private AccountType accountType;
    private boolean active, mtpinSet;
}
