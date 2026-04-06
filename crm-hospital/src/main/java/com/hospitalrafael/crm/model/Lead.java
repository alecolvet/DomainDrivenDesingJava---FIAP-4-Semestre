package com.hospitalrafael.crm.model;

import java.time.LocalDate;

/**
 * Entidade de negócio: Lead (paciente potencial do hospital).
 * Mapeada à tabela LEAD no banco de dados Oracle.
 *
 * Campos do banco:
 *   ID, NOME, EMAIL, TELEFONE, CANAL_ORIGEM, STATUS,
 *   LEAD_SCORE, PRIORIDADE, FATOR_URGENCIA,
 *   PLANO_SAUDE, PROCEDIMENTO_INTERESSE, CRIADO_EM
 */
public class Lead {

    // ─── Atributos mapeados às colunas da tabela LEAD ─────────────────────────

    private Long    id;
    private String  nome;
    private String  email;
    private Long    telefone;
    private String  canalOrigem;        // coluna: CANAL_ORIGEM
    private String  status;
    private String  leadScore;          // coluna: LEAD_SCORE
    private Integer prioridade;
    private Boolean fatorUrgencia;      // coluna: FATOR_URGENCIA (1=sim, 0=nao)
    private String  planoSaude;         // coluna: PLANO_SAUDE
    private String  procedimentoInteresse; // coluna: PROCEDIMENTO_INTERESSE
    private LocalDate criadoEm;         // coluna: CRIADO_EM

    public Lead() {}

    // ─── Getters e Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getTelefone() { return telefone; }
    public void setTelefone(Long telefone) { this.telefone = telefone; }

    public String getCanalOrigem() { return canalOrigem; }
    public void setCanalOrigem(String canalOrigem) { this.canalOrigem = canalOrigem; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLeadScore() { return leadScore; }
    public void setLeadScore(String leadScore) { this.leadScore = leadScore; }

    public Integer getPrioridade() { return prioridade; }
    public void setPrioridade(Integer prioridade) { this.prioridade = prioridade; }

    public Boolean getFatorUrgencia() { return fatorUrgencia; }
    public void setFatorUrgencia(Boolean fatorUrgencia) { this.fatorUrgencia = fatorUrgencia; }

    public String getPlanoSaude() { return planoSaude; }
    public void setPlanoSaude(String planoSaude) { this.planoSaude = planoSaude; }

    public String getProcedimentoInteresse() { return procedimentoInteresse; }
    public void setProcedimentoInteresse(String p) { this.procedimentoInteresse = p; }

    public LocalDate getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDate criadoEm) { this.criadoEm = criadoEm; }

    @Override
    public String toString() {
        return "Lead{id=" + id + ", nome='" + nome + "', email='" + email +
               "', status='" + status + "', leadScore='" + leadScore +
               "', prioridade=" + prioridade + ", urgente=" + fatorUrgencia + "}";
    }
}
