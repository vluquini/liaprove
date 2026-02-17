package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.usecases.user.admin.UserModerationUseCase;
import com.lia.liaprove.core.usecases.user.users.*;
import com.lia.liaprove.infrastructure.dtos.user.ChangePasswordRequest;
import com.lia.liaprove.infrastructure.dtos.user.UpdateUserRequest;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;
import com.lia.liaprove.infrastructure.mappers.users.UserMapper;
import com.lia.liaprove.infrastructure.security.SecurityContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController {
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
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
                request.getExperienceLevel()
        );
        return ResponseEntity.ok(userMapper.toResponseDto(updatedUser));
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
}
