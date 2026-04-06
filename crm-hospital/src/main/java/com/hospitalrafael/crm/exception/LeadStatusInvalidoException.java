package com.hospitalrafael.crm.exception;

/**
 * Lançada quando se tenta alterar o status de um Lead para uma
 * transição não permitida pelas regras de negócio.
 *
 * Ex: não é possível mudar de "Convertido" para "Novo".
 */
public class LeadStatusInvalidoException extends CrmException {

    public LeadStatusInvalidoException(String statusAtual, String novoStatus) {
        super("Transição de status inválida: não é possível mudar de '"
                + statusAtual + "' para '" + novoStatus + "'.");
    }
}
