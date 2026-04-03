package com.hospitalrafael.crm;

import com.hospitalrafael.crm.exception.CpfInvalidoException;
import com.hospitalrafael.crm.exception.EmailDuplicadoException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.repository.UsuarioRepository;
import com.hospitalrafael.crm.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do UsuarioService
 * Valida as regras de negócio de cadastro e validação de Usuários/Operadores.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - Testes Unitários")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        usuarioBase = new Usuario();
        usuarioBase.setId(1L);
        usuarioBase.setNome("Carlos Silva");
        usuarioBase.setEmail("carlos@hospital.com");
        usuarioBase.setSenha("Senha@123");
        usuarioBase.setDoc("12345678901");
        usuarioBase.setTelefone(11999990001L);
        usuarioBase.setDataNasc(LocalDate.of(1990, 5, 15));
    }

    @Test
    @DisplayName("Deve cadastrar usuário com CPF e email válidos")
    void deveCadastrarUsuarioComSucesso() {
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(usuarioRepository.existsByDoc(any())).thenReturn(false);
        when(usuarioRepository.save(any())).thenReturn(usuarioBase);

        Usuario resultado = usuarioService.cadastrar(usuarioBase);

        assertNotNull(resultado);
        assertEquals("Carlos Silva", resultado.getNome());
        verify(usuarioRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar CpfInvalidoException para CPF com menos de 11 dígitos")
    void deveLancarExceptionParaCpfInvalido() {
        usuarioBase.setDoc("123");

        assertThrows(CpfInvalidoException.class, () -> usuarioService.cadastrar(usuarioBase));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar CpfInvalidoException para CPF com letras")
    void deveLancarExceptionParaCpfComLetras() {
        usuarioBase.setDoc("1234567890A");

        assertThrows(CpfInvalidoException.class, () -> usuarioService.cadastrar(usuarioBase));
    }

    @Test
    @DisplayName("Deve lançar EmailDuplicadoException para email já existente")
    void deveLancarExceptionParaEmailDuplicado() {
        when(usuarioRepository.existsByEmail(usuarioBase.getEmail())).thenReturn(true);

        assertThrows(EmailDuplicadoException.class, () -> usuarioService.cadastrar(usuarioBase));
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException para ID inexistente")
    void deveLancarExceptionParaIdInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> usuarioService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve retornar lista de todos os usuários")
    void deveListarTodosOsUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioBase));

        List<Usuario> resultado = usuarioService.listarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve remover usuário existente com sucesso")
    void deveRemoverUsuarioComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        doNothing().when(usuarioRepository).delete(usuarioBase);

        assertDoesNotThrow(() -> usuarioService.remover(1L));
        verify(usuarioRepository).delete(usuarioBase);
    }
}
