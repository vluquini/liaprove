package com.lia.liaprove.infrastructure.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String tokenType;
    private Instant expiresAt;
    private AuthenticatedUserResponse user;
}
