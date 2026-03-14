package com.revpay.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePinRequest { private String currentPin, newPin, confirmPin; }
