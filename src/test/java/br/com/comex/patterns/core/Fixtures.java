package br.com.comex.patterns.core;

import br.com.comex.patterns.core.fiscal.EstrategiaTributariaFactory;
import br.com.comex.patterns.core.fiscal.SimuladorTributos;
import br.com.comex.patterns.core.fiscal.estrategias.AdmissaoTemporariaSuspensaoTotalStrategy;
import br.com.comex.patterns.core.fiscal.estrategias.AdmissaoTemporariaUtilizacaoEconomicaStrategy;
import br.com.comex.patterns.core.fiscal.estrategias.DrawbackSuspensaoStrategy;
import br.com.comex.patterns.core.fiscal.estrategias.RegimeComumStrategy;
import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.Incoterm;
import br.com.comex.patterns.core.model.NcmInfo;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.port.CatalogoNcm;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Dados e objetos reutilizados nos testes do núcleo (sem Spring). */
public final class Fixtures {

    public static final NcmInfo BRITADOR =
            new NcmInfo("84742010", "Britadores", new BigDecimal("14.00"), new BigDecimal("10.00"), false);
    public static final NcmInfo MEDICAMENTO =
            new NcmInfo("30049099", "Medicamentos", new BigDecimal("8.00"), BigDecimal.ZERO, true);

    public static final CatalogoNcm CATALOGO = codigo ->
            Optional.ofNullable(Map.of(BRITADOR.codigo(), BRITADOR, MEDICAMENTO.codigo(), MEDICAMENTO).get(codigo));

    private Fixtures() {
    }

    public static SimuladorTributos simulador() {
        return new SimuladorTributos(new EstrategiaTributariaFactory(List.of(
                new RegimeComumStrategy(),
                new AdmissaoTemporariaSuspensaoTotalStrategy(),
                new AdmissaoTemporariaUtilizacaoEconomicaStrategy(),
                new DrawbackSuspensaoStrategy())));
    }

    /** US$ 10.000 FOB + US$ 1.000 frete + US$ 100 seguro, câmbio 5,00, despesas R$ 500, ICMS 18%. */
    public static DadosImportacao importacao(Regime regime, Integer meses) {
        return importacao(Incoterm.FOB, "1000", "100", regime, meses, "84742010", false);
    }

    public static DadosImportacao importacao(Incoterm incoterm, String frete, String seguro, Regime regime,
                                             Integer meses, String ncm, boolean possuiLicenca) {
        return new DadosImportacao(ncm, incoterm, "USD", new BigDecimal("5.00"), new BigDecimal("10000"),
                new BigDecimal(frete), new BigDecimal(seguro), new BigDecimal("500"), new BigDecimal("18"),
                regime, meses, possuiLicenca);
    }
}
