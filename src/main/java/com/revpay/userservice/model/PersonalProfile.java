package com.revpay.userservice.model;

import com.revpay.userservice.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "personal_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long profileId;
    @OneToOne @JoinColumn(name = "user_id", nullable = false) private User user;
    private LocalDate dob;
    @Column(columnDefinition = "TEXT") private String address;
    @Enumerated(EnumType.STRING) private RecordStatus status;
}
