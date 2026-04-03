package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.model.Notificacao;
import com.hospitalrafael.crm.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de Notificações.
 *
 * Responsabilidades:
 *  - Criação de notificações para leads e operadores
 *  - Consulta de notificações por lead e operador
 *  - Remoção de notificações antigas
 */
@Service
@Transactional
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    // ─── REGRA DE NEGÓCIO 1: Criação de notificação com validação ────────────

    /**
     * Cria uma nova notificação para um lead.
     *
     * Regras aplicadas:
     *  - Mensagem não pode ser nula ou vazia
     *  - Mensagem é truncada se ultrapassar 100 caracteres
     *  - Lead é obrigatório
     *
     * @param notificacao dados da notificação
     * @return notificação salva
     */
    public Notificacao criar(Notificacao notificacao) {
        validarMensagem(notificacao.getMensagem());

        if (notificacao.getMensagem() != null && notificacao.getMensagem().length() > 100) {
            notificacao.setMensagem(notificacao.getMensagem().substring(0, 100));
        }

        return notificacaoRepository.save(notificacao);
    }

    // ─── REGRA DE NEGÓCIO 2: Atualização de mensagem ─────────────────────────

    /**
     * Atualiza a mensagem de uma notificação existente.
     *
     * @param id          ID da notificação
     * @param novaMensagem nova mensagem
     * @return notificação atualizada
     */
    public Notificacao atualizarMensagem(Long id, String novaMensagem) {
        Notificacao notificacao = buscarPorId(id);
        validarMensagem(novaMensagem);

        if (novaMensagem.length() > 100) {
            novaMensagem = novaMensagem.substring(0, 100);
        }

        notificacao.setMensagem(novaMensagem);
        return notificacaoRepository.save(notificacao);
    }

    // ─── REGRA DE NEGÓCIO 3: Remoção de notificação ──────────────────────────

    /**
     * Remove uma notificação pelo ID.
     *
     * @param id ID da notificação
     */
    public void remover(Long id) {
        Notificacao notificacao = buscarPorId(id);
        notificacaoRepository.delete(notificacao);
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    public Notificacao buscarPorId(Long id) {
        return notificacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificação", id));
    }

    public List<Notificacao> listarTodas() {
        return notificacaoRepository.findAll();
    }

    public List<Notificacao> listarPorLead(Long leadId) {
        return notificacaoRepository.findByLeadId(leadId);
    }

    public List<Notificacao> listarPorOperador(Long operadorId) {
        return notificacaoRepository.findByOperadorId(operadorId);
    }

    // ─── Validações privadas ──────────────────────────────────────────────────

    private void validarMensagem(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            throw new com.hospitalrafael.crm.exception.DadosInvalidosException(
                    "A mensagem da notificação não pode ser vazia."
            );
        }
    }
}
