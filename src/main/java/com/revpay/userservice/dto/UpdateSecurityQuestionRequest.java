package com.revpay.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSecurityQuestionRequest { private String currentPassword, securityQuestion, securityAnswer; }
