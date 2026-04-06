package com.hospitalrafael.crm;

import com.hospitalrafael.crm.connection.ConexaoBanco;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.model.Agendamento;
import com.hospitalrafael.crm.repository.AgendamentoRepository;
import com.hospitalrafael.crm.repository.LeadRepository;
import com.hospitalrafael.crm.repository.UsuarioRepository;
import com.hospitalrafael.crm.service.AgendamentoService;
import com.hospitalrafael.crm.service.LeadService;
import com.hospitalrafael.crm.service.UsuarioService;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe principal da aplicação CRM Hospital São Rafael.
 *
 * Challenge Sprint 3 (2ESPR)
 * Integrantes:
 *   - Alexandre Delfino  RM560059
 *   - Enzo Luciano       RM559557
 *   - Luigi Thiengo      RM560755
 *   - Pedro Claudino     RM561023
 *   - Samuel Backer      RM559269
 *
 * Tecnologias: Java 17 + JDBC + Oracle
 */
public class Main {

    public static void main(String[] args) {

        // ──────────────────────────────────────────────────────────────────────
        // INICIALIZAÇÃO DOS SERVIÇOS
        // ──────────────────────────────────────────────────────────────────────
        LeadRepository      leadRepo      = new LeadRepository();
        UsuarioRepository   usuarioRepo   = new UsuarioRepository();
        AgendamentoRepository agendRepo   = new AgendamentoRepository();

        LeadService         leadService      = new LeadService(leadRepo);
        UsuarioService      usuarioService   = new UsuarioService(usuarioRepo);
        AgendamentoService  agendService     = new AgendamentoService(agendRepo);

        // ──────────────────────────────────────────────────────────────────────
        //  ZONA DE TESTES
        //  Testa as principais regras de negócio com dados hardcoded.
        // ──────────────────────────────────────────────────────────────────────

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║       ZONA DE TESTES - CRM Hospital São Rafael           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        // ── TESTE 0: Conexão com o banco ──────────────────────────────────────
        System.out.println("\n── TESTE 0: Verificando conexão com Oracle ──");
        boolean conectado = ConexaoBanco.testarConexao();
        if (conectado) {
            System.out.println("[OK] Conexão com Oracle estabelecida com sucesso.");
        } else {
            System.out.println("[AVISO] Sem conexão com Oracle. Preencha RM e senha em ConexaoBanco.java");
            System.out.println("        Os testes abaixo serão ignorados sem conexão ativa.\n");
            return;
        }

        // ── TESTE 1: Cadastro de Usuário/Operador ─────────────────────────────
        System.out.println("\n── TESTE 1: Cadastro de Usuário (operador) ──");
        Usuario operador = null;
        try {
            operador = new Usuario();
            operador.setNome("Carlos Silva");
            operador.setEmail("carlos.silva@hospital.com");
            operador.setSenha("Senha@123");
            operador.setDoc("12345678901");
            operador.setTelefone(11999990001L);
            operador.setDataNasc(LocalDate.of(1990, 5, 15));

            operador = usuarioService.cadastrar(operador);
            System.out.println("[OK] Usuário criado: " + operador.getNome() + " | ID: " + operador.getId());
        } catch (Exception e) {
            System.out.println("[ERRO] " + e.getMessage());
        }

        // ── TESTE 2: CPF inválido deve lançar exceção ─────────────────────────
        System.out.println("\n── TESTE 2: CPF inválido deve lançar CpfInvalidoException ──");
        try {
            Usuario invalido = new Usuario();
            invalido.setNome("Teste");
            invalido.setEmail("teste.invalido@email.com");
            invalido.setSenha("Senha@123");
            invalido.setDoc("123");  // CPF com menos de 11 dígitos
            invalido.setTelefone(11000000000L);
            invalido.setDataNasc(LocalDate.of(1995, 1, 1));

            usuarioService.cadastrar(invalido);
            System.out.println("[FALHOU] Deveria ter lançado CpfInvalidoException");
        } catch (Exception e) {
            System.out.println("[OK] Exception capturada: " + e.getMessage());
        }

        // ── TESTE 3: Cadastro de Lead com cálculo de Lead Score ───────────────
        System.out.println("\n── TESTE 3: Cadastro de Lead com cálculo de Lead Score ──");
        Lead lead = null;
        try {
            lead = new Lead();
            lead.setNome("Ana Pereira");
            lead.setEmail("ana.pereira@email.com");
            lead.setTelefone(11977770001L);
            lead.setCanalOrigem("Indicação");
            lead.setPlanoSaude("Unimed");
            lead.setProcedimentoInteresse("Consulta Cardiologista");
            lead.setFatorUrgencia(true);

            lead = leadService.cadastrar(lead);
            System.out.println("[OK] Lead criado: " + lead.getNome());
            System.out.println("     Score: " + lead.getLeadScore() + " | Prioridade: " + lead.getPrioridade());
        } catch (Exception e) {
            System.out.println("[ERRO] " + e.getMessage());
        }

        // ── TESTE 4: Lead duplicado deve lançar exceção ───────────────────────
        System.out.println("\n── TESTE 4: Lead duplicado deve lançar LeadDuplicadoException ──");
        try {
            Lead duplicado = new Lead();
            duplicado.setNome("Ana Pereira");
            duplicado.setEmail("ana.pereira@email.com");  // mesmo e-mail
            duplicado.setTelefone(11977770001L);

            leadService.cadastrar(duplicado);
            System.out.println("[FALHOU] Deveria ter lançado LeadDuplicadoException");
        } catch (Exception e) {
            System.out.println("[OK] Exception capturada: " + e.getMessage());
        }

        // ── TESTE 5: Listagem de leads por prioridade ─────────────────────────
        System.out.println("\n── TESTE 5: Listagem de leads por prioridade ──");
        try {
            List<Lead> leads = leadService.listarPorPrioridade();
            System.out.println("[OK] Total de leads: " + leads.size());
            for (Lead l : leads) {
                System.out.println("     > " + l.getNome() + " | Score: " + l.getLeadScore()
                        + " | Prioridade: " + l.getPrioridade()
                        + " | Urgente: " + l.getFatorUrgencia());
            }
        } catch (Exception e) {
            System.out.println("[ERRO] " + e.getMessage());
        }

        // ── TESTE 6: Atualização de status — transição inválida ───────────────
        System.out.println("\n── TESTE 6: Transição de status inválida deve lançar exceção ──");
        if (lead != null) {
            try {
                // "Novo" → "Convertido" não é uma transição permitida
                leadService.atualizarStatus(lead.getId(), "Convertido");
                System.out.println("[FALHOU] Deveria ter lançado LeadStatusInvalidoException");
            } catch (Exception e) {
                System.out.println("[OK] Exception capturada: " + e.getMessage());
            }
        } else {
            System.out.println("[PULADO] Lead não foi criado no teste anterior.");
        }

        // ── TESTE 7: Atualização de status — transição válida ─────────────────
        System.out.println("\n── TESTE 7: Transição de status válida (Novo → Em Atendimento) ──");
        if (lead != null) {
            try {
                Lead atualizado = leadService.atualizarStatus(lead.getId(), "Em Atendimento");
                System.out.println("[OK] Status atualizado: " + atualizado.getStatus());
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }
        } else {
            System.out.println("[PULADO] Lead não disponível.");
        }

        // ── TESTE 8: Agendamento com data no passado ──────────────────────────
        System.out.println("\n── TESTE 8: Agendamento com data passada deve lançar exceção ──");
        try {
            Agendamento passado = new Agendamento();
            passado.setLeadId(lead != null ? lead.getId() : 1L);
            passado.setOperadorId(operador != null ? operador.getId() : 1L);
            passado.setProcedimento("Consulta");
            passado.setDataHora(LocalDate.now().minusDays(5));  // data passada

            agendService.criar(passado);
            System.out.println("[FALHOU] Deveria ter lançado AgendamentoDataPassadaException");
        } catch (Exception e) {
            System.out.println("[OK] Exception capturada: " + e.getMessage());
        }

        // ── TESTE 9: Agendamento válido ───────────────────────────────────────
        System.out.println("\n── TESTE 9: Criar agendamento com data futura ──");
        Agendamento agendamento = null;
        if (lead != null && operador != null) {
            try {
                agendamento = new Agendamento();
                agendamento.setLeadId(lead.getId());
                agendamento.setOperadorId(operador.getId());
                agendamento.setProcedimento("Consulta Cardiologista");
                agendamento.setDataHora(LocalDate.now().plusDays(7));

                agendamento = agendService.criar(agendamento);
                System.out.println("[OK] Agendamento criado | ID: " + agendamento.getId()
                        + " | Data: " + agendamento.getDataHora()
                        + " | Status: " + agendamento.getStatus());
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }
        } else {
            System.out.println("[PULADO] Lead ou operador não disponíveis.");
        }

        // ── TESTE 10: Confirmar agendamento ──────────────────────────────────
        System.out.println("\n── TESTE 10: Confirmar agendamento ──");
        if (agendamento != null) {
            try {
                Agendamento confirmado = agendService.confirmar(agendamento.getId());
                System.out.println("[OK] Status atualizado para: " + confirmado.getStatus());
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }
        } else {
            System.out.println("[PULADO] Agendamento não disponível.");
        }

        // ── TESTE 11: Estatísticas via SQL GROUP BY ───────────────────────────
        System.out.println("\n── TESTE 11: Estatísticas de leads via JDBC (GROUP BY) ──");
        try {
            leadService.imprimirEstatisticas();
            System.out.println("[OK] Estatísticas geradas com sucesso.");
        } catch (Exception e) {
            System.out.println("[ERRO] " + e.getMessage());
        }

        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              FIM DA ZONA DE TESTES                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
}
