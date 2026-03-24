package br.com.ecommerce.sac.factory;

import br.com.ecommerce.sac.dao.ClienteDAO;
import br.com.ecommerce.sac.dao.TicketDAO;
import br.com.ecommerce.sac.dao.impl.ClienteDAOImpl;
import br.com.ecommerce.sac.dao.impl.TicketDAOImpl;
import br.com.ecommerce.sac.exception.SacException;

/**
 * Padrão Factory — centraliza a criação dos objetos DAO.
 * Encapsula qual implementação concreta será utilizada,
 * facilitando a troca de tecnologia de persistência no futuro.
 */
public class DAOFactory {

    // Impede instanciação — classe utilitária estática
    private DAOFactory() {}

    /**
     * Retorna a implementação correta do DAO solicitado.
     *
     * @param tipo String com o tipo do DAO: "ticket" ou "cliente"
     * @return instância do DAO correspondente
     */
    public static Object criarDAO(String tipo) {
        if (tipo == null) {
            throw new SacException("SAC_FACTORY_NULL", "Tipo de DAO não pode ser nulo.");
        }

        switch (tipo.toLowerCase().trim()) {
            case "ticket":
                return criarTicketDAO();
            case "cliente":
                return criarClienteDAO();
            default:
                throw new SacException("SAC_FACTORY_TIPO",
                    "Tipo de DAO desconhecido: '" + tipo + "'. Use 'ticket' ou 'cliente'.");
        }
    }

    /** Retorna uma instância de TicketDAO. */
    public static TicketDAO criarTicketDAO() {
        System.out.println("[DAOFactory] Criando TicketDAOImpl...");
        return new TicketDAOImpl();
    }

    /** Retorna uma instância de ClienteDAO. */
    public static ClienteDAO criarClienteDAO() {
        System.out.println("[DAOFactory] Criando ClienteDAOImpl...");
        return new ClienteDAOImpl();
    }
}
