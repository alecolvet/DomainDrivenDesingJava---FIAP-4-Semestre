package com.hospitalrafael.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "LEAD")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String nome;

    @NotNull(message = "Telefone e obrigatorio")
    @Column(nullable = false)
    private Long telefone;

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String email;

    @Size(max = 40)
    @Column(name = "CANAL_ORIGEM", length = 40)
    private String canalOrigem;

    @Size(max = 40)
    @Column(length = 40)
    private String status;

    @Size(max = 40)
    @Column(name = "LEAD_SCORE", length = 40)
    private String leadScore;

    @Column
    private Integer prioridade;

    @Column(name = "FATOR_URGENCIA")
    private Boolean fatorUrgencia;

    @Size(max = 30)
    @Column(name = "FATOR_CANAL", length = 30)
    private String fatorCanal;

    @Size(max = 40)
    @Column(name = "FATOR_TEMPO_SEM_RESPOSTA", length = 40)
    private String fatorTempoSemResposta;

    @Column(name = "FATOR_REAGENDAMENTO")
    private LocalDate fatorReagendamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERADOR_ID")
    private Usuario operador;

    @Size(max = 40)
    @Column(name = "PROCEDIMENTO_INTERESSE", length = 40)
    private String procedimentoInteresse;

    @Size(max = 40)
    @Column(name = "PLANO_SAUDE", length = 40)
    private String planoSaude;

    @Column(name = "ULTIMO_CONTATO")
    private LocalDate ultimoContato;

    @Column(name = "CRIADO_EM")
    private LocalDate criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDate.now();
        if (this.status == null) this.status = "Novo";
    }

    public Lead() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Long getTelefone() { return telefone; }
    public void setTelefone(Long telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public String getFatorCanal() { return fatorCanal; }
    public void setFatorCanal(String fatorCanal) { this.fatorCanal = fatorCanal; }

    public String getFatorTempoSemResposta() { return fatorTempoSemResposta; }
    public void setFatorTempoSemResposta(String fatorTempoSemResposta) { this.fatorTempoSemResposta = fatorTempoSemResposta; }

    public LocalDate getFatorReagendamento() { return fatorReagendamento; }
    public void setFatorReagendamento(LocalDate fatorReagendamento) { this.fatorReagendamento = fatorReagendamento; }

    public Usuario getOperador() { return operador; }
    public void setOperador(Usuario operador) { this.operador = operador; }

    public String getProcedimentoInteresse() { return procedimentoInteresse; }
    public void setProcedimentoInteresse(String procedimentoInteresse) { this.procedimentoInteresse = procedimentoInteresse; }

    public String getPlanoSaude() { return planoSaude; }
    public void setPlanoSaude(String planoSaude) { this.planoSaude = planoSaude; }

    public LocalDate getUltimoContato() { return ultimoContato; }
    public void setUltimoContato(LocalDate ultimoContato) { this.ultimoContato = ultimoContato; }

    public LocalDate getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDate criadoEm) { this.criadoEm = criadoEm; }
}
