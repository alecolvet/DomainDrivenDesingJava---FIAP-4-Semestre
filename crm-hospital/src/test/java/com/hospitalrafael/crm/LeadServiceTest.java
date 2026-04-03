package com.hospitalrafael.crm;

import com.hospitalrafael.crm.exception.LeadDuplicadoException;
import com.hospitalrafael.crm.exception.LeadStatusInvalidoException;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.repository.LeadRepository;
import com.hospitalrafael.crm.service.LeadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do LeadService
 * Valida as regras de negócio de cadastro, score e status de Leads.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LeadService - Testes Unitários")
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private LeadService leadService;

    private Lead leadBase;

    @BeforeEach
    void setUp() {
        leadBase = new Lead();
        leadBase.setId(1L);
        leadBase.setNome("Ana Pereira");
        leadBase.setEmail("ana@email.com");
        leadBase.setTelefone(11977770001L);
        leadBase.setCanalOrigem("Instagram");
        leadBase.setPlanoSaude("Unimed");
        leadBase.setFatorUrgencia(true);
        leadBase.setStatus("Novo");
    }

    // ─── Testes de Cadastro ───────────────────────────────────────────────────

    @Test
    @DisplayName("Deve cadastrar lead com sucesso e calcular score")
    void deveCadastrarLeadComSucesso() {
        when(leadRepository.existsByEmail(leadBase.getEmail())).thenReturn(false);
        when(leadRepository.save(any(Lead.class))).thenAnswer(inv -> inv.getArgument(0));

        Lead resultado = leadService.cadastrar(leadBase);

        assertNotNull(resultado);
        assertNotNull(resultado.getLeadScore());
        assertNotNull(resultado.getPrioridade());
        verify(leadRepository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("Deve lançar LeadDuplicadoException para email já existente")
    void deveLancarExceptionParaEmailDuplicado() {
        when(leadRepository.existsByEmail(leadBase.getEmail())).thenReturn(true);

        assertThrows(LeadDuplicadoException.class, () -> leadService.cadastrar(leadBase));
        verify(leadRepository, never()).save(any());
    }

    // ─── Testes de Lead Score ─────────────────────────────────────────────────

    @Test
    @DisplayName("Lead urgente com indicação deve ter score Muito Alto")
    void leadUrgenteComIndicacaoDeveSerMuitoAlto() {
        leadBase.setFatorUrgencia(true);
        leadBase.setCanalOrigem("Indicação");
        leadBase.setPlanoSaude("Unimed");

        when(leadRepository.existsByEmail(any())).thenReturn(false);
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Lead resultado = leadService.cadastrar(leadBase);

        assertEquals("Muito Alto", resultado.getLeadScore());
        assertEquals(1, resultado.getPrioridade());
    }

    @Test
    @DisplayName("Lead sem urgência e canal orgânico deve ter score Médio")
    void leadSemUrgenciaDeveSerMedio() {
        leadBase.setFatorUrgencia(false);
        leadBase.setCanalOrigem("Site");
        leadBase.setPlanoSaude(null);

        when(leadRepository.existsByEmail(any())).thenReturn(false);
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Lead resultado = leadService.cadastrar(leadBase);

        assertEquals("Médio", resultado.getLeadScore());
        assertEquals(3, resultado.getPrioridade());
    }

    @Test
    @DisplayName("Lead sem nenhum fator deve ter score Baixo")
    void leadSemFatoresDeveSerBaixo() {
        leadBase.setFatorUrgencia(false);
        leadBase.setCanalOrigem(null);
        leadBase.setPlanoSaude(null);

        when(leadRepository.existsByEmail(any())).thenReturn(false);
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Lead resultado = leadService.cadastrar(leadBase);

        assertEquals("Baixo", resultado.getLeadScore());
        assertEquals(4, resultado.getPrioridade());
    }

    // ─── Testes de Atualização de Status ─────────────────────────────────────

    @Test
    @DisplayName("Deve permitir transição de Novo para Em Atendimento")
    void devePermitirTransicaoNovoParaEmAtendimento() {
        leadBase.setStatus("Novo");
        when(leadRepository.findById(1L)).thenReturn(Optional.of(leadBase));
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Lead resultado = leadService.atualizarStatus(1L, "Em Atendimento");

        assertEquals("Em Atendimento", resultado.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exception para transição inválida de status")
    void deveLancarExceptionParaTransicaoInvalida() {
        leadBase.setStatus("Convertido");
        when(leadRepository.findById(1L)).thenReturn(Optional.of(leadBase));

        assertThrows(LeadStatusInvalidoException.class,
                () -> leadService.atualizarStatus(1L, "Novo"));
    }

    @Test
    @DisplayName("Deve retornar lista de leads urgentes")
    void deveListarLeadsUrgentes() {
        when(leadRepository.findLeadsUrgentesOrdenados()).thenReturn(List.of(leadBase));

        List<Lead> urgentes = leadService.listarUrgentes();

        assertFalse(urgentes.isEmpty());
        assertTrue(urgentes.get(0).getFatorUrgencia());
    }
}
