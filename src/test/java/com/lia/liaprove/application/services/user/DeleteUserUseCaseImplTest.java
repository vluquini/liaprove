package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.User;
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
class DeleteUserUseCaseImplTest {

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private DeleteUserUseCaseImpl deleteUserUseCase;

    @Test
    @DisplayName("Deve deletar um usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User existingUser = mock(User.class);
        when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        deleteUserUseCase.delete(userId);

        // Assert
        verify(userGateway).deleteById(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar usuário inexistente")
    void deveLancarExcecaoAoTentarDeletarUsuarioInexistente() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userGateway.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deleteUserUseCase.delete(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: " + userId);

        verify(userGateway, never()).deleteById(any(UUID.class));
    }
}
