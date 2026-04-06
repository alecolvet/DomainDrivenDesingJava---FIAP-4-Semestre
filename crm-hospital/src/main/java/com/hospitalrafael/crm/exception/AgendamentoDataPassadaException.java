package com.hospitalrafael.crm.exception;

/**
 * Lançada quando se tenta criar um agendamento com data no passado.
 */
public class AgendamentoDataPassadaException extends CrmException {

    public AgendamentoDataPassadaException() {
        super("A data do agendamento não pode ser no passado.");
    }
}
