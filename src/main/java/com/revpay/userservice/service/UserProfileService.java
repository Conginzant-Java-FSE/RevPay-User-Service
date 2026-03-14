package com.revpay.userservice.service;

import com.revpay.userservice.client.NotificationClient;
import com.revpay.userservice.dto.*;
import com.revpay.userservice.enums.*;
import com.revpay.userservice.model.*;
import com.revpay.userservice.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class UserProfileService {
    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired private UserRepository userRepo;
    @Autowired private PersonalProfileRepository personalRepo;
    @Autowired private BusinessProfileRepository businessRepo;
    @Autowired private BankAccountRepository bankRepo;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private NotificationClient notificationClient;

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ProfileMeResponse getFullProfile() {
        User user = getLoggedInUser();
        var builder = ProfileMeResponse.builder()
            .userId(user.getId()).accountType(user.getAccountType())
            .fullName(user.getFullName()).email(user.getEmail()).phone(user.getPhone())
            .username(user.getUsername()).createdAt(user.getCreatedAt())
            .mtpinSet(user.getMtPin() != null);

        bankRepo.findByUserAndIsPrimaryTrue(user).ifPresent(b ->
            builder.bankAccount(ProfileMeResponse.BankAccountSummary.builder()
                .bankName(b.getBankName())
                .accountNumber(mask(b.getAccountNumber()))
                .accountType(b.getAccountType() != null ? b.getAccountType().name() : null)
                .build()));

        if (user.getAccountType() == AccountType.PERSONAL) {
            Optional<PersonalProfile> p = personalRepo.findByUser(user);
            builder.profileComplete(p.isPresent());
            p.ifPresent(pr -> builder.address(pr.getAddress())
                .dob(pr.getDob() != null ? pr.getDob().toString() : null));
        } else if (user.getAccountType() == AccountType.BUSINESS) {
            Optional<BusinessProfile> b = businessRepo.findByUser(user);
            builder.profileComplete(b.isPresent());
            b.ifPresent(bp -> builder
                .businessName(bp.getBusinessName())
                .businessType(bp.getBusinessType() != null ? bp.getBusinessType().name() : null)
                .taxId(bp.getTaxId()).contactPhone(bp.getContact_phone()).website(bp.getWebsite())
                .businessStatus(bp.getStatus() != null ? bp.getStatus().name() : null));
        }
        return builder.build();
    }

    @Transactional
    public void createPersonalProfile(PersonalProfileFullRequest req) {
        User user = getLoggedInUser();
        if (personalRepo.existsByUser(user)) throw new IllegalStateException("Personal profile already exists");
        if (req.getUsername() != null) { user.setUsername(req.getUsername()); userRepo.save(user); }
        PersonalProfile p = new PersonalProfile();
        p.setUser(user); p.setDob(req.getDob()); p.setAddress(req.getAddress()); p.setStatus(RecordStatus.ACTIVE);
        personalRepo.save(p);
        saveBankAccount(user, req);
    }

    @Transactional
    public void updatePersonalProfile(PersonalProfileFullRequest req) {
        User user = getLoggedInUser();
        PersonalProfile p = personalRepo.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("Profile not found"));
        if (req.getUsername() != null) { user.setUsername(req.getUsername()); userRepo.save(user); }
        if (req.getDob() != null) p.setDob(req.getDob());
        if (req.getAddress() != null) p.setAddress(req.getAddress());
        personalRepo.save(p);
        bankRepo.findByUserAndIsPrimaryTrue(user).ifPresent(b -> {
            if (req.getBankName() != null) b.setBankName(req.getBankName());
            if (req.getAccountNumber() != null) b.setAccountNumber(req.getAccountNumber());
            if (req.getIfscCode() != null) b.setIfscCode(req.getIfscCode());
            bankRepo.save(b);
        });
    }

    @Transactional
    public void createBusinessProfile(BusinessProfileFullRequest req) {
        User user = getLoggedInUser();
        if (businessRepo.existsByUser(user)) throw new IllegalStateException("Business profile already exists");
        if (req.getUsername() != null) { user.setUsername(req.getUsername()); userRepo.save(user); }
        BusinessProfile bp = new BusinessProfile();
        bp.setUser(user); bp.setBusinessName(req.getBusinessName()); bp.setTaxId(req.getTaxId());
        bp.setAddress(req.getAddress()); bp.setBusinessType(req.getBusinessType());
        bp.setContact_phone(req.getContactPhone()); bp.setWebsite(req.getWebsite());
        bp.setStatus(RecordStatus.ACTIVE);
        businessRepo.save(bp);
        saveBankAccountBusiness(user, req);
    }

    @Transactional
    public void updateBusinessProfile(BusinessProfileUpdateRequest req) {
        User user = getLoggedInUser();
        BusinessProfile bp = businessRepo.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("Business profile not found"));
        if (req.getBusinessName() != null) bp.setBusinessName(req.getBusinessName());
        if (req.getBusinessType() != null) bp.setBusinessType(req.getBusinessType());
        if (req.getTaxId() != null) bp.setTaxId(req.getTaxId());
        if (req.getContactPhone() != null) bp.setContact_phone(req.getContactPhone());
        if (req.getWebsite() != null) bp.setWebsite(req.getWebsite());
        businessRepo.save(bp);
    }

    @Transactional
    public void setTransactionPin(SetTransactionPinRequest req) {
        User user = getLoggedInUser();
        if (user.getMtPin() != null) throw new IllegalStateException("PIN already set. Use change-pin to update.");
        if (!req.getMtPin().equals(req.getConfirmMtPin())) throw new IllegalArgumentException("PINs do not match");
        user.setMtPin(encoder.encode(req.getMtPin()));
        userRepo.save(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest req) {
        User user = getLoggedInUser();
        if (!encoder.matches(req.getCurrentPassword(), user.getPassword()))
            throw new IllegalArgumentException("Incorrect current password");
        if (!req.getNewPassword().equals(req.getConfirmPassword()))
            throw new IllegalArgumentException("New passwords do not match");
        user.setPassword(encoder.encode(req.getNewPassword()));
        userRepo.save(user);
        notify(user.getId(), "Your password was changed successfully. If this wasn't you, contact support.", "SECURITY_ALERT");
    }

    @Transactional
    public void changeTransactionPin(ChangePinRequest req) {
        User user = getLoggedInUser();
        if (user.getMtPin() != null && !encoder.matches(req.getCurrentPin(), user.getMtPin()))
            throw new IllegalArgumentException("Incorrect current PIN");
        if (!req.getNewPin().equals(req.getConfirmPin()))
            throw new IllegalArgumentException("New PINs do not match");
        user.setMtPin(encoder.encode(req.getNewPin()));
        userRepo.save(user);
    }

    @Transactional
    public void updateSecurityQuestion(UpdateSecurityQuestionRequest req) {
        User user = getLoggedInUser();
        if (!encoder.matches(req.getCurrentPassword(), user.getPassword()))
            throw new IllegalArgumentException("Incorrect password");
        user.setSecurityQuestion(req.getSecurityQuestion());
        user.setSecurityAnswer(encoder.encode(req.getSecurityAnswer().toLowerCase().trim()));
        userRepo.save(user);
    }

    @Transactional
    public void deleteAccount(Long userId, DeleteAccountRequest req) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (!encoder.matches(req.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("Invalid password");
        if (user.getStatus() == UserStatus.DEACTIVATED)
            throw new IllegalStateException("Account is already deactivated");
        user.setStatus(UserStatus.DEACTIVATED);
        user.setActive(false);
        user.setDeactivatedAt(LocalDateTime.now());
        if (req.getReason() != null) user.setDeactivationReason(req.getReason());
        userRepo.save(user);
    }

    // Internal: used by other services (wallet, transaction) to look up user info
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return UserInfoResponse.builder()
            .userId(user.getId()).fullName(user.getFullName()).email(user.getEmail())
            .phone(user.getPhone()).username(user.getUsername())
            .accountType(user.getAccountType()).active(user.isActive())
            .mtPin(user.getMtPin()).mtpinSet(user.getMtPin() != null)
            .build();
    }

    private void saveBankAccount(User user, PersonalProfileFullRequest req) {
        BankAccount b = new BankAccount();
        b.setUser(user); b.setAccountHolderName(req.getAccountHolderName());
        b.setBankName(req.getBankName()); b.setAccountNumber(req.getAccountNumber());
        b.setIfscCode(req.getIfscCode()); b.setAccountType(req.getAccountType());
        b.setIsPrimary(req.getIsPrimary() != null ? req.getIsPrimary() : true);
        b.setStatus(RecordStatus.ACTIVE);
        bankRepo.save(b);
    }

    private void saveBankAccountBusiness(User user, BusinessProfileFullRequest req) {
        BankAccount b = new BankAccount();
        b.setUser(user); b.setAccountHolderName(req.getAccountHolderName());
        b.setBankName(req.getBankName()); b.setAccountNumber(req.getAccountNumber());
        b.setIfscCode(req.getIfscCode()); b.setAccountType(req.getAccountType());
        b.setIsPrimary(req.getIsPrimary() != null ? req.getIsPrimary() : true);
        b.setStatus(RecordStatus.ACTIVE);
        bankRepo.save(b);
    }

    private void notify(Long userId, String message, String type) {
        try {
            notificationClient.createNotification(Map.of("userId", userId, "message", message, "type", type));
        } catch (Exception e) { log.warn("Notification failed: {}", e.getMessage()); }
    }

    private String mask(String n) {
        if (n == null || n.length() < 4) return "****";
        return "****" + n.substring(n.length() - 4);
    }
}
