package business.interfaces;

import entidades.Fornecedor;

import java.util.List;

public interface IGerenciamentoFornecedor{

    public abstract boolean cadastrarFornecedor(Fornecedor fornecedor);
    public abstract Fornecedor buscarFornecedor(int id);
    public abstract List<Fornecedor> listarFornecedores();
    public abstract List<Fornecedor> buscarPesquisa(String valor, String campo);
    public abstract boolean atualizarFornecedor(Fornecedor fornecedorNovo);
    public abstract boolean desativarFornecedor(int id);
    public abstract int gerarProximoId();
    

}