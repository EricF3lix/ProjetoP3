package repositories;

import entidades.Fornecedor;

import java.util.List;

public interface IFornecedorRepositorioDAO {

    public abstract boolean adicionar(Fornecedor fornecedor);

    public abstract List<Fornecedor> listarTodos();
    
    public abstract boolean atualizar(Fornecedor fornecedor);

    public abstract boolean desativar(int id);
    
    public abstract List<Fornecedor> buscarPesquisa(String valor, String campo);

    public abstract Fornecedor buscarFornecedor(int id);


}