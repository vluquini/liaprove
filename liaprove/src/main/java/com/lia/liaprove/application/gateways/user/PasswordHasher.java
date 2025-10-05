package com.lia.liaprove.application.gateways.user;

public interface PasswordHasher {
    String hash(String raw);
    boolean matches(String raw, String hash);
}
