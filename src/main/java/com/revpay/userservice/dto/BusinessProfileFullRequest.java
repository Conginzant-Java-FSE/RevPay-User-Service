package com.revpay.userservice.dto;

import com.revpay.userservice.enums.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProfileFullRequest {
    private String username, businessName, taxId, address, contactPhone, website;
    private BusinessType businessType;
    private String accountHolderName, bankName, accountNumber, ifscCode;
    private BankAccountType accountType;
    private Boolean isPrimary;
}
