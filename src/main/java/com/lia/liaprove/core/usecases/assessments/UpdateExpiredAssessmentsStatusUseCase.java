package com.lia.liaprove.core.usecases.assessments;

/**
 * Caso de uso para ser executado por um scheduler, responsável por atualizar o status
 * de avaliações personalizadas que já expiraram.
 */
public interface UpdateExpiredAssessmentsStatusUseCase {

    /**
     * Encontra todas as avaliações ativas com data de expiração no passado
     * e atualiza seu status para DEACTIVATED.
     */
    void execute();
}
