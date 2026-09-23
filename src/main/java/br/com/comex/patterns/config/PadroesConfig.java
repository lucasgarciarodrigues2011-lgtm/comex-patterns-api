package br.com.comex.patterns.config;

import br.com.comex.patterns.core.fiscal.CalculoTributarioStrategy;
import br.com.comex.patterns.core.fiscal.EstrategiaTributariaFactory;
import br.com.comex.patterns.core.fiscal.SimuladorTributos;
import br.com.comex.patterns.core.fiscal.estrategias.AdmissaoTemporariaSuspensaoTotalStrategy;
import br.com.comex.patterns.core.fiscal.estrategias.AdmissaoTemporariaUtilizacaoEconomicaStrategy;
import br.com.comex.patterns.core.fiscal.estrategias.DrawbackSuspensaoStrategy;
import br.com.comex.patterns.core.fiscal.estrategias.RegimeComumStrategy;
import br.com.comex.patterns.core.port.CatalogoNcm;
import br.com.comex.patterns.core.validacao.CadeiaValidacao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Liga o núcleo em Java puro ao Spring. Todos os beans daqui são Singletons gerenciados pelo container
 * (escopo padrão do Spring): uma única instância compartilhada por toda a aplicação.
 * <p>
 * Para adicionar um regime novo: crie a estratégia e declare mais um @Bean — a factory a recebe automaticamente.
 */
@Configuration
public class PadroesConfig {

    @Bean
    CalculoTributarioStrategy regimeComum() {
        return new RegimeComumStrategy();
    }

    @Bean
    CalculoTributarioStrategy admissaoTemporariaSuspensaoTotal() {
        return new AdmissaoTemporariaSuspensaoTotalStrategy();
    }

    @Bean
    CalculoTributarioStrategy admissaoTemporariaUtilizacaoEconomica() {
        return new AdmissaoTemporariaUtilizacaoEconomicaStrategy();
    }

    @Bean
    CalculoTributarioStrategy drawbackSuspensao() {
        return new DrawbackSuspensaoStrategy();
    }

    @Bean
    EstrategiaTributariaFactory estrategiaTributariaFactory(List<CalculoTributarioStrategy> estrategias) {
        return new EstrategiaTributariaFactory(estrategias);
    }

    @Bean
    SimuladorTributos simuladorTributos(EstrategiaTributariaFactory factory) {
        return new SimuladorTributos(factory);
    }

    @Bean
    CadeiaValidacao cadeiaValidacao(CatalogoNcm catalogoNcm) {
        return CadeiaValidacao.padrao(catalogoNcm);
    }
}
