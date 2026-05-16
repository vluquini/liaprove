package com.lia.liaprove.infrastructure.controllers.user;

import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.usecases.user.ChangePasswordUseCase;
import com.lia.liaprove.core.usecases.user.GetUserByIdUseCase;
import com.lia.liaprove.core.usecases.user.ListMyCertificatesUseCase;
import com.lia.liaprove.core.usecases.user.UpdateUserProfileUseCase;
import com.lia.liaprove.core.usecases.user.UserModerationUseCase;
import com.lia.liaprove.infrastructure.dtos.user.ChangePasswordRequest;
import com.lia.liaprove.infrastructure.dtos.user.UpdateUserRequest;
import com.lia.liaprove.infrastructure.dtos.user.UserCertificateResponse;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import com.lia.liaprove.infrastructure.security.SecurityContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController {
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ListMyCertificatesUseCase listMyCertificatesUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final UserModerationUseCase userModerationUseCase;
    private final UserMapper userMapper;
    private final SecurityContextService securityContextService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        User user = getUserByIdUseCase.findById(id);
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUserProfile(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        User updatedUser = updateUserProfileUseCase.updateProfile(
                id,
                request.getName(),
                request.getEmail(),
                request.getOccupation(),
                request.getBio(),
                request.getExperienceLevel(),
                request.getHardSkills(),
                request.getSoftSkills()
        );
        return ResponseEntity.ok(userMapper.toResponseDto(updatedUser));
    }

    @GetMapping("/me/certificates")
    public ResponseEntity<List<UserCertificateResponse>> listMyCertificates() {
        UUID userId = securityContextService.getCurrentUserId();
        List<UserCertificateResponse> certificates = listMyCertificatesUseCase.execute(userId).stream()
                .map(this::toUserCertificateResponse)
                .toList();

        return ResponseEntity.ok(certificates);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable UUID id, @Valid @RequestBody ChangePasswordRequest request) {
        changePasswordUseCase.changePassword(id, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/deactivate")
    public ResponseEntity<String> deactivateOwnAccount() {
        UUID userId = securityContextService.getCurrentUserId();

        userModerationUseCase.deactivateUser(userId, userId);

        return ResponseEntity.ok("Account deactivated successfully. It will be permanently deleted in 60 days unless you log in again.");
    }

    private UserCertificateResponse toUserCertificateResponse(Certificate certificate) {
        return new UserCertificateResponse(
                certificate.getCertificateNumber(),
                certificate.getTitle(),
                certificate.getDescription(),
                certificate.getCertificateUrl(),
                certificate.getIssueDate(),
                certificate.getScore()
        );
    }
}
