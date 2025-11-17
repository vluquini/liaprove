package com.lia.liaprove.infrastructure.services;

import com.lia.liaprove.application.gateways.user.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHasherImpl implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public PasswordHasherImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}