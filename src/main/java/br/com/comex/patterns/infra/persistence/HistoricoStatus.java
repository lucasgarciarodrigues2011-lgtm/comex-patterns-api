package br.com.comex.patterns.infra.persistence;

import br.com.comex.patterns.core.model.StatusProcesso;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_status")
public class HistoricoStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long processoId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusProcesso statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusProcesso statusNovo;

    private String observacao;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected HistoricoStatus() {
    }

    public HistoricoStatus(Long processoId, StatusProcesso statusAnterior, StatusProcesso statusNovo,
                           String observacao, LocalDateTime dataHora) {
        this.processoId = processoId;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.observacao = observacao;
        this.dataHora = dataHora;
    }

    public Long getId() { return id; }
    public Long getProcessoId() { return processoId; }
    public StatusProcesso getStatusAnterior() { return statusAnterior; }
    public StatusProcesso getStatusNovo() { return statusNovo; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getDataHora() { return dataHora; }
}
