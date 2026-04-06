package com.hospitalrafael.crm.exception;

/**
 * Lançada quando dados fornecidos violam alguma regra de negócio
 * não coberta pelas outras exceções específicas.
 */
public class DadosInvalidosException extends CrmException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
