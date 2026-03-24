package br.com.ecommerce.sac.dao;

import br.com.ecommerce.sac.model.Ticket;
import java.util.List;

/**
 * Interface DAO (Data Access Object) para a entidade Ticket.
 * Define o contrato das operações de CRUD.
 */
public interface TicketDAO {

    /** Persiste um novo Ticket no banco. */
    void salvar(Ticket ticket);

    /** Busca um Ticket pelo seu ID. */
    Ticket buscarPorId(int id);

    /** Busca todos os Tickets de um cliente específico. */
    List<Ticket> buscarPorCliente(int clienteId);

    /** Busca todos os Tickets com um determinado status. */
    List<Ticket> buscarPorStatus(String status);

    /** Atualiza os dados de um Ticket existente. */
    void atualizar(Ticket ticket);

    /** Remove um Ticket pelo ID. */
    void deletar(int id);

    /** Lista todos os Tickets. */
    List<Ticket> listarTodos();
}
