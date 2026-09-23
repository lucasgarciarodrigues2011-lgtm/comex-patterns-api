package br.com.comex.patterns.core.validacao;

import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.NcmInfo;

import java.util.ArrayList;
import java.util.List;

/** Estado compartilhado entre os elos da cadeia de validação. */
public class ContextoValidacao {

    private final DadosImportacao dados;
    private final List<String> erros = new ArrayList<>();
    private NcmInfo ncm;

    public ContextoValidacao(DadosImportacao dados) {
        this.dados = dados;
    }

    public DadosImportacao dados() {
        return dados;
    }

    public NcmInfo ncm() {
        return ncm;
    }

    public void ncm(NcmInfo ncm) {
        this.ncm = ncm;
    }

    public void erro(String mensagem) {
        erros.add(mensagem);
    }

    public List<String> erros() {
        return List.copyOf(erros);
    }

    public boolean temErros() {
        return !erros.isEmpty();
    }
}
