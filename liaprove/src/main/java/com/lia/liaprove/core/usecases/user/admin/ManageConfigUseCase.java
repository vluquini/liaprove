package com.lia.liaprove.core.usecases.user.admin;

import java.util.Map;
import java.util.UUID;

/**
 * Use case para gerenciamento dinâmico de parâmetros/configurações do sistema.
 *
 * <p>Permite alterar e consultar valores de configuração relevantes em tempo de execução
 * (ex.: roleMultiplier, laplaceAlpha, populationSize, GeneticConfig defaults, etc.).
 * Operações úteis para tuning, testes e demonstrações.
 */
public interface ManageConfigUseCase {
    /**
     * Atualiza (ou cria) uma chave de configuração com o valor informado.
     *
     * @param key     nome da configuração (ex.: "bayes.laplaceAlpha")
     * @param value   novo valor (representado como String; parsing será responsabilidade da implementação)
     * @param adminId id do admin/sistema que realizou a alteração
     */
    void updateConfig(String key, String value, UUID adminId);

    /**
     * Retorna o valor da configuração solicitada.
     *
     * @param  key nome da configuração
     * @return valor da configuração (String) ou {@code null} se não existir
     */
    String getConfig(String key);

    /**
     * Retorna todas as configurações atualmente disponíveis/registradas como um mapa
     * (chave -> valor).
     *
     * @return mapa imutável (ou snapshot) das configurações
     */
    Map<String, String> listAllConfigs();
}

