package com.revpay.userservice.dto;

import com.revpay.userservice.enums.AccountType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileMeResponse {
    private Long userId;
    private AccountType accountType;
    private String fullName, email, phone, username;
    private boolean mtpinSet, profileComplete;
    private String address, dob;
    private String businessName, businessType, taxId, contactPhone, website, businessStatus;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankAccountSummary { private String bankName, accountNumber, accountType; }

    private BankAccountSummary bankAccount;
}
