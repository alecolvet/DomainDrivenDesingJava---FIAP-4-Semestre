package com.hospitalrafael.crm.exception;

/**
 * Lançada quando se tenta cadastrar um Usuário com e-mail já existente.
 */
public class EmailDuplicadoException extends CrmException {

    public EmailDuplicadoException(String email) {
        super("Já existe um Usuário cadastrado com o e-mail: " + email);
    }
}
