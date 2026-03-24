package br.com.ecommerce.sac.exception;

/**
 * Exception customizada para o módulo de SAC.
 * Encapsula erros de negócio e de acesso a dados do módulo.
 */
public class SacException extends RuntimeException {

    private final String codigoErro;

    public SacException(String mensagem) {
        super(mensagem);
        this.codigoErro = "SAC_ERROR";
    }

    public SacException(String codigoErro, String mensagem) {
        super(mensagem);
        this.codigoErro = codigoErro;
    }

    public SacException(String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.codigoErro = "SAC_ERROR";
    }

    public SacException(String codigoErro, String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.codigoErro = codigoErro;
    }

    public String getCodigoErro() {
        return codigoErro;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", codigoErro, getMessage());
    }
}
