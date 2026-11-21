package repositories;

import entidades.Fornecedor;

import java.util.ArrayList;
import java.util.List;

public class JPAFornecedorRepositorioDAO implements IFornecedorRepositorioDAO {

    private final ManipulaArquivo manipulaArquivo;

    public JPAFornecedorRepositorioDAO() {

        manipulaArquivo = new ManipulaArquivo();

    }

    @Override
    public void adicionar(Fornecedor fornecedor) {
        manipulaArquivo.escrever(fornecedor);

    }

    @Override
    public List<Fornecedor> listarTodos() {
        return manipulaArquivo.carregarTudo(Fornecedor.class);

    }

    @Override
    public void desativar(int id) {
    	manipulaArquivo.desativar(Fornecedor.class, id);

        }

    

    @Override
    public Fornecedor buscarPesquisa(String valor, String campo) {


    }


    @Override
    public Fornecedor buscarFornecedor(int id) {

        

    }

}