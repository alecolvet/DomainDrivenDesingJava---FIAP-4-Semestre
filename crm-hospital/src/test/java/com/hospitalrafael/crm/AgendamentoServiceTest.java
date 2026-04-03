package com.hospitalrafael.crm;

import com.hospitalrafael.crm.exception.AgendamentoConflitanteException;
import com.hospitalrafael.crm.exception.AgendamentoDataPassadaException;
import com.hospitalrafael.crm.model.Agendamento;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.repository.AgendamentoRepository;
import com.hospitalrafael.crm.repository.NotificacaoRepository;
import com.hospitalrafael.crm.service.AgendamentoService;
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
 * Testes unitários do AgendamentoService
 * Valida as regras de negócio de criação, reagendamento e lembretes.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AgendamentoService - Testes Unitários")
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Agendamento agendamentoBase;
    private Usuario operador;
    private Lead lead;

    @BeforeEach
    void setUp() {
        operador = new Usuario();
        operador.setId(1L);
        operador.setNome("Operador Teste");

        lead = new Lead();
        lead.setId(1L);
        lead.setNome("Paciente Teste");

        agendamentoBase = new Agendamento();
        agendamentoBase.setId(1L);
        agendamentoBase.setLead(lead);
        agendamentoBase.setOperador(operador);
        agendamentoBase.setProcedimento("Consulta Cardiologista");
        agendamentoBase.setDataHora(LocalDate.now().plusDays(5));
        agendamentoBase.setStatus("Pendente");
        agendamentoBase.setLembreteEnviado(false);
    }

    @Test
    @DisplayName("Deve criar agendamento com data futura com sucesso")
    void deveCriarAgendamentoComSucesso() {
        when(agendamentoRepository.existsByOperadorIdAndDataHora(any(), any())).thenReturn(false);
        when(agendamentoRepository.save(any())).thenReturn(agendamentoBase);
        when(notificacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = agendamentoService.criar(agendamentoBase);

        assertNotNull(resultado);
        verify(agendamentoRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar AgendamentoDataPassadaException para data passada")
    void deveLancarExceptionParaDataPassada() {
        agendamentoBase.setDataHora(LocalDate.now().minusDays(1));

        assertThrows(AgendamentoDataPassadaException.class,
                () -> agendamentoService.criar(agendamentoBase));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar AgendamentoConflitanteException para horário já ocupado")
    void deveLancarExceptionParaConflito() {
        when(agendamentoRepository.existsByOperadorIdAndDataHora(
                operador.getId(), agendamentoBase.getDataHora())).thenReturn(true);

        assertThrows(AgendamentoConflitanteException.class,
                () -> agendamentoService.criar(agendamentoBase));
    }

    @Test
    @DisplayName("Deve reagendar para nova data futura com sucesso")
    void deveReagendarComSucesso() {
        LocalDate novaData = LocalDate.now().plusDays(10);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoBase));
        when(agendamentoRepository.existsByOperadorIdAndDataHora(any(), eq(novaData))).thenReturn(false);
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = agendamentoService.reagendar(1L, novaData);

        assertEquals("Reagendado", resultado.getStatus());
        assertEquals(novaData, resultado.getDataHora());
    }

    @Test
    @DisplayName("Deve confirmar agendamento e mudar status para Confirmado")
    void deveConfirmarAgendamento() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoBase));
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = agendamentoService.confirmar(1L);

        assertEquals("Confirmado", resultado.getStatus());
    }

    @Test
    @DisplayName("Deve enviar lembretes apenas para agendamentos próximos")
    void deveEnviarLembretes() {
        Agendamento proximo = new Agendamento();
        proximo.setDataHora(LocalDate.now());
        proximo.setLembreteEnviado(false);

        Agendamento distante = new Agendamento();
        distante.setDataHora(LocalDate.now().plusDays(30));
        distante.setLembreteEnviado(false);

        when(agendamentoRepository.findByLembreteEnviadoFalse()).thenReturn(List.of(proximo, distante));
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        int count = agendamentoService.enviarLembretes();

        assertEquals(1, count);
    }
}
