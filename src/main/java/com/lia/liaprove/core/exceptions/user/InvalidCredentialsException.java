package com.lia.liaprove.core.exceptions.user;

/**
 * Exceção lançada quando as credenciais fornecidas (ex: senha antiga) são inválidas durante uma operação.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
