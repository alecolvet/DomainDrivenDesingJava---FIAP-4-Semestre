package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.exception.DadosInvalidosException;
import com.hospitalrafael.crm.exception.LeadDuplicadoException;
import com.hospitalrafael.crm.exception.LeadStatusInvalidoException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.repository.LeadRepository;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de Leads.
 *
 * Responsabilidades separadas por método:
 *  1. Cadastrar com cálculo automático de Lead Score
 *  2. Atualizar status com validação de transições permitidas
 *  3. Listar leads por prioridade e urgência
 */
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    // ─── REGRA DE NEGÓCIO 1: Cadastro com cálculo de Lead Score ──────────────

    /**
     * Cadastra um novo lead com cálculo automático de score e prioridade.
     *
     * Regras aplicadas:
     *  - Nome e e-mail são obrigatórios
     *  - E-mail não pode estar duplicado
     *  - Lead Score é calculado automaticamente pelos fatores
     *  - Status inicial = "Novo"
     *
     * @param lead dados do lead
     * @return lead salvo com score e prioridade calculados
     * @throws DadosInvalidosException se campos obrigatórios estiverem ausentes
     * @throws LeadDuplicadoException  se o e-mail já existir
     */
    public Lead cadastrar(Lead lead) {
        validarCamposObrigatorios(lead);
        validarEmailUnico(lead.getEmail());

        if (lead.getStatus() == null) lead.setStatus("Novo");

        calcularLeadScore(lead);

        return leadRepository.salvar(lead);
    }

    // ─── REGRA DE NEGÓCIO 2: Atualização de status com validação ─────────────

    /**
     * Altera o status de um lead respeitando as transições permitidas.
     *
     * Transições válidas:
     *   Novo            → Em Atendimento | Cancelado
     *   Em Atendimento  → Aguardando Retorno | Convertido | Cancelado
     *   Aguardando Retorno → Em Atendimento | Cancelado
     *   Convertido / Cancelado → nenhuma transição
     *
     * @param id         ID do lead
     * @param novoStatus novo status desejado
     * @return lead com status atualizado
     * @throws RecursoNaoEncontradoException  se o ID não existir
     * @throws LeadStatusInvalidoException    se a transição não for permitida
     */
    public Lead atualizarStatus(Long id, String novoStatus) {
        Lead lead = buscarPorId(id);
        validarTransicaoStatus(lead.getStatus(), novoStatus);
        lead.setStatus(novoStatus);
        leadRepository.atualizar(lead);
        return lead;
    }

    // ─── REGRA DE NEGÓCIO 3: Listagem por prioridade e urgência ───────────────

    /**
     * Lista todos os leads ordenados por prioridade (1 = mais urgente).
     */
    public List<Lead> listarPorPrioridade() {
        return leadRepository.buscarTodosOrdenadosPorPrioridade();
    }

    /**
     * Lista apenas os leads marcados como urgentes.
     */
    public List<Lead> listarUrgentes() {
        return leadRepository.buscarUrgentes();
    }

    /**
     * Lista leads filtrados por status.
     */
    public List<Lead> listarPorStatus(String status) {
        return leadRepository.buscarPorStatus(status);
    }

    /**
     * Busca um lead pelo ID, lançando exceção se não encontrado.
     */
    public Lead buscarPorId(Long id) {
        return leadRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lead", id));
    }

    /**
     * Imprime estatísticas de leads diretamente via JDBC (SQL GROUP BY).
     */
    public void imprimirEstatisticas() {
        leadRepository.imprimirEstatisticas();
    }

    // ─── Lógica de cálculo de Lead Score (privada) ────────────────────────────

    /**
     * Calcula automaticamente o Lead Score e a prioridade numérica.
     *
     * Pontuação:
     *   +3  fator_urgencia = true
     *   +2  canal = "Indicação" ou "WhatsApp"
     *   +1  qualquer outro canal informado
     *   +1  tem plano de saúde
     *
     * Score final:
     *   >= 5  → "Muito Alto" / prioridade 1
     *   >= 3  → "Alto"       / prioridade 2
     *   >= 1  → "Médio"      / prioridade 3
     *    < 1  → "Baixo"      / prioridade 4
     */
    private void calcularLeadScore(Lead lead) {
        int pontos = 0;

        if (Boolean.TRUE.equals(lead.getFatorUrgencia())) {
            pontos += 3;
        }

        if (lead.getCanalOrigem() != null) {
            String canal = lead.getCanalOrigem().toLowerCase();
            if (canal.contains("indica") || canal.contains("whatsapp")) {
                pontos += 2;
            } else {
                pontos += 1;
            }
        }

        if (lead.getPlanoSaude() != null && !lead.getPlanoSaude().isBlank()) {
            pontos += 1;
        }

        if (pontos >= 5) {
            lead.setLeadScore("Muito Alto");
            lead.setPrioridade(1);
        } else if (pontos >= 3) {
            lead.setLeadScore("Alto");
            lead.setPrioridade(2);
        } else if (pontos >= 1) {
            lead.setLeadScore("Medio");
            lead.setPrioridade(3);
        } else {
            lead.setLeadScore("Baixo");
            lead.setPrioridade(4);
        }
    }

    // ─── Validações privadas ──────────────────────────────────────────────────

    private void validarCamposObrigatorios(Lead lead) {
        if (lead.getNome() == null || lead.getNome().isBlank()) {
            throw new DadosInvalidosException("Nome do lead é obrigatório.");
        }
        if (lead.getEmail() == null || lead.getEmail().isBlank()) {
            throw new DadosInvalidosException("E-mail do lead é obrigatório.");
        }
    }

    private void validarEmailUnico(String email) {
        if (leadRepository.existePorEmail(email)) {
            throw new LeadDuplicadoException(email);
        }
    }

    private void validarTransicaoStatus(String atual, String novo) {
        boolean valida = switch (atual != null ? atual : "Novo") {
            case "Novo"               -> novo.equals("Em Atendimento") || novo.equals("Cancelado");
            case "Em Atendimento"     -> List.of("Aguardando Retorno", "Convertido", "Cancelado").contains(novo);
            case "Aguardando Retorno" -> List.of("Em Atendimento", "Cancelado").contains(novo);
            case "Convertido", "Cancelado" -> false;
            default -> false;
        };

        if (!valida) {
            throw new LeadStatusInvalidoException(atual, novo);
        }
    }
}
