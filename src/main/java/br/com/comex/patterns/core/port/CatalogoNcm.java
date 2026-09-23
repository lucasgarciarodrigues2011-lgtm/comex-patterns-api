package br.com.comex.patterns.core.port;

import br.com.comex.patterns.core.model.NcmInfo;

import java.util.Optional;

/**
 * Porta de saída do domínio para consultar NCMs.
 * O núcleo não sabe se os dados vêm de banco, arquivo ou API — quem decide é o adaptador.
 */
public interface CatalogoNcm {

    Optional<NcmInfo> buscar(String codigo);
}
