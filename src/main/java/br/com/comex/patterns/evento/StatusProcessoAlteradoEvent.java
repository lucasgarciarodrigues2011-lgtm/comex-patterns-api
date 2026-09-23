package br.com.comex.patterns.evento;

import br.com.comex.patterns.core.model.StatusProcesso;

import java.time.LocalDateTime;

/**
 * Evento publicado sempre que um processo muda de status (inclusive na abertura, com anterior = null).
 * Quem publica não conhece quem escuta — base do padrão Observer.
 */
public record StatusProcessoAlteradoEvent(Long processoId,
                                          String referencia,
                                          String importador,
                                          StatusProcesso anterior,
                                          StatusProcesso novo,
                                          String observacao,
                                          LocalDateTime dataHora) {
}
