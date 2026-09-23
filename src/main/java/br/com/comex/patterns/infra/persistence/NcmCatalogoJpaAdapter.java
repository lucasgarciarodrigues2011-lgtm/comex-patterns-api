package br.com.comex.patterns.infra.persistence;

import br.com.comex.patterns.core.model.NcmInfo;
import br.com.comex.patterns.core.port.CatalogoNcm;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Padrão Adapter: adapta o repositório JPA (Spring Data) à porta {@link CatalogoNcm} do domínio.
 * Trocar o banco por uma API da Receita, por exemplo, exigiria só um novo adaptador.
 */
@Component
public class NcmCatalogoJpaAdapter implements CatalogoNcm {

    private final NcmRepository repository;

    public NcmCatalogoJpaAdapter(NcmRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<NcmInfo> buscar(String codigo) {
        return repository.findById(codigo).map(NcmEntity::toInfo);
    }
}
