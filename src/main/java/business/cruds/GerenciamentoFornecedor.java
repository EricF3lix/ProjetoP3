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

        if (repositorio.buscarPorId(fornecedor.getId()) != null) {
            return false;
        }

        if (repositorio.buscarPorCnpj(fornecedor.getCnpj()) != null) {
            return false;
        }

        repositorio.adicionar(fornecedor);

        return true;
    }

    @Override
    public Fornecedor buscarFornecedor(int id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Fornecedor> buscarPesquisa(String valor, String campo){
    	
    }

    @Override
    public List<Fornecedor> listarFornecedores() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean atualizarFornecedor(Fornecedor fornecedorNovo) {

        List<Fornecedor> fornecedores = repositorio.listarTodos();

        for (int i = 0; i < fornecedores.size(); i++){
            if (fornecedores.get(i).getId() == fornecedorNovo.getId()){
                fornecedores.set(i, fornecedorNovo);
                repositorio.salvar();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean desativarFornecedor(int id) {

        Fornecedor fornecedor = repositorio.buscarPorId(id);

        if (fornecedor == null) {
            return false;
        }

        repositorio.desativar(id); 

        return true;
    }

    @Override
    public int gerarProximoId() {
        return repositorio.gerarProximoId();
    }
}