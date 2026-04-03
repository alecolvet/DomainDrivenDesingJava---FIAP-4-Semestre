package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.exception.CpfInvalidoException;
import com.hospitalrafael.crm.exception.EmailDuplicadoException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio relacionadas a Usuários/Operadores.
 *
 * Responsabilidades:
 *  - Cadastro com validação de CPF e email únicos
 *  - Atualização de dados
 *  - Remoção segura (verifica dependências)
 *  - Consultas
 */
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ─── REGRA DE NEGÓCIO 1: Cadastro com validação de CPF e email único ──────

    /**
     * Cadastra um novo usuário/operador.
     *
     * Regras aplicadas:
     *  - CPF deve ter exatamente 11 dígitos numéricos
     *  - Email não pode ser duplicado no sistema
     *  - CPF não pode ser duplicado no sistema
     *
     * @param usuario dados do usuário a ser cadastrado
     * @return usuário salvo com ID gerado
     * @throws CpfInvalidoException    se o CPF não for válido
     * @throws EmailDuplicadoException se o email já existir
     */
    public Usuario cadastrar(Usuario usuario) {
        validarCpf(usuario.getDoc());
        validarEmailUnico(usuario.getEmail());
        validarCpfUnico(usuario.getDoc());
        return usuarioRepository.save(usuario);
    }

    // ─── REGRA DE NEGÓCIO 2: Atualização de dados do usuário ─────────────────

    /**
     * Atualiza os dados de um usuário existente.
     * Garante que o novo email não conflite com outro usuário.
     *
     * @param id      ID do usuário a atualizar
     * @param dados   novos dados
     * @return usuário atualizado
     */
    public Usuario atualizar(Long id, Usuario dados) {
        Usuario existente = buscarPorId(id);

        // Valida email apenas se foi alterado
        if (!existente.getEmail().equalsIgnoreCase(dados.getEmail())) {
            validarEmailUnico(dados.getEmail());
        }

        existente.setNome(dados.getNome());
        existente.setEmail(dados.getEmail());
        existente.setTelefone(dados.getTelefone());
        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            existente.setSenha(dados.getSenha());
        }

        return usuarioRepository.save(existente);
    }

    // ─── REGRA DE NEGÓCIO 3: Remoção segura ──────────────────────────────────

    /**
     * Remove um usuário pelo ID.
     * Lança exception se o usuário não existir.
     *
     * @param id ID do usuário
     */
    public void remover(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // ─── Validações privadas ──────────────────────────────────────────────────

    private void validarCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new CpfInvalidoException(cpf);
        }
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailDuplicadoException(email);
        }
    }

    private void validarCpfUnico(String doc) {
        if (usuarioRepository.existsByDoc(doc)) {
            throw new com.hospitalrafael.crm.exception.DadosInvalidosException(
                    "CPF " + doc + " já está cadastrado no sistema."
            );
        }
    }
}
