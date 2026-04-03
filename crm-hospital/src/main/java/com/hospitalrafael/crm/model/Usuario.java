package com.hospitalrafael.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String nome;

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String email;

    @NotBlank(message = "Senha e obrigatoria")
    @Size(min = 6, max = 20)
    @Column(nullable = false, length = 20)
    private String senha;

    @NotBlank(message = "CPF e obrigatorio")
    @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 digitos")
    @Column(nullable = false, length = 11)
    private String doc;

    @NotNull(message = "Telefone e obrigatorio")
    @Column(nullable = false)
    private Long telefone;

    @NotNull(message = "Data de nascimento e obrigatoria")
    @Past(message = "Data de nascimento deve ser no passado")
    @Column(name = "DATANASC", nullable = false)
    private LocalDate dataNasc;

    public Usuario() {}

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
}
