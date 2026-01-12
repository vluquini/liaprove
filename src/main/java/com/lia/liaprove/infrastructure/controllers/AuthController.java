package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.usecases.user.users.CreateUserUseCase;
import com.lia.liaprove.infrastructure.dtos.AuthenticationRequest;
import com.lia.liaprove.infrastructure.dtos.AuthenticationResponse;
import com.lia.liaprove.infrastructure.dtos.CreateUserRequest;
import com.lia.liaprove.infrastructure.security.CustomUserDetails;
import com.lia.liaprove.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final CreateUserUseCase createUserUseCase;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody CreateUserRequest request) {
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

        UserDetails userDetails = new CustomUserDetails(newUser);
        final String jwtToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(AuthenticationResponse.builder().token(jwtToken).build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        // Se a autenticação for bem-sucedida, gera um token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwtToken = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(AuthenticationResponse.builder().token(jwtToken).build());
    }
}
