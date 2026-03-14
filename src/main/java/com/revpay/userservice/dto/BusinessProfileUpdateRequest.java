package com.revpay.userservice.dto;

import com.revpay.userservice.enums.BusinessType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProfileUpdateRequest {
    private String businessName, taxId, contactPhone, website;
    private BusinessType businessType;
}
