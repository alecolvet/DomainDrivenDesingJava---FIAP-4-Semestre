package com.hospitalrafael.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "NOTIFICACAO")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Lead e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEAD_ID", nullable = false)
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERADOR_ID")
    private Usuario operador;

    @Size(max = 100)
    @Column(length = 100)
    private String mensagem;

    @Size(max = 40)
    @Column(length = 40)
    private String lead_nome;

    @Column(name = "CRIADO_EM")
    private LocalDate criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDate.now();
    }

    public Notificacao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }

    public Usuario getOperador() { return operador; }
    public void setOperador(Usuario operador) { this.operador = operador; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getLead_nome() { return lead_nome; }
    public void setLead_nome(String lead_nome) { this.lead_nome = lead_nome; }

    public LocalDate getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDate criadoEm) { this.criadoEm = criadoEm; }
}
