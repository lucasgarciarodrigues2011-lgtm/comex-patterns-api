package br.com.comex.patterns.core;

import br.com.comex.patterns.core.fiscal.EstrategiaTributariaFactory;
import br.com.comex.patterns.core.fiscal.ParametrosFiscais;
import br.com.comex.patterns.core.fiscal.estrategias.DrawbackSuspensaoStrategy;
import br.com.comex.patterns.core.fiscal.estrategias.RegimeComumStrategy;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.model.ResultadoTributos;
import br.com.comex.patterns.core.model.StatusProcesso;
import br.com.comex.patterns.core.model.TransicaoStatusInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PadroesCriacionaisTest {

    @Test
    @DisplayName("Singleton: sempre a mesma instância de ParametrosFiscais")
    void singleton() {
        assertThat(ParametrosFiscais.valueOf("INSTANCIA")).isSameAs(ParametrosFiscais.INSTANCIA);
        assertThat(ParametrosFiscais.values()).hasSize(1);
    }

    @Test
    @DisplayName("Factory: devolve a estratégia do regime e recusa regime sem estratégia")
    void factory() {
        EstrategiaTributariaFactory factory = new EstrategiaTributariaFactory(
                List.of(new RegimeComumStrategy(), new DrawbackSuspensaoStrategy()));

        assertThat(factory.para(Regime.COMUM)).isInstanceOf(RegimeComumStrategy.class);
        assertThatThrownBy(() -> factory.para(Regime.ADMISSAO_TEMPORARIA_SUSPENSAO_TOTAL))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new EstrategiaTributariaFactory(
                List.of(new RegimeComumStrategy(), new RegimeComumStrategy())))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Builder: exige os campos obrigatórios")
    void builder() {
        assertThatThrownBy(() -> ResultadoTributos.builder().regime(Regime.COMUM).taxaCambio(BigDecimal.ONE).build())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Status: só permite transições do fluxo aduaneiro")
    void transicoes() {
        assertThat(StatusProcesso.REGISTRADO.transicionarPara(StatusProcesso.CANAL_VERDE))
                .isEqualTo(StatusProcesso.CANAL_VERDE);
        assertThatThrownBy(() -> StatusProcesso.ABERTO.transicionarPara(StatusProcesso.DESEMBARACADO))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
        assertThat(StatusProcesso.ENTREGUE.proximosPermitidos()).isEmpty();
    }
}
