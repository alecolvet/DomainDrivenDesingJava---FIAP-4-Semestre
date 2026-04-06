package com.hospitalrafael.crm.model;

import java.time.LocalDate;

/**
 * Entidade de negócio: Usuario (operador/atendente do hospital).
 * Mapeada à tabela USUARIO no banco de dados Oracle.
 *
 * Campos do banco:
 *   ID, NOME, EMAIL, SENHA, DOC (CPF), TELEFONE, DATANASC
 */
public class Usuario {

    // ─── Atributos mapeados às colunas da tabela USUARIO ─────────────────────

    private Long      id;
    private String    nome;
    private String    email;
    private String    senha;
    private String    doc;           // coluna: DOC (CPF com 11 dígitos)
    private Long      telefone;
    private LocalDate dataNasc;      // coluna: DATANASC

    public Usuario() {}

    // ─── Getters e Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getDoc() { return doc; }
    public void setDoc(String doc) { this.doc = doc; }

    public Long getTelefone() { return telefone; }
    public void setTelefone(Long telefone) { this.telefone = telefone; }

    public LocalDate getDataNasc() { return dataNasc; }
    public void setDataNasc(LocalDate dataNasc) { this.dataNasc = dataNasc; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', email='" + email +
               "', doc='" + doc + "'}";
    }
}
