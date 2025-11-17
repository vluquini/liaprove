package com.lia.liaprove.infrastructure.configs;

import com.lia.liaprove.application.gateways.user.PasswordHasher;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.application.services.user.CreateUserUseCaseImpl;
import com.lia.liaprove.application.services.user.DefaultUserFactory;
import com.lia.liaprove.core.usecases.user.users.CreateUserUseCase;
import com.lia.liaprove.core.usecases.user.users.UserFactory;
import com.lia.liaprove.infrastructure.mappers.UserMapper;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import com.lia.liaprove.infrastructure.services.PasswordHasherImpl;
import com.lia.liaprove.infrastructure.services.UserGatewayImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public PasswordHasher passwordHasher(PasswordEncoder passwordEncoder) {
        return new PasswordHasherImpl(passwordEncoder);
    }

    @Bean
    public UserGateway userGateway(UserJpaRepository userJpaRepository, UserMapper userMapper) {
        return new UserGatewayImpl(userJpaRepository, userMapper);
    }

    @Bean
    public UserFactory userFactory() {
        return new DefaultUserFactory();
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserGateway userGateway, PasswordHasher passwordHasher, UserFactory userFactory) {
        return new CreateUserUseCaseImpl(userGateway, passwordHasher, userFactory);
    }
}