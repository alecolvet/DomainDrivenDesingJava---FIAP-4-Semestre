package br.com.ecommerce.sac.main;

import br.com.ecommerce.sac.connection.ConexaoBD;
import br.com.ecommerce.sac.exception.SacException;
import br.com.ecommerce.sac.model.Cliente;
import br.com.ecommerce.sac.model.Ticket;
import br.com.ecommerce.sac.service.SacService;

import java.util.List;

/**
 * Ponto de entrada da aplicação — demonstra todas as funcionalidades do módulo SAC.
 *
 * Módulo: SAC (Serviço de Atendimento ao Cliente)
 * Domínio: www.ecommerce.com.br
 */
public class Main {

    public static void main(String[] args) {

        SacService sacService = new SacService();

        System.out.println("═".repeat(60));
        System.out.println("    MÓDULO SAC — Serviço de Atendimento ao Cliente");
        System.out.println("    www.ecommerce.com.br");
        System.out.println("═".repeat(60));

        try {

            // ─────────────────────────────────────────────────
            // CADASTRO DE CLIENTES
            // ─────────────────────────────────────────────────
            System.out.println("\n>>> Cadastrando clientes...");
            Cliente joao  = sacService.cadastrarCliente("João Silva",  "joao@email.com",  "111.222.333-44", "(11) 91111-2222");
            Cliente maria = sacService.cadastrarCliente("Maria Santos","maria@email.com", "555.666.777-88", "(11) 93333-4444");

            // ─────────────────────────────────────────────────
            // FUNCIONALIDADE 1 — Abertura de Tickets
            // ─────────────────────────────────────────────────
            System.out.println("\n>>> Funcionalidade 1: Abertura de Tickets");

            Ticket t1 = sacService.abrirTicket(
                joao.getId(),
                "Produto não entregue",
                "Meu pedido #98765 devia chegar ontem e não foi entregue.",
                "ALTA"
            );

            Ticket t2 = sacService.abrirTicket(
                joao.getId(),
                "Produto com defeito",
                "O celular que comprei não liga desde que chegou.",
                "CRITICA"
            );

            Ticket t3 = sacService.abrirTicket(
                maria.getId(),
                "Dúvida sobre devolução",
                "Como faço para devolver um produto dentro do prazo?",
                "BAIXA"
            );

            // ─────────────────────────────────────────────────
            // FUNCIONALIDADE 2 — Atualização de Status
            // ─────────────────────────────────────────────────
            System.out.println("\n>>> Funcionalidade 2: Atualização de Status");

            sacService.atualizarStatusTicket(t1.getId(), "EM_ANDAMENTO");
            sacService.atualizarStatusTicket(t2.getId(), "EM_ANDAMENTO");
            sacService.atualizarStatusTicket(t3.getId(), "RESOLVIDO");
            sacService.atualizarStatusTicket(t3.getId(), "FECHADO");

            // ─────────────────────────────────────────────────
            // FUNCIONALIDADE 3 — Histórico do Cliente
            // ─────────────────────────────────────────────────
            System.out.println("\n>>> Funcionalidade 3: Histórico de Tickets");

            sacService.consultarHistoricoCliente(joao.getId());
            sacService.consultarHistoricoCliente(maria.getId());

            // ─────────────────────────────────────────────────
            // DEMONSTRAÇÃO DO TRATAMENTO DE EXCEPTION
            // ─────────────────────────────────────────────────
            System.out.println("\n>>> Demonstração da SacException (validação de negócio):");
            try {
                sacService.abrirTicket(joao.getId(), "", "sem título", "ALTA");
            } catch (SacException e) {
                System.out.println("  ⚠ Exception capturada: " + e);
            }

            try {
                sacService.atualizarStatusTicket(t3.getId(), "ABERTO"); // ticket já fechado
            } catch (SacException e) {
                System.out.println("  ⚠ Exception capturada: " + e);
            }

            // ─────────────────────────────────────────────────
            // DEMONSTRAÇÃO DO SINGLETON
            // ─────────────────────────────────────────────────
            System.out.println("\n>>> Demonstração do Singleton (mesma instância de conexão):");
            ConexaoBD instancia1 = ConexaoBD.getInstancia();
            ConexaoBD instancia2 = ConexaoBD.getInstancia();
            System.out.println("  Instância 1: " + System.identityHashCode(instancia1));
            System.out.println("  Instância 2: " + System.identityHashCode(instancia2));
            System.out.println("  São a mesma instância? " + (instancia1 == instancia2));

            // ─────────────────────────────────────────────────
            // LISTAGEM DE TICKETS ABERTOS
            // ─────────────────────────────────────────────────
            System.out.println("\n>>> Tickets com status EM_ANDAMENTO:");
            List<Ticket> emAndamento = sacService.listarTicketsPorStatus("EM_ANDAMENTO");
            emAndamento.forEach(t -> System.out.println("  " + t));

        } catch (SacException e) {
            System.err.println("\n❌ ERRO SAC: " + e);
        } finally {
            ConexaoBD.getInstancia().fecharConexao();
        }

        System.out.println("\n" + "═".repeat(60));
        System.out.println("    Execução finalizada.");
        System.out.println("═".repeat(60));
    }
}
