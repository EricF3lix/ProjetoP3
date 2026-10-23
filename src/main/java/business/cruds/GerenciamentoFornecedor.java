package business.cruds;

import java.util.List;

import business.interfaces.IGerenciamentoFornecedor;
import entidades.Fornecedor;
import repositories.IFornecedorRepositorio;

public class GerenciamentoFornecedor implements IGerenciamentoFornecedor {

    private final IFornecedorRepositorio repositorio;

    public GerenciamentoFornecedor(IFornecedorRepositorio repositorio) {
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
    public List<Fornecedor> buscarPorCnpj(String cnpj) {

        return repositorio.listarTodos().stream()
                .filter(f -> f.getCnpj().trim().startsWith(cnpj.trim()))
                .toList();
    }

    @Override
    public List<Fornecedor> buscarPorNome(String nome) {

        return repositorio.listarTodos().stream()
                .filter(f -> f.getRazaoSocial().trim().toLowerCase().startsWith(nome.trim().toLowerCase()))
                .toList();
    }

    @Override
    public List<Fornecedor> buscarPorEmail(String email) {

        return repositorio.listarTodos().stream()
                .filter(f -> f.getEmail().trim().toLowerCase().startsWith(email.trim().toLowerCase()))
                .toList();
    }

    @Override
    public List<Fornecedor> buscarPorTelefone(String telefone) {

        return repositorio.listarTodos().stream()
                .filter(f -> f.getTelefone().trim().startsWith(telefone.trim()))
                .toList();
    }
    
    @Override
    public List<Fornecedor> buscarPorId(String id) {

        return repositorio.listarTodos().stream()
                .filter(f -> String.valueOf(f.getId()).startsWith(id.trim()))
                .toList();
    }
    
    @Override
    public List<Fornecedor> buscarPorTodos(String texto) {

        return repositorio.listarTodos().stream()
                .filter(f -> String.valueOf(f.getId()).equals(texto.trim())
                        || f.getRazaoSocial().trim().toLowerCase().startsWith(texto.trim().toLowerCase())
                        || f.getCnpj().trim().startsWith(texto.trim())
                        || f.getEmail().trim().toLowerCase().startsWith(texto.trim().toLowerCase())
                        || f.getTelefone().trim().startsWith(texto.trim()))
                .toList();
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