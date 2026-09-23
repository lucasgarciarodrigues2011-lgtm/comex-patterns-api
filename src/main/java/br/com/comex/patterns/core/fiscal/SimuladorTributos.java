package br.com.comex.patterns.core.fiscal;

import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.NcmInfo;
import br.com.comex.patterns.core.model.ResultadoTributos;
import br.com.comex.patterns.core.model.TipoTributo;
import br.com.comex.patterns.core.model.TributoCalculado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

/**
 * Calcula os tributos de importação. As fórmulas de base de cálculo são fixas;
 * a parcela devida de cada tributo vem da {@link CalculoTributarioStrategy} do regime.
 * <p>
 * Fórmulas (simplificadas para fins didáticos):
 * <ul>
 *   <li>Valor aduaneiro (VA) = (mercadoria + frete + seguro) x taxa de câmbio</li>
 *   <li>II = VA x alíquota II</li>
 *   <li>IPI = (VA + II) x alíquota IPI</li>
 *   <li>PIS = VA x 2,10% | COFINS = VA x 9,65%</li>
 *   <li>ICMS "por dentro" = (VA + II + IPI + PIS + COFINS + despesas) / (1 - alíquota) x alíquota</li>
 * </ul>
 */
public class SimuladorTributos {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final EstrategiaTributariaFactory factory;
    private final ParametrosFiscais parametros = ParametrosFiscais.INSTANCIA;

    public SimuladorTributos(EstrategiaTributariaFactory factory) {
        this.factory = factory;
    }

    public ResultadoTributos simular(DadosImportacao dados, NcmInfo ncm) {
        CalculoTributarioStrategy estrategia = factory.para(dados.regime());

        BigDecimal valorAduaneiro = calcularValorAduaneiro(dados);
        BigDecimal despesas = valorOuZero(dados.despesasAduaneiras());

        Map<TipoTributo, BigDecimal> base = new EnumMap<>(TipoTributo.class);
        Map<TipoTributo, BigDecimal> aliquota = new EnumMap<>(TipoTributo.class);
        Map<TipoTributo, BigDecimal> integral = new EnumMap<>(TipoTributo.class);

        registrar(TipoTributo.II, valorAduaneiro, ncm.aliquotaIi(), base, aliquota, integral);
        registrar(TipoTributo.IPI, valorAduaneiro.add(integral.get(TipoTributo.II)), ncm.aliquotaIpi(),
                base, aliquota, integral);
        registrar(TipoTributo.PIS, valorAduaneiro, parametros.aliquotaPis(), base, aliquota, integral);
        registrar(TipoTributo.COFINS, valorAduaneiro, parametros.aliquotaCofins(), base, aliquota, integral);

        BigDecimal aliquotaIcms = valorOuZero(dados.aliquotaIcms());
        BigDecimal somaParaIcms = valorAduaneiro
                .add(integral.get(TipoTributo.II))
                .add(integral.get(TipoTributo.IPI))
                .add(integral.get(TipoTributo.PIS))
                .add(integral.get(TipoTributo.COFINS))
                .add(despesas);
        BigDecimal fatorPorDentro = BigDecimal.ONE.subtract(aliquotaIcms.divide(CEM, 10, RoundingMode.HALF_UP));
        BigDecimal baseIcms = parametros.arredondar(somaParaIcms.divide(fatorPorDentro, 10, RoundingMode.HALF_UP));
        registrar(TipoTributo.ICMS, baseIcms, aliquotaIcms, base, aliquota, integral);

        ResultadoTributos.Builder resultado = ResultadoTributos.builder()
                .regime(dados.regime())
                .taxaCambio(dados.taxaCambio())
                .valorAduaneiro(valorAduaneiro)
                .despesasAduaneiras(despesas)
                .observacao(estrategia.observacao(dados));

        for (TipoTributo tipo : TipoTributo.values()) {
            BigDecimal valorIntegral = integral.get(tipo);
            BigDecimal devido = parametros.arredondar(valorIntegral.multiply(estrategia.fracaoDevida(tipo, dados)));
            resultado.tributo(new TributoCalculado(tipo, base.get(tipo), aliquota.get(tipo),
                    valorIntegral, devido, valorIntegral.subtract(devido)));
        }
        return resultado.build();
    }

    public BigDecimal calcularValorAduaneiro(DadosImportacao dados) {
        BigDecimal valorEstrangeiro = dados.valorMercadoria()
                .add(valorOuZero(dados.frete()))
                .add(valorOuZero(dados.seguro()));
        return parametros.arredondar(valorEstrangeiro.multiply(dados.taxaCambio()));
    }

    private void registrar(TipoTributo tipo, BigDecimal baseCalculo, BigDecimal aliq,
                           Map<TipoTributo, BigDecimal> base,
                           Map<TipoTributo, BigDecimal> aliquota,
                           Map<TipoTributo, BigDecimal> integral) {
        base.put(tipo, parametros.arredondar(baseCalculo));
        aliquota.put(tipo, aliq);
        integral.put(tipo, parametros.aplicarPercentual(baseCalculo, aliq));
    }

    private static BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}
