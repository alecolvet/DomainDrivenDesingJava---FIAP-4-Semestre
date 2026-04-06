package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.exception.AgendamentoDataPassadaException;
import com.hospitalrafael.crm.exception.DadosInvalidosException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.model.Agendamento;
import com.hospitalrafael.crm.repository.AgendamentoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de Agendamentos.
 *
 * Responsabilidades:
 *  1. Criar agendamento com validação de data e conflito de horário
 *  2. Confirmar agendamento existente
 *  3. Cancelar agendamento existente
 */
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    // ─── REGRA DE NEGÓCIO 1: Criação com validação de data e conflito ─────────

    /**
     * Cria um novo agendamento.
     *
     * Regras aplicadas:
     *  - Data não pode ser no passado
     *  - Lead e Operador são obrigatórios
     *  - Operador não pode ter outro agendamento ativo na mesma data
     *
     * @param agendamento dados do agendamento
     * @return agendamento salvo com ID gerado
     * @throws DadosInvalidosException          se campos obrigatórios faltarem
     * @throws AgendamentoDataPassadaException   se data for no passado
     * @throws DadosInvalidosException           se houver conflito de horário
     */
    public Agendamento criar(Agendamento agendamento) {
        validarCamposObrigatorios(agendamento);
        validarDataFutura(agendamento.getDataHora());
        validarConflito(agendamento.getOperadorId(), agendamento.getDataHora());

        agendamento.setStatus("Pendente");
        return agendamentoRepository.salvar(agendamento);
    }

    // ─── REGRA DE NEGÓCIO 2: Confirmação de agendamento ──────────────────────

    /**
     * Confirma um agendamento pendente, alterando seu status para "Confirmado".
     *
     * @param id ID do agendamento
     * @throws RecursoNaoEncontradoException se o agendamento não existir
     */
    public Agendamento confirmar(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setStatus("Confirmado");
        agendamentoRepository.atualizarStatus(id, "Confirmado");
        return agendamento;
    }

    // ─── REGRA DE NEGÓCIO 3: Cancelamento de agendamento ─────────────────────

    /**
     * Cancela um agendamento, alterando seu status para "Cancelado".
     *
     * @param id ID do agendamento
     * @throws RecursoNaoEncontradoException se o agendamento não existir
     */
    public Agendamento cancelar(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setStatus("Cancelado");
        agendamentoRepository.atualizarStatus(id, "Cancelado");
        return agendamento;
    }

    /**
     * Busca um agendamento pelo ID.
     */
    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento", id));
    }

    /**
     * Lista agendamentos de um lead.
     */
    public List<Agendamento> listarPorLead(Long leadId) {
        return agendamentoRepository.buscarPorLeadId(leadId);
    }

    // ─── Validações privadas ──────────────────────────────────────────────────

    private void validarCamposObrigatorios(Agendamento a) {
        if (a.getLeadId() == null) {
            throw new DadosInvalidosException("Lead é obrigatório para o agendamento.");
        }
        if (a.getOperadorId() == null) {
            throw new DadosInvalidosException("Operador é obrigatório para o agendamento.");
        }
        if (a.getDataHora() == null) {
            throw new DadosInvalidosException("Data/hora é obrigatória para o agendamento.");
        }
    }

    private void validarDataFutura(LocalDate data) {
        if (data == null || !data.isAfter(LocalDate.now())) {
            throw new AgendamentoDataPassadaException();
        }
    }

    private void validarConflito(Long operadorId, LocalDate dataHora) {
        if (agendamentoRepository.existeConflito(operadorId, dataHora)) {
            throw new DadosInvalidosException(
                    "Operador já possui agendamento na data: " + dataHora);
        }
    }
}
