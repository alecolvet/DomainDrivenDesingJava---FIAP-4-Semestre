package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.exception.DadosInvalidosException;
import com.hospitalrafael.crm.exception.LeadDuplicadoException;
import com.hospitalrafael.crm.exception.LeadStatusInvalidoException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de Leads.
 *
 * Responsabilidades:
 *  - Cadastro com detecção de duplicidade
 *  - Cálculo automático de lead score e prioridade
 *  - Atualização de status com validação de transição
 *  - Listagem por prioridade e urgência
 */
@Service
@Transactional
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    // ─── REGRA DE NEGÓCIO 1: Cadastro com cálculo de Lead Score ──────────────

    /**
     * Cadastra um novo Lead no sistema.
     *
     * Aplica automaticamente:
     *  - Validação de duplicidade por email
     *  - Cálculo do Lead Score baseado nos fatores (urgência, canal, tempo)
     *  - Definição da prioridade numérica
     *  - Status inicial: "Novo"
     *
     * @param lead dados do lead
     * @return lead salvo com score e prioridade calculados
     * @throws LeadDuplicadoException se email já existir
     */
    public Lead cadastrar(Lead lead) {
        validarEmailUnico(lead.getEmail());
        calcularLeadScore(lead);
        return leadRepository.save(lead);
    }

    // ─── REGRA DE NEGÓCIO 2: Atualização de status com validação ─────────────

    /**
     * Atualiza o status de um Lead respeitando as transições permitidas.
     *
     * Transições válidas:
     *  Novo → Em Atendimento
     *  Em Atendimento → Aguardando Retorno | Convertido | Cancelado
     *  Aguardando Retorno → Em Atendimento | Cancelado
     *
     * @param id         ID do lead
     * @param novoStatus novo status desejado
     * @return lead atualizado
     * @throws LeadStatusInvalidoException se a transição não for permitida
     */
    public Lead atualizarStatus(Long id, String novoStatus) {
        Lead lead = buscarPorId(id);
        validarTransicaoStatus(lead.getStatus(), novoStatus);
        lead.setStatus(novoStatus);
        return leadRepository.save(lead);
    }

    // ─── REGRA DE NEGÓCIO 3: Atribuição de operador ───────────────────────────

    /**
     * Atribui (ou reatribui) um operador a um lead.
     * Atualiza também o fator de canal e tempo sem resposta.
     *
     * @param leadId     ID do lead
     * @param operador   usuário operador
     * @return lead atualizado
     */
    public Lead atribuirOperador(Long leadId, com.hospitalrafael.crm.model.Usuario operador) {
        Lead lead = buscarPorId(leadId);
        lead.setOperador(operador);
        lead.setUltimoContato(java.time.LocalDate.now());
        lead.setFatorTempoSemResposta("Menos de 24h");
        return leadRepository.save(lead);
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    public Lead buscarPorId(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lead", id));
    }

    public List<Lead> listarTodos() {
        return leadRepository.findAll();
    }

    public List<Lead> listarPorPrioridade() {
        return leadRepository.findAllByOrderByPrioridadeAsc();
    }

    public List<Lead> listarUrgentes() {
        return leadRepository.findLeadsUrgentesOrdenados();
    }

    public List<Lead> listarPorStatus(String status) {
        return leadRepository.findByStatusOrderByPrioridadeAsc(status);
    }

    public void remover(Long id) {
        Lead lead = buscarPorId(id);
        leadRepository.delete(lead);
    }

    // ─── Lógica de cálculo de Lead Score (privada) ────────────────────────────

    /**
     * Calcula o Lead Score e a prioridade com base nos fatores:
     *  - fator_urgencia   (+3 pontos)
     *  - canal_origem     (+2 para Indicação/WhatsApp, +1 para outros)
     *  - plano_saude      (+1 se tiver plano)
     *
     * Score final:
     *  >= 5  → "Muito Alto" / prioridade 1
     *  >= 3  → "Alto"       / prioridade 2
     *  >= 1  → "Médio"      / prioridade 3
     *  < 1   → "Baixo"      / prioridade 4
     */
    private void calcularLeadScore(Lead lead) {
        int pontos = 0;

        if (Boolean.TRUE.equals(lead.getFatorUrgencia())) {
            pontos += 3;
        }

        if (lead.getCanalOrigem() != null) {
            String canal = lead.getCanalOrigem().toLowerCase();
            if (canal.contains("indicação") || canal.contains("whatsapp")) {
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
            lead.setLeadScore("Médio");
            lead.setPrioridade(3);
        } else {
            lead.setLeadScore("Baixo");
            lead.setPrioridade(4);
        }
    }

    private void validarEmailUnico(String email) {
        if (leadRepository.existsByEmail(email)) {
            throw new LeadDuplicadoException(email);
        }
    }

    private void validarTransicaoStatus(String statusAtual, String novoStatus) {
        boolean transicaoValida = switch (statusAtual != null ? statusAtual : "Novo") {
            case "Novo" -> novoStatus.equals("Em Atendimento") || novoStatus.equals("Cancelado");
            case "Em Atendimento" -> List.of("Aguardando Retorno", "Convertido", "Cancelado").contains(novoStatus);
            case "Aguardando Retorno" -> List.of("Em Atendimento", "Cancelado").contains(novoStatus);
            case "Convertido", "Cancelado" -> false;
            default -> false;
        };

        if (!transicaoValida) {
            throw new LeadStatusInvalidoException(statusAtual, "mudar para '" + novoStatus + "'");
        }
    }
}
