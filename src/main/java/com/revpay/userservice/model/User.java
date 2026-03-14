package com.revpay.userservice.model;

import com.revpay.userservice.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true) private String username;
    @Column(nullable = false) private String fullName;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false, unique = true) private String phone;
    @Column(nullable = false) private String password;
    private String securityQuestion;
    private String securityAnswer;
    @Column(name = "mt_pin") private String mtPin;
    @Enumerated(EnumType.STRING) private AccountType accountType;
    private boolean active = true;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private UserStatus status = UserStatus.ACTIVE;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role = Role.USER;
    @Column(name = "deactivated_at") private LocalDateTime deactivatedAt;
    @Column(name = "deactivation_reason", columnDefinition = "TEXT") private String deactivationReason;
    @Column(name = "daily_limit") private Double dailyLimit = 100000.0;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
