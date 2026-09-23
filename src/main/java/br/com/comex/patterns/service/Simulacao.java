package br.com.comex.patterns.service;

import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.NcmInfo;
import br.com.comex.patterns.core.model.ResultadoTributos;

/** Resultado de uma simulação: os dados efetivamente usados (com a taxa resolvida), a NCM e os tributos. */
public record Simulacao(DadosImportacao dados, NcmInfo ncm, ResultadoTributos resultado) {
}
