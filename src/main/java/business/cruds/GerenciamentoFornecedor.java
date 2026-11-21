package business.cruds;

import business.interfaces.IGerenciamentoFornecedor;
import entidades.Fornecedor;
import repositories.IFornecedorRepositorioDAO;

import java.util.List;

public class GerenciamentoFornecedor implements IGerenciamentoFornecedor {

    private final IFornecedorRepositorioDAO repositorio;

    public GerenciamentoFornecedor(IFornecedorRepositorioDAO repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean cadastrarFornecedor(Fornecedor fornecedor) {
        return repositorio.adicionar(fornecedor);

    }

    @Override
    public Fornecedor buscarFornecedor(int id) {
        return repositorio.buscarFornecedor(id);
    }

    @Override
    public List<Fornecedor> buscarPesquisa(String valor, String campo){
    	return repositorio.buscarPesquisa(valor, campo);
    }

    @Override
    public List<Fornecedor> listarFornecedores() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean atualizarFornecedor(Fornecedor fornecedorNovo) {
    	return repositorio.atualizar(fornecedorNovo);
        
    }

    @Override
    public boolean desativarFornecedor(int id) {
        return repositorio.desativar(id);
        
        }
}