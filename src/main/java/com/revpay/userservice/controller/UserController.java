package com.revpay.userservice.controller;

import com.revpay.userservice.dto.*;
import com.revpay.userservice.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired private UserProfileService service;

    // ── Profile endpoints ─────────────────────────────────────────
    @GetMapping("/api/users/me")
    public ResponseEntity<ApiResponse<ProfileMeResponse>> getProfile() {
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", service.getFullProfile()));
    }

    @PostMapping("/api/users/create-personal-profile")
    public ResponseEntity<ApiResponse<Void>> createPersonal(@RequestBody PersonalProfileFullRequest req) {
        service.createPersonalProfile(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Personal profile created"));
    }

    @PutMapping("/api/users/update-personal-profile")
    public ResponseEntity<ApiResponse<Void>> updatePersonal(@RequestBody PersonalProfileFullRequest req) {
        service.updatePersonalProfile(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated"));
    }

    @PostMapping("/api/users/create-business-profile")
    public ResponseEntity<ApiResponse<Void>> createBusiness(@RequestBody BusinessProfileFullRequest req) {
        service.createBusinessProfile(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Business profile created"));
    }

    @PutMapping("/api/users/update-business-profile")
    public ResponseEntity<ApiResponse<Void>> updateBusiness(@RequestBody BusinessProfileUpdateRequest req) {
        service.updateBusinessProfile(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Business profile updated"));
    }

    // ── Security/PIN endpoints ────────────────────────────────────
    @PostMapping("/api/users/set-pin")
    public ResponseEntity<ApiResponse<Void>> setPin(@RequestBody SetTransactionPinRequest req) {
        service.setTransactionPin(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction PIN set"));
    }

    @PutMapping("/api/profile/change-pin")
    public ResponseEntity<ApiResponse<Void>> changePin(@RequestBody ChangePinRequest req) {
        service.changeTransactionPin(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "PIN changed"));
    }

    @PutMapping("/api/users/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody ChangePasswordRequest req) {
        service.changePassword(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed"));
    }

    @PutMapping("/api/profile/security-question/update")
    public ResponseEntity<ApiResponse<Void>> updateSecurityQ(@RequestBody UpdateSecurityQuestionRequest req) {
        service.updateSecurityQuestion(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Security question updated"));
    }

    @DeleteMapping("/api/profile/delete-account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody DeleteAccountRequest req) {
        service.deleteAccount(Long.parseLong(userId), req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account deactivated"));
    }

    // ── Internal endpoints (called by other services) ─────────────
    @GetMapping("/internal/users/{userId}")
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUserInfo(userId));
    }
}
