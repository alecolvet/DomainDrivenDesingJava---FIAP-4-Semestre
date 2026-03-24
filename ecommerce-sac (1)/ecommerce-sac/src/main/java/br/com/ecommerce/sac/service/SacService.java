package br.com.ecommerce.sac.service;

import br.com.ecommerce.sac.dao.ClienteDAO;
import br.com.ecommerce.sac.dao.TicketDAO;
import br.com.ecommerce.sac.exception.SacException;
import br.com.ecommerce.sac.factory.DAOFactory;
import br.com.ecommerce.sac.model.Cliente;
import br.com.ecommerce.sac.model.Ticket;

import java.util.List;

/**
 * Camada de serviço do módulo SAC.
 * Concentra as regras de negócio e as 3 funcionalidades principais:
 *
 *   1. Abertura de Ticket de suporte
 *   2. Atualização de status do Ticket
 *   3. Consulta de histórico de Tickets de um cliente
 */
public class SacService {

    private final TicketDAO  ticketDAO;
    private final ClienteDAO clienteDAO;

    // Statuses válidos
    private static final List<String> STATUS_VALIDOS =
        List.of("ABERTO", "EM_ANDAMENTO", "RESOLVIDO", "FECHADO");

    // Prioridades válidas
    private static final List<String> PRIORIDADES_VALIDAS =
        List.of("BAIXA", "MEDIA", "ALTA", "CRITICA");

    public SacService() {
        // Usa o Factory para obter os DAOs (aplica ambos os padrões: Factory + DAO)
        this.ticketDAO  = DAOFactory.criarTicketDAO();
        this.clienteDAO = DAOFactory.criarClienteDAO();
    }

    // ══════════════════════════════════════════════════════════════
    // FUNCIONALIDADE 1 — Abertura de Ticket
    // ══════════════════════════════════════════════════════════════

    /**
     * Abre um novo ticket de suporte para um cliente.
     * Valida o cliente, título, descrição e prioridade antes de persistir.
     *
     * @return Ticket criado com ID gerado
     */
    public Ticket abrirTicket(int clienteId, String titulo, String descricao, String prioridade) {
        // Validações de negócio
        if (titulo == null || titulo.isBlank()) {
            throw new SacException("SAC_TICKET_TITULO", "O título do ticket não pode ser vazio.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new SacException("SAC_TICKET_DESC", "A descrição do ticket não pode ser vazia.");
        }
        if (!PRIORIDADES_VALIDAS.contains(prioridade.toUpperCase())) {
            throw new SacException("SAC_TICKET_PRIO",
                "Prioridade inválida: '" + prioridade + "'. Use: " + PRIORIDADES_VALIDAS);
        }

        // Verifica se o cliente existe
        Cliente cliente = clienteDAO.buscarPorId(clienteId);

        Ticket ticket = new Ticket(titulo, descricao, prioridade.toUpperCase(), cliente.getId());
        ticketDAO.salvar(ticket);

        System.out.println("\n✅ Ticket #" + ticket.getId() + " aberto com sucesso para o cliente: " + cliente.getNome());
        return ticket;
    }

    // ══════════════════════════════════════════════════════════════
    // FUNCIONALIDADE 2 — Atualização de Status do Ticket
    // ══════════════════════════════════════════════════════════════

    /**
     * Atualiza o status de um ticket existente.
     * Aplica regras de transição de status (fluxo de atendimento).
     */
    public Ticket atualizarStatusTicket(int ticketId, String novoStatus) {
        if (!STATUS_VALIDOS.contains(novoStatus.toUpperCase())) {
            throw new SacException("SAC_STATUS_INVALIDO",
                "Status inválido: '" + novoStatus + "'. Use: " + STATUS_VALIDOS);
        }

        Ticket ticket = ticketDAO.buscarPorId(ticketId);

        // Regra de negócio: ticket FECHADO não pode ser reaberto por esta operação
        if ("FECHADO".equals(ticket.getStatus()) && !"FECHADO".equals(novoStatus.toUpperCase())) {
            throw new SacException("SAC_STATUS_FECHADO",
                "Ticket #" + ticketId + " está FECHADO e não pode ter o status alterado.");
        }

        ticket.setStatus(novoStatus.toUpperCase());
        ticketDAO.atualizar(ticket);

        System.out.println("\n✅ Status do Ticket #" + ticketId + " atualizado para: " + novoStatus.toUpperCase());
        return ticket;
    }

    // ══════════════════════════════════════════════════════════════
    // FUNCIONALIDADE 3 — Histórico de Tickets do Cliente
    // ══════════════════════════════════════════════════════════════

    /**
     * Retorna o histórico completo de tickets de um cliente,
     * exibindo um resumo formatado no console.
     */
    public List<Ticket> consultarHistoricoCliente(int clienteId) {
        // Verifica se o cliente existe
        Cliente cliente = clienteDAO.buscarPorId(clienteId);

        List<Ticket> tickets = ticketDAO.buscarPorCliente(clienteId);

        System.out.println("\n📋 Histórico de Tickets — Cliente: " + cliente.getNome());
        System.out.println("─".repeat(60));

        if (tickets.isEmpty()) {
            System.out.println("   Nenhum ticket encontrado para este cliente.");
        } else {
            for (Ticket t : tickets) {
                System.out.printf("  #%-4d | %-8s | %-10s | %s%n",
                    t.getId(), t.getStatus(), t.getPrioridade(), t.getTitulo());
            }
            System.out.println("─".repeat(60));
            System.out.println("   Total: " + tickets.size() + " ticket(s)");
        }

        return tickets;
    }

    // ══════════════════════════════════════════════════════════════
    // Operações auxiliares de Cliente
    // ══════════════════════════════════════════════════════════════

    public Cliente cadastrarCliente(String nome, String email, String cpf, String telefone) {
        if (email == null || !email.contains("@")) {
            throw new SacException("SAC_CLI_EMAIL", "E-mail inválido: " + email);
        }
        Cliente cliente = new Cliente(nome, email, cpf, telefone);
        clienteDAO.salvar(cliente);
        System.out.println("\n✅ Cliente cadastrado: " + cliente);
        return cliente;
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.listarTodos();
    }

    public List<Ticket> listarTicketsPorStatus(String status) {
        if (!STATUS_VALIDOS.contains(status.toUpperCase())) {
            throw new SacException("SAC_STATUS_INVALIDO", "Status inválido: " + status);
        }
        return ticketDAO.buscarPorStatus(status.toUpperCase());
    }
}
