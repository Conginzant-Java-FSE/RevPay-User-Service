package com.revpay.userservice.model;

import com.revpay.userservice.enums.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long businessId;
    @OneToOne @JoinColumn(name = "user_id", nullable = false) private User user;
    private String businessName, taxId, contact_phone, website;
    @Column(columnDefinition = "TEXT") private String address;
    @Enumerated(EnumType.STRING) private BusinessType businessType;
    @Enumerated(EnumType.STRING) private RecordStatus status;
}
