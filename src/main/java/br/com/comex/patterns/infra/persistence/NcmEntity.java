package br.com.comex.patterns.infra.persistence;

import br.com.comex.patterns.core.model.NcmInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "ncm")
public class NcmEntity {

    @Id
    @Column(length = 8)
    private String codigo;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquotaIi;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquotaIpi;

    @Column(nullable = false)
    private boolean exigeLicenca;

    protected NcmEntity() {
    }

    public NcmInfo toInfo() {
        return new NcmInfo(codigo, descricao, aliquotaIi, aliquotaIpi, exigeLicenca);
    }

    public String getCodigo() {
        return codigo;
    }
}
