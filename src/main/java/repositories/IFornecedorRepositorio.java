package repositories;

import entidades.Fornecedor;

import java.util.List;

public interface IFornecedorRepositorio {

    public abstract void adicionar(Fornecedor fornecedor);

    public abstract List<Fornecedor> listarTodos();
    
    public abstract void desativar(int id);

    public abstract Fornecedor buscarPorCnpj(String cnpj);

    public abstract Fornecedor buscarPorId(int id);

    public abstract void salvar();

    public abstract int gerarProximoId();

}
