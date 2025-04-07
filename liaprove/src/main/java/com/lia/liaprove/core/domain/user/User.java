package com.lia.liaprove.core.domain.user;

import java.time.LocalDateTime;

public abstract class User {
    private String name;
    private String email;
    private String password;
    // Título da profissão que o usuário exerce
    private String occupation;
    private String bio;
    private ExperienceLevel experienceLevel;
    private UserRole role;
    // Utilizado para medir o peso do voto nas questões
    private Integer voteWeight;
    // Média de pontuação nas avaliações realizadas
    private Float averageScore;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLogin;

}
