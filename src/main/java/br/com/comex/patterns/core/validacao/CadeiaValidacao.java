package br.com.comex.patterns.core.validacao;

import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.NcmInfo;
import br.com.comex.patterns.core.port.CatalogoNcm;
import br.com.comex.patterns.core.validacao.validadores.CamposObrigatoriosValidador;
import br.com.comex.patterns.core.validacao.validadores.IncotermValidador;
import br.com.comex.patterns.core.validacao.validadores.LicenciamentoValidador;
import br.com.comex.patterns.core.validacao.validadores.NcmCadastradaValidador;
import br.com.comex.patterns.core.validacao.validadores.RegimeValidador;

/** Monta a cadeia na ordem em que as regras fazem sentido e executa a validação. */
public class CadeiaValidacao {

    private final ValidadorImportacao primeiro;

    public CadeiaValidacao(ValidadorImportacao primeiro) {
        this.primeiro = primeiro;
    }

    /** Cadeia padrão: campos obrigatórios -> incoterm -> regime -> NCM -> licenciamento. */
    public static CadeiaValidacao padrao(CatalogoNcm catalogoNcm) {
        ValidadorImportacao inicio = new CamposObrigatoriosValidador();
        inicio.encadear(new IncotermValidador())
                .encadear(new RegimeValidador())
                .encadear(new NcmCadastradaValidador(catalogoNcm))
                .encadear(new LicenciamentoValidador());
        return new CadeiaValidacao(inicio);
    }

    /**
     * Valida a importação e devolve os dados da NCM encontrados no caminho.
     *
     * @throws ValidacaoImportacaoException com todos os erros encontrados
     */
    public NcmInfo validar(DadosImportacao dados) {
        ContextoValidacao contexto = new ContextoValidacao(dados);
        primeiro.validar(contexto);
        if (contexto.temErros()) {
            throw new ValidacaoImportacaoException(contexto.erros());
        }
        return contexto.ncm();
    }
}
