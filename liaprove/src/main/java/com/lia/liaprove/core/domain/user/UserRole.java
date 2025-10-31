package com.lia.liaprove.core.domain.user;

/**
 * Enum que define os papéis (roles) principais que um usuário pode ter no sistema.
 * Os papéis determinam as permissões e o tipo de interação do usuário com a plataforma.
 */
public enum UserRole {
    ADMIN, RECRUITER, PROFESSIONAL;

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isRecruiter() {
        return this == RECRUITER;
    }

    public boolean isProfessional() {
        return this == PROFESSIONAL;
    }

}
