package br.com.comex.patterns.core.validacao.validadores;

import br.com.comex.patterns.core.model.NcmInfo;
import br.com.comex.patterns.core.port.CatalogoNcm;
import br.com.comex.patterns.core.validacao.ContextoValidacao;
import br.com.comex.patterns.core.validacao.ValidadorImportacao;

import java.util.Optional;

/**
 * Busca a NCM no catálogo e guarda no contexto para os próximos elos e para o cálculo.
 * Se a NCM não existir, interrompe a cadeia: as regras seguintes dependem dela.
 */
public class NcmCadastradaValidador extends ValidadorImportacao {

    private final CatalogoNcm catalogo;

    public NcmCadastradaValidador(CatalogoNcm catalogo) {
        this.catalogo = catalogo;
    }

    @Override
    protected boolean verificar(ContextoValidacao contexto) {
        String codigo = contexto.dados().ncm().replace(".", "");
        Optional<NcmInfo> ncm = catalogo.buscar(codigo);
        if (ncm.isEmpty()) {
            contexto.erro("NCM %s não encontrada no catálogo.".formatted(codigo));
            return false;
        }
        contexto.ncm(ncm.get());
        return true;
    }
}
