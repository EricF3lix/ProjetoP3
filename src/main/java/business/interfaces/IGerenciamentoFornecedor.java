package business.interfaces;
import java.util.List;

import entidades.Fornecedor;

public interface IGerenciamentoFornecedor{

    public abstract boolean cadastrarFornecedor(Fornecedor fornecedor);
    public abstract Fornecedor buscarFornecedor(int id);
    public abstract List<Fornecedor> listarFornecedores();
    public abstract List<Fornecedor> buscarPorId(String id);
    public abstract List<Fornecedor> buscarPorCnpj(String cnpj);
    public abstract List<Fornecedor> buscarPorNome(String nome);
    public abstract List<Fornecedor> buscarPorEmail(String email);
    public abstract List<Fornecedor> buscarPorTelefone(String telefone);
    public abstract List<Fornecedor> buscarPorTodos(String texto);
    public abstract boolean atualizarFornecedor(Fornecedor fornecedorNovo);
    public abstract boolean desativarFornecedor(int id);
    public abstract int gerarProximoId();
    

}