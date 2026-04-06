package com.hospitalrafael.crm.exception;

/**
 * Exceção base do sistema CRM Hospital São Rafael.
 * Todas as exceções de negócio herdam desta classe.
 *
 * Extende RuntimeException para não obrigar try-catch em todo lugar
 * (unchecked exception).
 */
public class CrmException extends RuntimeException {

    public CrmException(String mensagem) {
        super(mensagem);
    }
}
