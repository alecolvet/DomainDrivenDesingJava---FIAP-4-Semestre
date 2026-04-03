package com.hospitalrafael.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "AGENDAMENTO")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Lead e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEAD_ID", nullable = false)
    private Lead lead;

    @NotNull(message = "Operador e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERADOR_ID", nullable = false)
    private Usuario operador;

    @Size(max = 40)
    @Column(length = 40)
    private String procedimento;

    @Column(name = "DATA_HORA")
    private LocalDate dataHora;

    @Size(max = 40)
    @Column(length = 40)
    private String status;

    @Column(name = "LEMBRETE_ENVIADO")
    private Boolean lembreteEnviado;

    @Column(name = "CRIADO_EM")
    private LocalDate criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDate.now();
        if (this.lembreteEnviado == null) this.lembreteEnviado = false;
        if (this.status == null) this.status = "Pendente";
    }

    public Agendamento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }

    public Usuario getOperador() { return operador; }
    public void setOperador(Usuario operador) { this.operador = operador; }

    public String getProcedimento() { return procedimento; }
    public void setProcedimento(String procedimento) { this.procedimento = procedimento; }

    public LocalDate getDataHora() { return dataHora; }
    public void setDataHora(LocalDate dataHora) { this.dataHora = dataHora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getLembreteEnviado() { return lembreteEnviado; }
    public void setLembreteEnviado(Boolean lembreteEnviado) { this.lembreteEnviado = lembreteEnviado; }

    public LocalDate getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDate criadoEm) { this.criadoEm = criadoEm; }
}
