package br.com.comex.patterns.evento;

import br.com.comex.patterns.infra.persistence.HistoricoStatus;
import br.com.comex.patterns.infra.persistence.HistoricoStatusRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Observer 1: grava a trilha de auditoria de cada mudança de status. */
@Component
public class HistoricoStatusListener {

    private final HistoricoStatusRepository repository;

    public HistoricoStatusListener(HistoricoStatusRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void registrar(StatusProcessoAlteradoEvent evento) {
        repository.save(new HistoricoStatus(evento.processoId(), evento.anterior(), evento.novo(),
                evento.observacao(), evento.dataHora()));
    }
}
