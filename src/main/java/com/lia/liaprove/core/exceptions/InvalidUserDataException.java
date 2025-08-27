package com.lia.liaprove.core.exceptions;

/**
* Exceção lançada quando os dados fornecidos para criação/atualização de um usuário são inválidos.
*/
public class InvalidUserDataException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidUserDataException() {
        super();
    }

    public InvalidUserDataException(String message) {
        super(message);
    }

    public InvalidUserDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserDataException(Throwable cause) {
        super(cause);
    }
}
