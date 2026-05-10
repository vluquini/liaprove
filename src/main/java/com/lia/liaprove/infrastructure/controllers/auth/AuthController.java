package com.lia.liaprove.infrastructure.controllers.auth;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserStatus;
import com.lia.liaprove.core.usecases.user.CreateUserUseCase;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.infrastructure.dtos.user.AuthenticatedUserResponse;
import com.lia.liaprove.infrastructure.dtos.user.AuthenticationRequest;
import com.lia.liaprove.infrastructure.dtos.user.AuthenticationResponse;
import com.lia.liaprove.infrastructure.dtos.user.CreateUserRequest;
import com.lia.liaprove.infrastructure.security.CustomUserDetails;
import com.lia.liaprove.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("!dev")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final CreateUserUseCase createUserUseCase;
    private final UserGateway userGateway;
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
                request.getHardSkills(),
                request.getSoftSkills(),
                request.getCompanyName(),
                request.getCompanyEmail()
        );

        UserDetails userDetails = new CustomUserDetails(newUser);
        JwtService.GeneratedToken generatedToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(buildAuthenticationResponse(generatedToken, newUser));
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
        final CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.getEmail());
        User user = userDetails.user();

        // Reativação automática se o usuário estiver INACTIVE
        if (UserStatus.INACTIVE.equals(user.getStatus())) {
            user.setStatus(UserStatus.ACTIVE);
            userGateway.save(user);
        }

        JwtService.GeneratedToken generatedToken = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(buildAuthenticationResponse(generatedToken, user));
    }

    private AuthenticationResponse buildAuthenticationResponse(JwtService.GeneratedToken generatedToken, User user) {
        return AuthenticationResponse.builder()
                .token(generatedToken.token())
                .tokenType("Bearer")
                .expiresAt(generatedToken.expiresAt())
                .user(toAuthenticatedUserResponse(user))
                .build();
    }

    private AuthenticatedUserResponse toAuthenticatedUserResponse(User user) {
        String companyName = null;
        String companyEmail = null;
        if (user instanceof UserRecruiter recruiter) {
            companyName = recruiter.getCompanyName();
            companyEmail = recruiter.getCompanyEmail();
        }

        return new AuthenticatedUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getOccupation(),
                user.getExperienceLevel(),
                companyName,
                companyEmail
        );
    }
}
