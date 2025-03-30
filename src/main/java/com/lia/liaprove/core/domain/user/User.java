package com.lia.liaprove.core.domain.user;

import java.time.LocalDateTime;

public abstract class User {
    private String name;
    private String email;
    private String password;
    private String bio;
    private ExperienceLevel experienceLevel;
    private Integer voteWeight;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLogin;


}
