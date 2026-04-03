package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.exception.DadosInvalidosException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.model.Interacao;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.repository.InteracaoRepository;
import com.hospitalrafael.crm.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de Interações.
 *
 * Responsabilidades:
 *  - Registro de interações com detecção de urgência
 *  - Atualização de último contato do lead
 *  - Consultas por lead e operador
 */
@Service
@Transactional
public class InteracaoService {

    private final InteracaoRepository interacaoRepository;
    private final LeadRepository leadRepository;

    public InteracaoService(InteracaoRepository interacaoRepository,
                             LeadRepository leadRepository) {
        this.interacaoRepository = interacaoRepository;
        this.leadRepository = leadRepository;
    }

    // ─── REGRA DE NEGÓCIO 1: Registro com detecção de urgência ───────────────

    /**
     * Registra uma nova interação com um lead.
     *
     * Regras aplicadas:
     *  - Tipo de interação é obrigatório
     *  - Detecção automática de palavras-chave de urgência no conteúdo
     *  - Atualiza a data de último contato do lead
     *  - Se urgência detectada, atualiza fator_urgencia do lead
     *
     * @param interacao dados da interação
     * @return interação salva
     */
    public Interacao registrar(Interacao interacao) {
        if (interacao.getTipo() == null || interacao.getTipo().isBlank()) {
            throw new DadosInvalidosException("O tipo da interação é obrigatório.");
        }

        detectarUrgencia(interacao);
        Interacao salva = interacaoRepository.save(interacao);
        atualizarUltimoContatoLead(interacao);
        return salva;
    }

    // ─── REGRA DE NEGÓCIO 2: Atualização de interação ────────────────────────

    /**
     * Atualiza o conteúdo e urgência de uma interação existente.
     *
     * @param id    ID da interação
     * @param dados novos dados
     * @return interação atualizada
     */
    public Interacao atualizar(Long id, Interacao dados) {
        Interacao existente = buscarPorId(id);
        existente.setConteudo(dados.getConteudo());
        detectarUrgencia(existente);
        return interacaoRepository.save(existente);
    }

    // ─── REGRA DE NEGÓCIO 3: Remoção ─────────────────────────────────────────

    public void remover(Long id) {
        Interacao interacao = buscarPorId(id);
        interacaoRepository.delete(interacao);
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    public Interacao buscarPorId(Long id) {
        return interacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Interação", id));
    }

    public List<Interacao> listarPorLead(Long leadId) {
        return interacaoRepository.findByLeadId(leadId);
    }

    public List<Interacao> listarPorOperador(Long operadorId) {
        return interacaoRepository.findByOperadorId(operadorId);
    }

    public List<Interacao> listarUrgentes() {
        return interacaoRepository.findByUrgenciaDetectadaTrue();
    }

    // ─── Métodos privados ─────────────────────────────────────────────────────

    /**
     * Detecta palavras-chave de urgência no conteúdo da interação
     * e marca o campo urgenciaDetectada automaticamente.
     */
    private void detectarUrgencia(Interacao interacao) {
        if (interacao.getConteudo() == null) {
            interacao.setUrgenciaDetectada(false);
            return;
        }

        String conteudo = interacao.getConteudo().toLowerCase();
        boolean urgente = conteudo.contains("urgente") ||
                conteudo.contains("urgência") ||
                conteudo.contains("dor") ||
                conteudo.contains("emergência") ||
                conteudo.contains("imediato") ||
                conteudo.contains("grave");

        interacao.setUrgenciaDetectada(urgente);

        // Propaga urgência para o lead
        if (urgente && interacao.getLead() != null) {
            Lead lead = interacao.getLead();
            lead.setFatorUrgencia(true);
            leadRepository.save(lead);
        }
    }

    private void atualizarUltimoContatoLead(Interacao interacao) {
        if (interacao.getLead() != null) {
            Lead lead = interacao.getLead();
            lead.setUltimoContato(java.time.LocalDate.now());
            lead.setFatorTempoSemResposta("Menos de 24h");
            leadRepository.save(lead);
        }
    }
}
