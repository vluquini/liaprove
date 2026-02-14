package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.usecases.user.users.*;
import com.lia.liaprove.infrastructure.dtos.user.ChangePasswordRequest;
import com.lia.liaprove.infrastructure.dtos.user.CreateUserRequest;
import com.lia.liaprove.infrastructure.dtos.user.UpdateUserRequest;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;
import com.lia.liaprove.infrastructure.mappers.users.UserMapper;
import com.lia.liaprove.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final FindUsersUseCase findUsersUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserMapper userMapper;

    public UserController(CreateUserUseCase createUserUseCase, GetUserByIdUseCase getUserByIdUseCase, FindUsersUseCase findUsersUseCase, UpdateUserProfileUseCase updateUserProfileUseCase, ChangePasswordUseCase changePasswordUseCase, DeleteUserUseCase deleteUserUseCase, UserMapper userMapper) {
        this.createUserUseCase = createUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.findUsersUseCase = findUsersUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        User newUser = createUserUseCase.create(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getOccupation(),
                request.getExperienceLevel(),
                request.getRole(),
                request.getCompanyName(),
                request.getCompanyEmail()
        );
        return new ResponseEntity<>(userMapper.toResponseDto(newUser), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        User user = getUserByIdUseCase.findById(id);
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<User> users = findUsersUseCase.findByName(Optional.ofNullable(name), Optional.ofNullable(role), page, size);
        List<UserResponseDto> userResponseDtos = users.stream().map(userMapper::toResponseDto).collect(Collectors.toList());
        return ResponseEntity.ok(userResponseDtos);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID actorUserId = principal.user().getId();

        deleteUserUseCase.delete(id, actorUserId);

        return ResponseEntity.noContent().build();
    }
}
