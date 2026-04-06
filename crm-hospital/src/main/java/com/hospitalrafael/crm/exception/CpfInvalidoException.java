package com.hospitalrafael.crm.exception;

/**
 * Lançada quando o CPF informado não é válido.
 * O CPF deve conter exatamente 11 dígitos numéricos.
 */
public class CpfInvalidoException extends CrmException {

    public CpfInvalidoException(String cpf) {
        super("CPF inválido: '" + cpf + "'. O CPF deve ter exatamente 11 dígitos numéricos.");
    }
}
