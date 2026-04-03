package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.exception.AgendamentoConflitanteException;
import com.hospitalrafael.crm.exception.AgendamentoDataPassadaException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.model.Agendamento;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.Notificacao;
import com.hospitalrafael.crm.repository.AgendamentoRepository;
import com.hospitalrafael.crm.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de Agendamentos.
 *
 * Responsabilidades:
 *  - Criação com validação de conflito de horário e data
 *  - Reagendamento
 *  - Confirmação de agendamento
 *  - Envio de lembretes automáticos
 */
@Service
@Transactional
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final NotificacaoRepository notificacaoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                               NotificacaoRepository notificacaoRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.notificacaoRepository = notificacaoRepository;
    }

    // ─── REGRA DE NEGÓCIO 1: Criação com validação de conflito e data ────────

    /**
     * Cria um novo agendamento.
     *
     * Regras aplicadas:
     *  - Data não pode ser no passado
     *  - Operador não pode ter outro agendamento no mesmo horário
     *  - Gera notificação de lembrete automaticamente
     *
     * @param agendamento dados do agendamento
     * @return agendamento salvo
     * @throws AgendamentoDataPassadaException  se data for no passado
     * @throws AgendamentoConflitanteException  se operador já tiver compromisso
     */
    public Agendamento criar(Agendamento agendamento) {
        validarDataFutura(agendamento.getDataHora());
        validarConflito(agendamento);

        Agendamento salvo = agendamentoRepository.save(agendamento);
        gerarNotificacaoLembrete(salvo);
        return salvo;
    }

    // ─── REGRA DE NEGÓCIO 2: Reagendamento ───────────────────────────────────

    /**
     * Reagenda um agendamento existente para nova data.
     *
     * Regras aplicadas:
     *  - Nova data não pode ser no passado
     *  - Verifica conflito na nova data/operador
     *  - Atualiza status para "Reagendado"
     *  - Atualiza fator_reagendamento do Lead
     *
     * @param id       ID do agendamento
     * @param novaData nova data/hora
     * @return agendamento atualizado
     */
    public Agendamento reagendar(Long id, LocalDate novaData) {
        Agendamento agendamento = buscarPorId(id);
        validarDataFutura(novaData);

        Agendamento temp = new Agendamento();
        temp.setOperador(agendamento.getOperador());
        temp.setDataHora(novaData);
        validarConflito(temp);

        agendamento.setDataHora(novaData);
        agendamento.setStatus("Reagendado");
        agendamento.setLembreteEnviado(false);

        // Atualiza o Lead com a data de reagendamento
        Lead lead = agendamento.getLead();
        if (lead != null) {
            lead.setFatorReagendamento(novaData);
        }

        return agendamentoRepository.save(agendamento);
    }

    // ─── REGRA DE NEGÓCIO 3: Confirmação e lembretes ─────────────────────────

    /**
     * Confirma um agendamento pendente.
     *
     * @param id ID do agendamento
     * @return agendamento confirmado
     */
    public Agendamento confirmar(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setStatus("Confirmado");
        return agendamentoRepository.save(agendamento);
    }

    /**
     * Marca o lembrete como enviado para agendamentos próximos.
     * Processa todos os agendamentos sem lembrete enviado.
     *
     * @return quantidade de lembretes enviados
     */
    public int enviarLembretes() {
        List<Agendamento> pendentes = agendamentoRepository.findByLembreteEnviadoFalse();
        LocalDate amanha = LocalDate.now().plusDays(1);

        int count = 0;
        for (Agendamento ag : pendentes) {
            if (ag.getDataHora() != null && !ag.getDataHora().isAfter(amanha)) {
                ag.setLembreteEnviado(true);
                agendamentoRepository.save(ag);
                count++;
            }
        }
        return count;
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento", id));
    }

    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    public List<Agendamento> listarPorLead(Long leadId) {
        return agendamentoRepository.findByLeadId(leadId);
    }

    public List<Agendamento> listarPorStatus(String status) {
        return agendamentoRepository.findByStatus(status);
    }

    public void cancelar(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setStatus("Cancelado");
        agendamentoRepository.save(agendamento);
    }

    // ─── Métodos privados ─────────────────────────────────────────────────────

    private void validarDataFutura(LocalDate data) {
        if (data == null || !data.isAfter(LocalDate.now().minusDays(1))) {
            throw new AgendamentoDataPassadaException();
        }
    }

    private void validarConflito(Agendamento agendamento) {
        if (agendamento.getOperador() != null && agendamento.getDataHora() != null) {
            boolean conflito = agendamentoRepository.existsByOperadorIdAndDataHora(
                    agendamento.getOperador().getId(),
                    agendamento.getDataHora()
            );
            if (conflito) {
                throw new AgendamentoConflitanteException(
                        agendamento.getDataHora().toString(),
                        agendamento.getOperador().getNome()
                );
            }
        }
    }

    private void gerarNotificacaoLembrete(Agendamento agendamento) {
        if (agendamento.getLead() == null) return;

        Notificacao notificacao = new Notificacao();
        notificacao.setLead(agendamento.getLead());
        notificacao.setOperador(agendamento.getOperador());
        notificacao.setLead_nome(agendamento.getLead().getNome());

        String msg = String.format(
                "Lembrete: %s agendado para %s. Confirme sua presença.",
                agendamento.getProcedimento() != null ? agendamento.getProcedimento() : "procedimento",
                agendamento.getDataHora()
        );
        notificacao.setMensagem(msg.length() > 100 ? msg.substring(0, 100) : msg);
        notificacaoRepository.save(notificacao);
    }
}
