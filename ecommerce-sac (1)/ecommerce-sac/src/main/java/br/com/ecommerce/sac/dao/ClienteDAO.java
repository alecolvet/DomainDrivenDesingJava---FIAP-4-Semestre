package br.com.ecommerce.sac.dao;

import br.com.ecommerce.sac.model.Cliente;
import java.util.List;

/**
 * Interface DAO (Data Access Object) para a entidade Cliente.
 */
public interface ClienteDAO {

    void salvar(Cliente cliente);

    Cliente buscarPorId(int id);

    Cliente buscarPorEmail(String email);

    void atualizar(Cliente cliente);

    void deletar(int id);

    List<Cliente> listarTodos();
}
