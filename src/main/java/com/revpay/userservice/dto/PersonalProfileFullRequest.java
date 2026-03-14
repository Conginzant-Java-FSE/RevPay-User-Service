package com.revpay.userservice.dto;

import com.revpay.userservice.enums.BankAccountType;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalProfileFullRequest {
    private String username, address, accountHolderName, bankName, accountNumber, ifscCode;
    private LocalDate dob;
    private BankAccountType accountType;
    private Boolean isPrimary;
}
