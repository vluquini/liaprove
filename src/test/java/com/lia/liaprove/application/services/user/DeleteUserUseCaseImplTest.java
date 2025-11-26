package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.AuthorizationException;
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
    @DisplayName("Deve deletar um usuário com sucesso quando o ator é Admin e o alvo não é Admin")
    void shouldDeleteUserSuccessfullyWhenActorIsAdminAndTargetIsNotAdmin() {
        // Arrange
        User adminUser = mock(User.class);
        when(adminUser.getId()).thenReturn(UUID.randomUUID());
        when(adminUser.getRole()).thenReturn(UserRole.ADMIN);

        User professionalUser = mock(User.class);
        when(professionalUser.getId()).thenReturn(UUID.randomUUID());
        when(professionalUser.getRole()).thenReturn(UserRole.PROFESSIONAL);

        when(userGateway.findById(professionalUser.getId())).thenReturn(Optional.of(professionalUser));
        when(userGateway.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        // Act
        deleteUserUseCase.delete(professionalUser.getId(), adminUser.getId());

        // Assert
        verify(userGateway).deleteById(professionalUser.getId());
    }

    @Test
    @DisplayName("Deve deletar com sucesso quando um usuário não-Admin deleta a si mesmo")
    void shouldDeleteSuccessfullyWhenNonAdminDeletesSelf() {
        // Arrange
        User professionalUser = mock(User.class);
        when(professionalUser.getId()).thenReturn(UUID.randomUUID());
        when(professionalUser.getRole()).thenReturn(UserRole.PROFESSIONAL);

        when(userGateway.findById(professionalUser.getId())).thenReturn(Optional.of(professionalUser));

        // Act
        deleteUserUseCase.delete(professionalUser.getId(), professionalUser.getId());

        // Assert
        verify(userGateway).deleteById(professionalUser.getId());
    }

    @Test
    @DisplayName("Deve lançar AuthorizationException ao tentar deletar a própria conta sendo Admin")
    void shouldThrowAuthorizationExceptionWhenAdminDeletesSelf() {
        // Arrange
        User adminUser = mock(User.class);
        when(adminUser.getId()).thenReturn(UUID.randomUUID());
        when(adminUser.getRole()).thenReturn(UserRole.ADMIN);

        when(userGateway.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        // Act & Assert
        assertThatThrownBy(() -> deleteUserUseCase.delete(adminUser.getId(), adminUser.getId()))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("Admins cannot delete their own account.");

        verify(userGateway, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar AuthorizationException ao Admin tentar deletar outro Admin")
    void shouldThrowAuthorizationExceptionWhenAdminDeletesAnotherAdmin() {
        // Arrange
        User adminUser = mock(User.class);
        when(adminUser.getId()).thenReturn(UUID.randomUUID());
        when(adminUser.getRole()).thenReturn(UserRole.ADMIN);

        User anotherAdminUser = mock(User.class);
        when(anotherAdminUser.getId()).thenReturn(UUID.randomUUID());
        when(anotherAdminUser.getRole()).thenReturn(UserRole.ADMIN);

        when(userGateway.findById(anotherAdminUser.getId())).thenReturn(Optional.of(anotherAdminUser));
        when(userGateway.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        // Act & Assert
        assertThatThrownBy(() -> deleteUserUseCase.delete(anotherAdminUser.getId(), adminUser.getId()))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("Admins are not allowed to delete other admin accounts.");

        verify(userGateway, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar AuthorizationException quando usuário não-Admin tenta deletar outro usuário")
    void shouldThrowAuthorizationExceptionWhenNonAdminDeletesAnotherUser() {
        // Arrange
        User professionalUser = mock(User.class);
        when(professionalUser.getId()).thenReturn(UUID.randomUUID());
        when(professionalUser.getRole()).thenReturn(UserRole.PROFESSIONAL);

        User anotherProfessional = mock(User.class);
        when(anotherProfessional.getId()).thenReturn(UUID.randomUUID());

        when(userGateway.findById(anotherProfessional.getId())).thenReturn(Optional.of(anotherProfessional));
        when(userGateway.findById(professionalUser.getId())).thenReturn(Optional.of(professionalUser));


        // Act & Assert
        assertThatThrownBy(() -> deleteUserUseCase.delete(anotherProfessional.getId(), professionalUser.getId()))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("A user is not allowed to delete another user.");

        verify(userGateway, never()).deleteById(any(UUID.class));
    }


    @Test
    @DisplayName("Deve lançar UserNotFoundException ao tentar deletar usuário alvo inexistente")
    void shouldThrowUserNotFoundExceptionWhenTargetUserNotFound() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID(); // ID do ator, não precisa de um mock completo

        // Apenas o stub do alvo é necessário, pois o código falha antes de procurar o ator
        when(userGateway.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deleteUserUseCase.delete(nonExistentUserId, actorId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User to be deleted not found with id: " + nonExistentUserId);

        verify(userGateway, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando o usuário ator não for encontrado")
    void shouldThrowUserNotFoundExceptionWhenActorUserNotFound() {
        // Arrange
        User professionalUser = mock(User.class);
        when(professionalUser.getId()).thenReturn(UUID.randomUUID());

        UUID nonExistentActorId = UUID.randomUUID();
        when(userGateway.findById(professionalUser.getId())).thenReturn(Optional.of(professionalUser));
        when(userGateway.findById(nonExistentActorId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deleteUserUseCase.delete(professionalUser.getId(), nonExistentActorId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Actor user not found with id: " + nonExistentActorId);

        verify(userGateway, never()).deleteById(any(UUID.class));
    }
}
