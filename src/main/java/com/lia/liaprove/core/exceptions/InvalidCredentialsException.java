package com.lia.liaprove.core.exceptions;

/**
 * Exceção lançada quando as credenciais fornecidas (ex: senha antiga) são inválidas durante uma operação.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
