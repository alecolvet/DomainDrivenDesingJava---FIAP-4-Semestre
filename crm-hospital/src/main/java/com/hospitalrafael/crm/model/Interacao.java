package com.hospitalrafael.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "INTERACAO")
public class Interacao {

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
    private String tipo;

    @Size(max = 40)
    @Column(length = 40)
    private String conteudo;

    @Column(name = "URGENCIA_DETECTADA")
    private Boolean urgenciaDetectada;

    @Column(name = "REALIZADO_EM")
    private LocalDate realizadoEm;

    @PrePersist
    protected void onCreate() {
        this.realizadoEm = LocalDate.now();
    }

    public Interacao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }

    public Usuario getOperador() { return operador; }
    public void setOperador(Usuario operador) { this.operador = operador; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public Boolean getUrgenciaDetectada() { return urgenciaDetectada; }
    public void setUrgenciaDetectada(Boolean urgenciaDetectada) { this.urgenciaDetectada = urgenciaDetectada; }

    public LocalDate getRealizadoEm() { return realizadoEm; }
    public void setRealizadoEm(LocalDate realizadoEm) { this.realizadoEm = realizadoEm; }
}
