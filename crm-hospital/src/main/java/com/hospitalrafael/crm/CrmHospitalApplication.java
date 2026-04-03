package com.hospitalrafael.crm;

import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.service.AgendamentoService;
import com.hospitalrafael.crm.service.LeadService;
import com.hospitalrafael.crm.service.NotificacaoService;
import com.hospitalrafael.crm.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;

/**
 * Aplicação principal do CRM - Hospital São Rafael
 *
 * Challenge Sprint 3 (2ESPR)
 * Integrantes:
 *   - Alexandre Delfino  RM560059
 *   - Enzo Luciano       RM559557
 *   - Luigi Thiengo      RM560755
 *   - Pedro Claudino     RM561023
 *   - Samuel Backer      RM559269
 */
@SpringBootApplication
public class CrmHospitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmHospitalApplication.class, args);
    }

    /**
     * ZONA DE TESTES - Executado apenas com o perfil "test"
     * Demonstra as principais regras de negócio da aplicação
     */
    @Bean
    @Profile("test")
    CommandLineRunner zonaDeTestes(
            UsuarioService usuarioService,
            LeadService leadService,
            AgendamentoService agendamentoService,
            NotificacaoService notificacaoService
    ) {
        return args -> {
            System.out.println("\n========================================");
            System.out.println("  ZONA DE TESTES - CRM Hospital São Rafael");
            System.out.println("========================================\n");

            // ── TESTE 1: Criação de Usuário/Operador ──────────────────────────
            System.out.println("── TESTE 1: Criação de Usuário ──");
            try {
                Usuario operador = new Usuario();
                operador.setNome("Carlos Silva");
                operador.setEmail("carlos.silva@hospital.com");
                operador.setSenha("Senha@123");
                operador.setDoc("12345678901");
                operador.setTelefone(11999990001L);
                operador.setDataNasc(LocalDate.of(1990, 5, 15));

                Usuario salvo = usuarioService.cadastrar(operador);
                System.out.println("[OK] Usuário criado: " + salvo.getNome() + " | ID: " + salvo.getId());
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }

            // ── TESTE 2: Cadastro de Lead ─────────────────────────────────────
            System.out.println("\n── TESTE 2: Cadastro de Lead ──");
            try {
                Lead lead = new Lead();
                lead.setNome("Ana Pereira");
                lead.setEmail("ana.pereira@email.com");
                lead.setTelefone(11977770001L);
                lead.setCanalOrigem("Instagram");
                lead.setPlanoSaude("Unimed");
                lead.setProcedimentoInteresse("Consulta Cardiologista");
                lead.setFatorUrgencia(true);

                Lead leadSalvo = leadService.cadastrar(lead);
                System.out.println("[OK] Lead criado: " + leadSalvo.getNome() + " | Score: " + leadSalvo.getLeadScore());
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }

            // ── TESTE 3: Validação de Lead duplicado ──────────────────────────
            System.out.println("\n── TESTE 3: Validação de Lead duplicado ──");
            try {
                Lead leadDuplicado = new Lead();
                leadDuplicado.setNome("Ana Pereira");
                leadDuplicado.setEmail("ana.pereira@email.com");
                leadDuplicado.setTelefone(11977770001L);
                leadService.cadastrar(leadDuplicado);
                System.out.println("[FALHOU] Deveria ter lançado exception");
            } catch (Exception e) {
                System.out.println("[OK] Exception capturada corretamente: " + e.getMessage());
            }

            // ── TESTE 4: Cálculo de Lead Score ───────────────────────────────
            System.out.println("\n── TESTE 4: Cálculo de Lead Score ──");
            try {
                List<Lead> leads = leadService.listarPorPrioridade();
                System.out.println("[OK] Leads ordenados por prioridade: " + leads.size() + " encontrado(s)");
                leads.forEach(l -> System.out.println("   > " + l.getNome() + " | Score: " + l.getLeadScore() + " | Urgente: " + l.getFatorUrgencia()));
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }

            // ── TESTE 5: Busca de Leads urgentes ─────────────────────────────
            System.out.println("\n── TESTE 5: Busca de Leads urgentes ──");
            try {
                List<Lead> urgentes = leadService.listarUrgentes();
                System.out.println("[OK] Leads urgentes encontrados: " + urgentes.size());
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }

            // ── TESTE 6: Validação de CPF inválido ───────────────────────────
            System.out.println("\n── TESTE 6: Validação de CPF inválido ──");
            try {
                Usuario userInvalido = new Usuario();
                userInvalido.setNome("Teste");
                userInvalido.setEmail("teste@email.com");
                userInvalido.setSenha("Senha@123");
                userInvalido.setDoc("123"); // CPF inválido
                userInvalido.setTelefone(11999999999L);
                userInvalido.setDataNasc(LocalDate.of(1990, 1, 1));
                usuarioService.cadastrar(userInvalido);
                System.out.println("[FALHOU] Deveria ter lançado exception");
            } catch (Exception e) {
                System.out.println("[OK] Exception capturada corretamente: " + e.getMessage());
            }

            System.out.println("\n========================================");
            System.out.println("  FIM DA ZONA DE TESTES");
            System.out.println("========================================\n");
        };
    }
}
