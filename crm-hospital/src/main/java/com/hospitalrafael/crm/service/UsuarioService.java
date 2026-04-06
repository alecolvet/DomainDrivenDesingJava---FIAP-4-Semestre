package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.exception.CpfInvalidoException;
import com.hospitalrafael.crm.exception.DadosInvalidosException;
import com.hospitalrafael.crm.exception.EmailDuplicadoException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.repository.UsuarioRepository;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de Usuarios/Operadores.
 *
 * Responsabilidades separadas por método:
 *  1. Cadastrar com validação de CPF e e-mail únicos
 *  2. Atualizar dados com validação de e-mail
 *  3. Buscar e listar usuários com tratamento de não encontrado
 */
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ─── REGRA DE NEGÓCIO 1: Cadastro com validação de CPF e e-mail ──────────

    /**
     * Cadastra um novo usuário/operador no sistema.
     *
     * Regras aplicadas:
     *  - Nome, e-mail e CPF são obrigatórios
     *  - CPF deve ter exatamente 11 dígitos numéricos
     *  - E-mail não pode estar duplicado no sistema
     *  - CPF não pode estar duplicado no sistema
     *
     * @param usuario dados do novo usuário
     * @return usuário salvo com ID gerado pelo banco
     * @throws DadosInvalidosException  se campos obrigatórios estiverem nulos/vazios
     * @throws CpfInvalidoException     se o CPF não tiver 11 dígitos numéricos
     * @throws EmailDuplicadoException  se o e-mail já existir no sistema
     */
    public Usuario cadastrar(Usuario usuario) {
        validarCamposObrigatorios(usuario);
        validarCpf(usuario.getDoc());
        validarEmailUnico(usuario.getEmail());
        validarCpfUnico(usuario.getDoc());
        return usuarioRepository.salvar(usuario);
    }

    // ─── REGRA DE NEGÓCIO 2: Atualização de dados ─────────────────────────────

    /**
     * Atualiza os dados de um usuário existente.
     *
     * Regras aplicadas:
     *  - Usuário deve existir no banco
     *  - Novo e-mail não pode conflitar com outro usuário
     *
     * @param id    ID do usuário a atualizar
     * @param dados novos dados
     * @return usuário atualizado
     * @throws RecursoNaoEncontradoException se o ID não existir
     */
    public Usuario atualizar(Long id, Usuario dados) {
        Usuario existente = buscarPorId(id);

        if (!existente.getEmail().equalsIgnoreCase(dados.getEmail())) {
            validarEmailUnico(dados.getEmail());
        }

        existente.setNome(dados.getNome());
        existente.setEmail(dados.getEmail());
        existente.setTelefone(dados.getTelefone());
        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            existente.setSenha(dados.getSenha());
        }

        usuarioRepository.atualizar(existente);
        return existente;
    }

    // ─── REGRA DE NEGÓCIO 3: Busca com tratamento de ausência ─────────────────

    /**
     * Busca um usuário pelo ID, lançando exceção se não encontrado.
     *
     * @param id ID do usuário
     * @return usuário encontrado
     * @throws RecursoNaoEncontradoException se não existir
     */
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    /**
     * Lista todos os usuários cadastrados.
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.buscarTodos();
    }

    // ─── Validações privadas ──────────────────────────────────────────────────

    private void validarCamposObrigatorios(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new DadosInvalidosException("Nome do usuário é obrigatório.");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new DadosInvalidosException("E-mail do usuário é obrigatório.");
        }
        if (usuario.getDoc() == null || usuario.getDoc().isBlank()) {
            throw new DadosInvalidosException("CPF do usuário é obrigatório.");
        }
        if (usuario.getDataNasc() == null) {
            throw new DadosInvalidosException("Data de nascimento é obrigatória.");
        }
    }

    private void validarCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new CpfInvalidoException(cpf);
        }
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existePorEmail(email)) {
            throw new EmailDuplicadoException(email);
        }
    }

    private void validarCpfUnico(String doc) {
        if (usuarioRepository.existePorDoc(doc)) {
            throw new DadosInvalidosException("CPF " + doc + " já está cadastrado no sistema.");
        }
    }
}
