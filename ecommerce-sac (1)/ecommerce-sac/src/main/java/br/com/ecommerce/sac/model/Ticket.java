package br.com.ecommerce.sac.model;

import java.time.LocalDateTime;

public class Ticket {

    private int id;
    private String titulo;
    private String descricao;
    private String status;       // ABERTO, EM_ANDAMENTO, RESOLVIDO, FECHADO
    private String prioridade;   // BAIXA, MEDIA, ALTA, CRITICA
    private int clienteId;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Ticket() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        this.status = "ABERTO";
    }

    public Ticket(String titulo, String descricao, String prioridade, int clienteId) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.clienteId = clienteId;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    @Override
    public String toString() {
        return String.format(
            "Ticket[id=%d, titulo='%s', status=%s, prioridade=%s, clienteId=%d]",
            id, titulo, status, prioridade, clienteId
        );
    }
}
