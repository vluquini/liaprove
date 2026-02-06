package com.lia.liaprove.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro de autenticação simplificado utilizado exclusivamente no profile {@code dev}.
 *
 * <p>
 * Este filtro intercepta as requisições HTTP e autentica automaticamente
 * o usuário utilizando um token fixo ou uma identidade pré-definida,
 * sem validação de JWT ou consulta ao banco de dados.
 * </p>
 *
 * <p>
 * Seu objetivo é acelerar o ciclo de desenvolvimento e testes,
 * permitindo acesso imediato aos endpoints protegidos da aplicação.
 * </p>
 *
 * <p>
 * Este filtro é registrado apenas quando o profile {@code dev} está ativo
 * e não deve ser utilizado em ambientes de produção.
 * </p>
 */

@Profile("dev")
@Component
public class DevAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "dev-user",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN"),
                                new SimpleGrantedAuthority("ROLE_PROFESSIONAL"),
                                new SimpleGrantedAuthority("ROLE_RECRUITER")
                        )
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
