package br.com.comex.patterns.evento;

import br.com.comex.patterns.core.model.StatusProcesso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

/**
 * Observer 2: avisa o cliente nos marcos que importam para ele.
 * Aqui a notificação é só um log; em produção poderia ser e-mail, WhatsApp etc. —
 * e nada no fluxo principal precisaria mudar para isso.
 */
@Component
public class NotificacaoClienteListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoClienteListener.class);

    private static final Set<StatusProcesso> MARCOS = EnumSet.of(
            StatusProcesso.REGISTRADO, StatusProcesso.CANAL_VERDE, StatusProcesso.CANAL_AMARELO,
            StatusProcesso.CANAL_VERMELHO, StatusProcesso.CANAL_CINZA, StatusProcesso.EM_EXIGENCIA,
            StatusProcesso.DESEMBARACADO, StatusProcesso.ENTREGUE);

    @EventListener
    public void notificar(StatusProcessoAlteradoEvent evento) {
        if (MARCOS.contains(evento.novo())) {
            log.info("[NOTIFICAÇÃO] {} | Processo {}: status atualizado para {}",
                    evento.importador(), evento.referencia(), evento.novo());
        }
    }
}
