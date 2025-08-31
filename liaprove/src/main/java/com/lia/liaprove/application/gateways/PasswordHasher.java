package com.lia.liaprove.application.gateways;

public interface PasswordHasher {
    String hash(String raw);
    boolean matches(String raw, String hash);
}
