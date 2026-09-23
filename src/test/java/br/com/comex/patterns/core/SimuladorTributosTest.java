package br.com.comex.patterns.core;

import br.com.comex.patterns.core.fiscal.SimuladorTributos;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.model.ResultadoTributos;
import br.com.comex.patterns.core.model.TipoTributo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static br.com.comex.patterns.core.Fixtures.BRITADOR;
import static br.com.comex.patterns.core.Fixtures.importacao;
import static org.assertj.core.api.Assertions.assertThat;

class SimuladorTributosTest {

    private final SimuladorTributos simulador = Fixtures.simulador();

    private static BigDecimal bd(String v) {
        return new BigDecimal(v);
    }

    @Test
    @DisplayName("Regime comum: todos os tributos devidos, ICMS calculado por dentro")
    void regimeComum() {
        ResultadoTributos r = simulador.simular(importacao(Regime.COMUM, null), BRITADOR);

        assertThat(r.getValorAduaneiro()).isEqualByComparingTo("55500.00");     // 11.100 x 5,00
        assertThat(r.tributo(TipoTributo.II).valorDevido()).isEqualByComparingTo("7770.00");
        assertThat(r.tributo(TipoTributo.IPI).valorDevido()).isEqualByComparingTo("6327.00");
        assertThat(r.tributo(TipoTributo.PIS).valorDevido()).isEqualByComparingTo("1165.50");
        assertThat(r.tributo(TipoTributo.COFINS).valorDevido()).isEqualByComparingTo("5355.75");
        assertThat(r.tributo(TipoTributo.ICMS).baseCalculo()).isEqualByComparingTo("93436.89");
        assertThat(r.tributo(TipoTributo.ICMS).valorDevido()).isEqualByComparingTo("16818.64");
        assertThat(r.getTotalDevido()).isEqualByComparingTo("37436.89");
        assertThat(r.getTotalSuspenso()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(r.getCustoTotalDesembaraco()).isEqualByComparingTo("93436.89");
    }

    @Test
    @DisplayName("Admissão Temporária (suspensão total): nada devido, tudo suspenso")
    void admissaoTemporariaSuspensaoTotal() {
        ResultadoTributos r = simulador.simular(importacao(Regime.ADMISSAO_TEMPORARIA_SUSPENSAO_TOTAL, 6), BRITADOR);

        assertThat(r.getTotalDevido()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(r.getTotalSuspenso()).isEqualByComparingTo("37436.89");
        assertThat(r.getObservacoes()).isNotEmpty();
    }

    @Test
    @DisplayName("Admissão Temporária (utilização econômica): 1% ao mês dos federais, limitado a 100%")
    void admissaoTemporariaUtilizacaoEconomica() {
        ResultadoTributos dozeMeses =
                simulador.simular(importacao(Regime.ADMISSAO_TEMPORARIA_UTILIZACAO_ECONOMICA, 12), BRITADOR);
        assertThat(dozeMeses.tributo(TipoTributo.II).valorDevido()).isEqualByComparingTo(bd("932.40"));
        assertThat(dozeMeses.tributo(TipoTributo.II).valorSuspenso()).isEqualByComparingTo(bd("6837.60"));
        assertThat(dozeMeses.tributo(TipoTributo.ICMS).valorDevido()).isEqualByComparingTo(BigDecimal.ZERO);

        ResultadoTributos duzentosMeses =
                simulador.simular(importacao(Regime.ADMISSAO_TEMPORARIA_UTILIZACAO_ECONOMICA, 200), BRITADOR);
        assertThat(duzentosMeses.tributo(TipoTributo.II).valorDevido()).isEqualByComparingTo(bd("7770.00"));
    }

    @Test
    @DisplayName("Drawback suspensão: federais suspensos, ICMS devido")
    void drawbackSuspensao() {
        ResultadoTributos r = simulador.simular(importacao(Regime.DRAWBACK_SUSPENSAO, null), BRITADOR);

        assertThat(r.getTotalDevido()).isEqualByComparingTo("16818.64");
        assertThat(r.tributo(TipoTributo.II).valorSuspenso()).isEqualByComparingTo("7770.00");
    }
}
