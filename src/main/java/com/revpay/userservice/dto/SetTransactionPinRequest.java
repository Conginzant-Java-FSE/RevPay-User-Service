package com.revpay.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetTransactionPinRequest { private String mtPin, confirmMtPin; }
