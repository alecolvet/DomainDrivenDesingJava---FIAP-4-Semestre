package com.hospitalrafael.crm.model;

import java.time.LocalDate;

/**
 * Entidade de negócio: Agendamento (consulta/procedimento agendado).
 * Mapeada à tabela AGENDAMENTO no banco de dados Oracle.
 *
 * Campos do banco:
 *   ID, LEAD_ID, OPERADOR_ID, PROCEDIMENTO, DATA_HORA, STATUS, CRIADO_EM
 */
public class Agendamento {

    // ─── Atributos mapeados às colunas da tabela AGENDAMENTO ─────────────────

    private Long      id;
    private Long      leadId;          // coluna: LEAD_ID (FK para LEAD)
    private Long      operadorId;      // coluna: OPERADOR_ID (FK para USUARIO)
    private String    procedimento;
    private LocalDate dataHora;        // coluna: DATA_HORA
    private String    status;
    private LocalDate criadoEm;        // coluna: CRIADO_EM

    public Agendamento() {}

    // ─── Getters e Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public Long getOperadorId() { return operadorId; }
    public void setOperadorId(Long operadorId) { this.operadorId = operadorId; }

    public String getProcedimento() { return procedimento; }
    public void setProcedimento(String procedimento) { this.procedimento = procedimento; }

    public LocalDate getDataHora() { return dataHora; }
    public void setDataHora(LocalDate dataHora) { this.dataHora = dataHora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDate criadoEm) { this.criadoEm = criadoEm; }

    @Override
    public String toString() {
        return "Agendamento{id=" + id + ", leadId=" + leadId +
               ", procedimento='" + procedimento + "', data=" + dataHora +
               ", status='" + status + "'}";
    }
}
