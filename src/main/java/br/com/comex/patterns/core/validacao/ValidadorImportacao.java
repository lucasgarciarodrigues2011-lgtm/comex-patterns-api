package br.com.comex.patterns.core.validacao;

/**
 * Padrão Chain of Responsibility: cada validador verifica uma regra e passa o contexto adiante.
 * <p>
 * Nesta implementação a cadeia acumula todos os erros (em vez de parar no primeiro),
 * para que quem chama a API receba a lista completa de pendências de uma vez.
 * Um elo pode interromper a cadeia retornando {@code false} em {@link #verificar} quando
 * as regras seguintes dependem dele (ex.: sem NCM cadastrada não faz sentido checar licença).
 */
public abstract class ValidadorImportacao {

    private ValidadorImportacao proximo;

    /** Encadeia o próximo validador e o devolve, permitindo a escrita fluente a.encadear(b).encadear(c). */
    public ValidadorImportacao encadear(ValidadorImportacao proximo) {
        this.proximo = proximo;
        return proximo;
    }

    public final void validar(ContextoValidacao contexto) {
        boolean continuar = verificar(contexto);
        if (continuar && proximo != null) {
            proximo.validar(contexto);
        }
    }

    /**
     * @return {@code true} para seguir para o próximo elo; {@code false} para interromper a cadeia.
     */
    protected abstract boolean verificar(ContextoValidacao contexto);
}
