package br.com.comex.patterns.core.validacao.validadores;

import br.com.comex.patterns.core.validacao.ContextoValidacao;
import br.com.comex.patterns.core.validacao.ValidadorImportacao;

/** Mercadorias sujeitas a controle administrativo exigem licença (LI/LPCO) antes do registro. */
public class LicenciamentoValidador extends ValidadorImportacao {

    @Override
    protected boolean verificar(ContextoValidacao contexto) {
        if (contexto.ncm().exigeLicenca() && !contexto.dados().possuiLicenca()) {
            contexto.erro("NCM %s exige licenciamento (LI/LPCO) e nenhuma licença foi informada."
                    .formatted(contexto.ncm().codigo()));
        }
        return true;
    }
}
