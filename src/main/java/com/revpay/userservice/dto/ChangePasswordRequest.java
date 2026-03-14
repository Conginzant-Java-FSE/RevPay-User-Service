package com.revpay.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest { private String currentPassword, newPassword, confirmPassword; }
