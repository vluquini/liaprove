package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.usecases.user.users.CreateUserUseCase;
import com.lia.liaprove.core.usecases.user.users.FindUsersUseCase;
import com.lia.liaprove.core.usecases.user.users.GetUserByIdUseCase;
import com.lia.liaprove.infrastructure.dtos.CreateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final FindUsersUseCase findUsersUseCase;

    public UserController(CreateUserUseCase createUserUseCase, GetUserByIdUseCase getUserByIdUseCase, FindUsersUseCase findUsersUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.findUsersUseCase = findUsersUseCase;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        User newUser = createUserUseCase.create(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getOccupation(),
                request.getExperienceLevel(),
                request.getRole()
        );
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        User user = getUserByIdUseCase.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> findUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<User> users = findUsersUseCase.findByName(Optional.ofNullable(name), Optional.ofNullable(role), page, size);
        return ResponseEntity.ok(users);
    }
}
