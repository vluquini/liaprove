package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.usecases.user.users.CreateUserUseCase;
import com.lia.liaprove.infrastructure.dtos.CreateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final CreateUserUseCase createUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
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
}
