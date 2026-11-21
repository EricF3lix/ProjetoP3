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

        fornecedores.add(fornecedor);

        salvar();

    }

    @Override
    public List<Fornecedor> listarTodos() {

        return fornecedores;

    }

    @Override
    public void desativar(int id) {

        Fornecedor fornecedor = buscarPorId(id);

        if (fornecedor != null) {

            fornecedor.desativar();

            salvar();

        }

    }

    @Override
    public Fornecedor buscarPesquisa(String valor, String campo) {

        

    }


    @Override
    public Fornecedor buscarFornecedor(int id) {

        

    }

}