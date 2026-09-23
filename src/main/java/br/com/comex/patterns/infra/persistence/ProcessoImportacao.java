package br.com.comex.patterns.infra.persistence;

import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.Incoterm;
import br.com.comex.patterns.core.model.NcmInfo;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.model.ResultadoTributos;
import br.com.comex.patterns.core.model.StatusProcesso;
import br.com.comex.patterns.core.model.TributoCalculado;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "processo_importacao")
public class ProcessoImportacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String referencia;

    @Column(nullable = false)
    private String importador;

    @Column(nullable = false, length = 8)
    private String ncm;

    private String ncmDescricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Incoterm incoterm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Regime regime;

    @Column(nullable = false, length = 3)
    private String moeda;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal taxaCambio;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valorMercadoria;

    @Column(precision = 19, scale = 2)
    private BigDecimal frete;

    @Column(precision = 19, scale = 2)
    private BigDecimal seguro;

    @Column(precision = 19, scale = 2)
    private BigDecimal despesasAduaneiras;

    @Column(precision = 5, scale = 2)
    private BigDecimal aliquotaIcms;

    private Integer mesesPermanencia;

    private boolean possuiLicenca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusProcesso status;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorAduaneiro;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalDevido;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalSuspenso;

    @Column(precision = 19, scale = 2)
    private BigDecimal custoTotalDesembaraco;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "processo_tributo", joinColumns = @JoinColumn(name = "processo_id"))
    private List<TributoEmbeddable> tributos = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    protected ProcessoImportacao() {
    }

    public static ProcessoImportacao abrir(String referencia, String importador, DadosImportacao dados,
                                           NcmInfo ncm, ResultadoTributos resultado) {
        ProcessoImportacao p = new ProcessoImportacao();
        p.referencia = referencia;
        p.importador = importador;
        p.ncm = ncm.codigo();
        p.ncmDescricao = ncm.descricao();
        p.incoterm = dados.incoterm();
        p.regime = dados.regime();
        p.moeda = dados.moeda();
        p.taxaCambio = dados.taxaCambio();
        p.valorMercadoria = dados.valorMercadoria();
        p.frete = dados.frete();
        p.seguro = dados.seguro();
        p.despesasAduaneiras = dados.despesasAduaneiras();
        p.aliquotaIcms = dados.aliquotaIcms();
        p.mesesPermanencia = dados.mesesPermanencia();
        p.possuiLicenca = dados.possuiLicenca();
        p.status = StatusProcesso.ABERTO;
        p.valorAduaneiro = resultado.getValorAduaneiro();
        p.totalDevido = resultado.getTotalDevido();
        p.totalSuspenso = resultado.getTotalSuspenso();
        p.custoTotalDesembaraco = resultado.getCustoTotalDesembaraco();
        resultado.getTributos().forEach(t -> p.tributos.add(TributoEmbeddable.de(t)));
        p.criadoEm = LocalDateTime.now();
        return p;
    }

    /** Muda o status respeitando as transições permitidas; devolve o status anterior. */
    public StatusProcesso alterarStatus(StatusProcesso novo) {
        StatusProcesso anterior = this.status;
        this.status = anterior.transicionarPara(novo);
        this.atualizadoEm = LocalDateTime.now();
        return anterior;
    }

    public Long getId() { return id; }
    public String getReferencia() { return referencia; }
    public String getImportador() { return importador; }
    public String getNcm() { return ncm; }
    public String getNcmDescricao() { return ncmDescricao; }
    public Incoterm getIncoterm() { return incoterm; }
    public Regime getRegime() { return regime; }
    public String getMoeda() { return moeda; }
    public BigDecimal getTaxaCambio() { return taxaCambio; }
    public BigDecimal getValorMercadoria() { return valorMercadoria; }
    public BigDecimal getFrete() { return frete; }
    public BigDecimal getSeguro() { return seguro; }
    public StatusProcesso getStatus() { return status; }
    public BigDecimal getValorAduaneiro() { return valorAduaneiro; }
    public BigDecimal getTotalDevido() { return totalDevido; }
    public BigDecimal getTotalSuspenso() { return totalSuspenso; }
    public BigDecimal getCustoTotalDesembaraco() { return custoTotalDesembaraco; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }

    public List<TributoCalculado> getTributos() {
        return tributos.stream().map(TributoEmbeddable::toTributo).toList();
    }
}
