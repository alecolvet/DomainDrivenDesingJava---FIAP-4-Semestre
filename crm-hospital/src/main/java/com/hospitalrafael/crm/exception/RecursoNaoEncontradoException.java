package com.hospitalrafael.crm.exception;

/**
 * Lançada quando um recurso (Lead, Usuario, Agendamento) não é encontrado no banco.
 */
public class RecursoNaoEncontradoException extends CrmException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " com ID " + id + " não foi encontrado.");
    }
}
