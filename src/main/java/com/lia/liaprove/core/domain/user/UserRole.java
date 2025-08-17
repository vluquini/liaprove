package com.lia.liaprove.core.domain.user;

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
