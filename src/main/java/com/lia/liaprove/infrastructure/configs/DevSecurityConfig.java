package com.lia.liaprove.infrastructure.configs;

import com.lia.liaprove.infrastructure.security.CustomAccessDeniedHandler;
import com.lia.liaprove.infrastructure.security.CustomAuthenticationEntryPoint;
import com.lia.liaprove.infrastructure.security.DevAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração de segurança exclusiva para o profile {@code dev}.
 *
 * <p>
 * Esta classe substitui a configuração de segurança padrão utilizada nos
 * demais ambientes, desabilitando autenticação baseada em JWT e permitindo
 * o acesso simplificado à aplicação durante o desenvolvimento.
 * </p>
 *
 * <p>
 * Seu principal objetivo é facilitar testes manuais e automatizados,
 * eliminando a necessidade de login/registro e integração com provedores
 * reais de autenticação.
 * </p>
 *
 * <p>
 * Esta configuração só é carregada quando o profile {@code dev} está ativo.
 * </p>
 */

@Profile("dev")
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class DevSecurityConfig {

    private final DevAuthenticationFilter devAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(e -> e.authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(devAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Não registrar jwtAuthFilter ou authenticationProvider aqui
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}
