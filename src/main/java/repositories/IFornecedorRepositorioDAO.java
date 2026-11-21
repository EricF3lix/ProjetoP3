package repositories;

import entidades.Fornecedor;

import java.util.List;

public interface IFornecedorRepositorioDAO {

    public abstract void adicionar(Fornecedor fornecedor);

    public abstract List<Fornecedor> listarTodos();

    public abstract void desativar(int id);

    public abstract List<Fornecedor> buscarPesquisa(String valor, String campo);

    public abstract Fornecedor buscarFornecedor(int id);


}