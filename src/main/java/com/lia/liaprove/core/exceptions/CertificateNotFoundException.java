package com.lia.liaprove.core.exceptions;

/**
 * Exceção lançada quando um certificado solicitado não é encontrado.
 */
public class CertificateNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CertificateNotFoundException() {
    super();
  }

  public CertificateNotFoundException(String message) {
    super(message);
  }

  public CertificateNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public CertificateNotFoundException(Throwable cause) {
    super(cause);
  }
}
