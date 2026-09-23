package br.com.comex.patterns.service;

import br.com.comex.patterns.core.fiscal.SimuladorTributos;
import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.NcmInfo;
import br.com.comex.patterns.core.model.ResultadoTributos;
import br.com.comex.patterns.core.model.StatusProcesso;
import br.com.comex.patterns.core.port.CotacaoCambio;
import br.com.comex.patterns.core.validacao.CadeiaValidacao;
import br.com.comex.patterns.core.validacao.ValidacaoImportacaoException;
import br.com.comex.patterns.evento.StatusProcessoAlteradoEvent;
import br.com.comex.patterns.infra.persistence.HistoricoStatus;
import br.com.comex.patterns.infra.persistence.HistoricoStatusRepository;
import br.com.comex.patterns.infra.persistence.ProcessoImportacao;
import br.com.comex.patterns.infra.persistence.ProcessoRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Padrão Facade: um ponto de entrada simples para operações que envolvem vários subsistemas —
 * cotação de câmbio (PTAX), cadeia de validação, simulador com estratégias, persistência e eventos.
 * Os controllers só conversam com esta classe.
 */
@Service
public class ImportacaoFacade {

    private final CotacaoCambio cotacaoCambio;
    private final CadeiaValidacao cadeiaValidacao;
    private final SimuladorTributos simulador;
    private final ProcessoRepository processoRepository;
    private final HistoricoStatusRepository historicoRepository;
    private final ApplicationEventPublisher eventos;

    public ImportacaoFacade(CotacaoCambio cotacaoCambio,
                            CadeiaValidacao cadeiaValidacao,
                            SimuladorTributos simulador,
                            ProcessoRepository processoRepository,
                            HistoricoStatusRepository historicoRepository,
                            ApplicationEventPublisher eventos) {
        this.cotacaoCambio = cotacaoCambio;
        this.cadeiaValidacao = cadeiaValidacao;
        this.simulador = simulador;
        this.processoRepository = processoRepository;
        this.historicoRepository = historicoRepository;
        this.eventos = eventos;
    }

    /** Valida e calcula os tributos sem gravar nada. */
    public Simulacao simular(DadosImportacao dados) {
        DadosImportacao completos = resolverCambio(dados);
        NcmInfo ncm = cadeiaValidacao.validar(completos);
        ResultadoTributos resultado = simulador.simular(completos, ncm);
        return new Simulacao(completos, ncm, resultado);
    }

    @Transactional
    public ProcessoImportacao abrirProcesso(String referencia, String importador, DadosImportacao dados) {
        if (processoRepository.existsByReferencia(referencia)) {
            throw new ValidacaoImportacaoException(List.of("Já existe um processo com a referência " + referencia + "."));
        }
        Simulacao simulacao = simular(dados);
        ProcessoImportacao processo = processoRepository.save(ProcessoImportacao.abrir(
                referencia, importador, simulacao.dados(), simulacao.ncm(), simulacao.resultado()));
        publicar(processo, null, "Processo aberto");
        return processo;
    }

    @Transactional
    public ProcessoImportacao alterarStatus(Long id, StatusProcesso novo, String observacao) {
        ProcessoImportacao processo = buscar(id);
        StatusProcesso anterior = processo.alterarStatus(novo);
        publicar(processo, anterior, observacao);
        return processo;
    }

    @Transactional(readOnly = true)
    public ProcessoImportacao buscar(Long id) {
        return processoRepository.findById(id).orElseThrow(() -> new ProcessoNaoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<ProcessoImportacao> listar(StatusProcesso status) {
        return status == null
                ? processoRepository.findAllByOrderByCriadoEmDesc()
                : processoRepository.findByStatusOrderByCriadoEmDesc(status);
    }

    @Transactional(readOnly = true)
    public List<HistoricoStatus> historico(Long id) {
        buscar(id);
        return historicoRepository.findByProcessoIdOrderByDataHoraAscIdAsc(id);
    }

    private DadosImportacao resolverCambio(DadosImportacao dados) {
        boolean semTaxa = dados.taxaCambio() == null;
        boolean moedaValida = dados.moeda() != null && dados.moeda().matches("[A-Z]{3}");
        if (semTaxa && moedaValida) {
            return dados.comTaxaCambio(cotacaoCambio.taxaVenda(dados.moeda()));
        }
        return dados;
    }

    private void publicar(ProcessoImportacao p, StatusProcesso anterior, String observacao) {
        eventos.publishEvent(new StatusProcessoAlteradoEvent(p.getId(), p.getReferencia(), p.getImportador(),
                anterior, p.getStatus(), observacao, LocalDateTime.now()));
    }
}
