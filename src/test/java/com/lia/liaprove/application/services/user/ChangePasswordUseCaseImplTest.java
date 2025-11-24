package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.PasswordHasher;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.InvalidCredentialsException;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordUseCaseImplTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private ChangePasswordUseCaseImpl changePasswordUseCase;

    @Test
    @DisplayName("Deve alterar a senha com sucesso")
    void deveAlterarSenhaComSucesso() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword456";
        String oldHashedPassword = "hashedOldPassword";
        String newHashedPassword = "hashedNewPassword";

        User user = mock(User.class);
        when(user.getPasswordHash()).thenReturn(oldHashedPassword);
        when(userGateway.findById(userId)).thenReturn(Optional.of(user));
        when(passwordHasher.matches(oldPassword, oldHashedPassword)).thenReturn(true);
        when(passwordHasher.hash(newPassword)).thenReturn(newHashedPassword);

        // Act
        changePasswordUseCase.changePassword(userId, oldPassword, newPassword);

        // Assert
        verify(user).setPasswordHash(newHashedPassword);
        verify(userGateway).save(user);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar senha de usuário inexistente")
    void deveLancarExcecaoAoTentarAlterarSenhaDeUsuarioInexistente() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userGateway.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> changePasswordUseCase.changePassword(userId, "oldPass", "newPass"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: " + userId);
        
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha antiga for inválida")
    void deveLancarExcecaoQuandoSenhaAntigaInvalida() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String oldPassword = "wrongOldPassword";
        String newPassword = "newPassword456";
        String correctOldHashedPassword = "hashedOldPassword";

        User user = mock(User.class);
        when(user.getPasswordHash()).thenReturn(correctOldHashedPassword);
        when(userGateway.findById(userId)).thenReturn(Optional.of(user));
        when(passwordHasher.matches(oldPassword, correctOldHashedPassword)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> changePasswordUseCase.changePassword(userId, oldPassword, newPassword))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid old password.");

        verify(user, never()).setPasswordHash(anyString());
        verify(userGateway, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a nova senha for muito curta")
    void deveLancarExcecaoQuandoNovaSenhaMuitoCurta() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String oldPassword = "oldPassword123";
        String newPassword = "123"; // Senha curta
        String oldHashedPassword = "hashedOldPassword";


        User user = mock(User.class);
        when(user.getPasswordHash()).thenReturn(oldHashedPassword);
        when(userGateway.findById(userId)).thenReturn(Optional.of(user));
        when(passwordHasher.matches(oldPassword, oldHashedPassword)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> changePasswordUseCase.changePassword(userId, oldPassword, newPassword))
                .isInstanceOf(InvalidUserDataException.class)
                .hasMessage("Password must have at least 6 characters");
        
        verify(userGateway, never()).save(any());
    }
}
