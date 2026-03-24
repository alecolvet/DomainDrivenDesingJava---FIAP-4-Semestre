package br.com.ecommerce.sac.model;

public class Cliente {

    private int id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;

    public Cliente() {}

    public Cliente(String nome, String email, String cpf, String telefone) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public String toString() {
        return String.format(
            "Cliente[id=%d, nome='%s', email='%s', cpf='%s', telefone='%s']",
            id, nome, email, cpf, telefone
        );
    }
}
