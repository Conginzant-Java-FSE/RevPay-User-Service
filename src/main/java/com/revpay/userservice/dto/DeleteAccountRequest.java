package com.revpay.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAccountRequest { private String password, reason; }
