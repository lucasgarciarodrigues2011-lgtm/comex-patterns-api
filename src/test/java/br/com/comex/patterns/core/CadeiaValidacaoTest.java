package br.com.comex.patterns.core;

import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.Incoterm;
import br.com.comex.patterns.core.model.NcmInfo;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.validacao.CadeiaValidacao;
import br.com.comex.patterns.core.validacao.ValidacaoImportacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static br.com.comex.patterns.core.Fixtures.importacao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CadeiaValidacaoTest {

    private final CadeiaValidacao cadeia = CadeiaValidacao.padrao(Fixtures.CATALOGO);

    @Test
    @DisplayName("Importação válida passa por todos os elos e devolve a NCM (aceita NCM com pontos)")
    void valida() {
        NcmInfo ncm = cadeia.validar(importacao(Incoterm.FOB, "1000", "100", Regime.COMUM, null, "8474.20.10", false));
        assertThat(ncm).isEqualTo(Fixtures.BRITADOR);
    }

    @Test
    @DisplayName("CIF com frete e seguro informados: acusa os dois problemas de uma vez")
    void incotermIncoerente() {
        assertThatThrownBy(() -> cadeia.validar(
                importacao(Incoterm.CIF, "1000", "100", Regime.COMUM, null, "84742010", false)))
                .isInstanceOfSatisfying(ValidacaoImportacaoException.class,
                        e -> assertThat(e.getErros()).hasSize(2).allMatch(m -> m.contains("CIF")));
    }

    @Test
    @DisplayName("NCM sujeita a licença sem LI/LPCO é barrada")
    void semLicenca() {
        assertThatThrownBy(() -> cadeia.validar(
                importacao(Incoterm.FOB, "1000", "0", Regime.COMUM, null, "30049099", false)))
                .isInstanceOfSatisfying(ValidacaoImportacaoException.class,
                        e -> {
                            assertThat(e.getErros()).hasSize(1);
                            assertThat(e.getErros().get(0)).contains("licenciamento");
                        });
    }

    @Test
    @DisplayName("Erros de elos diferentes são acumulados; NCM inexistente interrompe a cadeia")
    void acumulaErros() {
        assertThatThrownBy(() -> cadeia.validar(importacao(Incoterm.FOB, "1000", "0",
                Regime.ADMISSAO_TEMPORARIA_SUSPENSAO_TOTAL, null, "99999999", false)))
                .isInstanceOfSatisfying(ValidacaoImportacaoException.class,
                        e -> assertThat(e.getErros()).hasSize(2));
    }

    @Test
    @DisplayName("Sem os campos básicos, o primeiro elo lista tudo e para a cadeia")
    void camposObrigatorios() {
        DadosImportacao vazia = new DadosImportacao("123", null, "usd", null, null, null, null, null, null,
                null, null, false);
        assertThatThrownBy(() -> cadeia.validar(vazia))
                .isInstanceOfSatisfying(ValidacaoImportacaoException.class,
                        e -> assertThat(e.getErros()).hasSize(7));
    }
}
