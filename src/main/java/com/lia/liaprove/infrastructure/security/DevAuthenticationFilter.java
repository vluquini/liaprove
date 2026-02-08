package com.lia.liaprove.infrastructure.security;

import com.lia.liaprove.infrastructure.mappers.users.UserMapper;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
@RequiredArgsConstructor
public class DevAuthenticationFilter extends OncePerRequestFilter {

    // E-mail do usuário seeded que será usado como identidade no dev
    private static final String DEV_USER_HEADER = "X-Dev-User-Email";

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Se já autenticado, não sobrescrever
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            String email = request.getHeader(DEV_USER_HEADER);

            if (email != null && !email.isBlank()) {
                userJpaRepository.findByEmail(email).ifPresent(userEntity -> {
                    var userDomain = userMapper.toDomain(userEntity);
                    var userDetails = new CustomUserDetails(userDomain);

                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            }
        }

        filterChain.doFilter(request, response);
    }
}
