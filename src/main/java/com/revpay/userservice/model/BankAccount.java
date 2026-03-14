package com.revpay.userservice.model;

import com.revpay.userservice.enums.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long accountId;
    @ManyToOne @JoinColumn(name = "user_id", nullable = false) private User user;
    private String accountHolderName, bankName, accountNumber, ifscCode;
    private Boolean isPrimary;
    @Enumerated(EnumType.STRING) @Column(name = "account_type") private BankAccountType accountType;
    @Enumerated(EnumType.STRING) private RecordStatus status;
}
