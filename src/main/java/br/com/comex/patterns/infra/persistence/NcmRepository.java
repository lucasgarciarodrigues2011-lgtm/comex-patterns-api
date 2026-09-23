package br.com.comex.patterns.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NcmRepository extends JpaRepository<NcmEntity, String> {
}
