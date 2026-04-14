package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.exceptions.user.InvalidUserDataException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfileUseCaseImplTest {

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private UpdateUserProfileUseCaseImpl updateUserProfileUseCase;

    @Test
    @DisplayName("Deve atualizar o perfil do usuário com dados válidos")
    void deveAtualizarPerfilDoUsuarioComDadosValidos() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String newName = "John Snow Stark";
        String newEmail = "john.stark@winterfell.com";
        String newOccupation = "King in the North";
        String newBio = "The White Wolf";
        ExperienceLevel newExperience = ExperienceLevel.SENIOR;
        List<String> hardSkills = List.of("Leadership", "Architecture");
        List<String> softSkills = List.of("Communication");

        User existingUser = mock(User.class);
        when(existingUser.getEmail()).thenReturn("john.snow@thewall.com");
        when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userGateway.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userGateway.findByEmail(newEmail)).thenReturn(Optional.empty());


        // Act
        User updatedUser = updateUserProfileUseCase.updateProfile(userId, newName, newEmail, newOccupation, newBio, newExperience, hardSkills, softSkills);

        // Assert
        assertThat(updatedUser).isEqualTo(existingUser);
        verify(existingUser).updateProfile(newName, newEmail, newOccupation, newBio, newExperience);
        verify(userGateway).save(existingUser);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar usuário inexistente")
    void deveLancarExcecaoAoTentarAtualizarUsuarioInexistente() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userGateway.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updateUserProfileUseCase.updateProfile(userId, "New Name", "new@email.com", "New Occ", "New Bio", ExperienceLevel.JUNIOR, null, null))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: " + userId);

        verify(userGateway, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se nenhum campo for fornecido para atualização")
    void deveLancarExcecaoSeNenhumCampoFornecido() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> updateUserProfileUseCase.updateProfile(userId, null, null, null, null, null, null, null))
                .isInstanceOf(InvalidUserDataException.class)
                .hasMessage("At least one field must be provided to update the profile");

        assertThatThrownBy(() -> updateUserProfileUseCase.updateProfile(userId, " ", null, " ", null, null, null, null))
                .isInstanceOf(InvalidUserDataException.class)
                .hasMessage("At least one field must be provided to update the profile");

        verify(userGateway, never()).findById(any());
        verify(userGateway, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve permitir limpar a bio com uma string vazia")
    void devePermitirLimparBioComStringVazia() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User existingUser = mock(User.class);

        when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userGateway.save(existingUser)).thenReturn(existingUser);

        // Act
        updateUserProfileUseCase.updateProfile(userId, null, null, null, "", null, null, null);

        // Assert
        verify(existingUser).updateProfile(null, null, null, "", null);
        verify(userGateway).save(existingUser);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar usar um email que já pertence a outro usuário")
    void deveLancarExcecaoAoTentarUsarEmailDeOutroUsuario() {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        String newEmail = "taken@example.com";

        User userToUpdate = mock(User.class);
        when(userToUpdate.getEmail()).thenReturn("original@example.com");

        User otherUserWithEmail = mock(User.class);
        when(otherUserWithEmail.getId()).thenReturn(userId2);

        when(userGateway.findById(userId1)).thenReturn(Optional.of(userToUpdate));
        when(userGateway.findByEmail(newEmail)).thenReturn(Optional.of(otherUserWithEmail));

        // Act & Assert
        assertThatThrownBy(() -> updateUserProfileUseCase.updateProfile(userId1, "New Name", newEmail, null, null, null, null, null))
                .isInstanceOf(InvalidUserDataException.class)
                .hasMessage("Email already registered by another user.");

        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Não deve verificar email se o email não for alterado")
    void naoDeveVerificarEmailSeNaoForAlterado() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String sameEmail = "same@example.com";

        User existingUser = mock(User.class);
        when(existingUser.getEmail()).thenReturn(sameEmail);
        when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userGateway.save(existingUser)).thenReturn(existingUser);

        // Act
        updateUserProfileUseCase.updateProfile(userId, "New Name", sameEmail, null, null, null, null, null);

        // Assert
        verify(userGateway, never()).findByEmail(sameEmail);
        verify(userGateway).save(existingUser);
    }

    @Test
    @DisplayName("Deve atualizar hard skills e soft skills de um profissional")
    void deveAtualizarSkillsDeUmProfissional() {
        UUID userId = UUID.randomUUID();
        UserProfessional professional = new UserProfessional();
        professional.setId(userId);
        professional.setEmail("professional@example.com");
        professional.setHardSkills(List.of("Java"));
        professional.setSoftSkills(List.of("Focus"));

        when(userGateway.findById(userId)).thenReturn(Optional.of(professional));
        when(userGateway.save(professional)).thenReturn(professional);

        User updatedUser = updateUserProfileUseCase.updateProfile(
                userId,
                null,
                null,
                null,
                null,
                null,
                List.of("Java", "Spring Boot", "Java"),
                List.of("Communication", "Leadership")
        );

        assertThat(updatedUser).isSameAs(professional);
        assertThat(professional.getHardSkills()).containsExactly("Java", "Spring Boot");
        assertThat(professional.getSoftSkills()).containsExactly("Communication", "Leadership");
        verify(userGateway).save(professional);
    }
}
