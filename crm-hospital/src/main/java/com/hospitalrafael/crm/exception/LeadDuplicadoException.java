package com.hospitalrafael.crm.exception;

/**
 * Lançada quando se tenta cadastrar um Lead com e-mail já existente no sistema.
 */
public class LeadDuplicadoException extends CrmException {

    public LeadDuplicadoException(String email) {
        super("Já existe um Lead cadastrado com o e-mail: " + email);
    }
}
