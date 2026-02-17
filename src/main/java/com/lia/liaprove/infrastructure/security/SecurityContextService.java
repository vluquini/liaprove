package com.lia.liaprove.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityContextService {

    /**
     * Extrai o ID do usuário autenticado do SecurityContextHolder.
     * Assume que o principal é uma instância de CustomUserDetails.
     *
     * @return UUID do usuário autenticado.
     */
    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new IllegalStateException("User not authenticated or principal is not of expected type");
        }
        return principal.user().getId();
    }
}
