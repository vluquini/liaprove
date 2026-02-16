package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.usecases.user.admin.UserModerationUseCase;
import com.lia.liaprove.core.usecases.user.users.DeleteUserUseCase;
import com.lia.liaprove.core.usecases.user.users.FindUsersUseCase;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;
import com.lia.liaprove.infrastructure.mappers.users.UserMapper;
import com.lia.liaprove.infrastructure.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final FindUsersUseCase findUsersUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserModerationUseCase userModerationUseCase;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<User> users = findUsersUseCase.findByName(Optional.ofNullable(name), Optional.ofNullable(role), page, size);
        List<UserResponseDto> userResponseDtos = users.stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponseDtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID adminId = principal.user().getId();

        deleteUserUseCase.delete(id, adminId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID adminId = principal.user().getId();

        userModerationUseCase.activateUser(id, adminId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID adminId = principal.user().getId();

        userModerationUseCase.deactivateUser(id, adminId);
        return ResponseEntity.ok().build();
    }
}
