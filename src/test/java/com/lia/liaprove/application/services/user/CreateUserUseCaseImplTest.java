package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.PasswordHasher;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.*;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.usecases.user.users.UserFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseImplTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private CreateUserUseCaseImpl createUserUseCase;

    @Test
    @DisplayName("Deve registrar um novo usuário com dados válidos")
    void deveRegistrarUmNovoUsuarioComDadosValidos() {
        // Arrange
        String name = "John Snow";
        String email = "john.snow@example.com";
        String rawPassword = "password123";
        String hashedPassword = "hashedPassword123";
        String occupation = "Developer";
        ExperienceLevel experience = ExperienceLevel.PLENO;
        UserRole role = UserRole.PROFESSIONAL;

        UserCreateDto dto = new UserCreateDto(name, email, hashedPassword, occupation, experience, role);
        User userToCreate = mock(User.class);
        User savedUser = mock(User.class);

        when(userGateway.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordHasher.hash(rawPassword)).thenReturn(hashedPassword);
        when(userFactory.create(any(UserCreateDto.class))).thenReturn(userToCreate);
        when(userGateway.save(userToCreate)).thenReturn(savedUser);

        // Act
        User result = createUserUseCase.create(name, email, rawPassword, occupation, experience, role);

        // Assert
        assertThat(result).isEqualTo(savedUser);

        verify(userGateway).findByEmail(email);
        verify(passwordHasher).hash(rawPassword);
        verify(userFactory).create(dto); // Verifica se o DTO tem a senha hasheada
        verify(userGateway).save(userToCreate);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já estiver registrado")
    void deveLancarExcecaoQuandoEmailJaEstiverRegistrado() {
        // Arrange
        String email = "existing.user@example.com";
        User existingUser = mock(UserProfessional.class);
        when(existingUser.getEmail()).thenReturn(email);

        when(userGateway.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() -> createUserUseCase.create("John", email, "password123", "Dev", ExperienceLevel.JUNIOR, UserRole.PROFESSIONAL))
                .isInstanceOf(InvalidUserDataException.class)
                .hasMessageContaining("Email already registered");

        verify(userGateway).findByEmail(email);
        verifyNoInteractions(passwordHasher);
        verifyNoInteractions(userFactory);
        verify(userGateway, never()).save(any(User.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "abc"})
    @NullSource
    @DisplayName("Deve lançar exceção quando senha for inválida (curta ou nula)")
    void deveLancarExcecaoQuandoSenhaForInvalida(String invalidPassword) {
        // Arrange
        String email = "test@example.com";

        // Act & Assert
        assertThatThrownBy(() -> createUserUseCase.create("John", email, invalidPassword, "Dev", ExperienceLevel.JUNIOR, UserRole.PROFESSIONAL))
                .isInstanceOf(InvalidUserDataException.class)
                .hasMessageContaining("Password must have at least");

        verifyNoInteractions(userGateway);
        verifyNoInteractions(passwordHasher);
        verifyNoInteractions(userFactory);
    }
}

