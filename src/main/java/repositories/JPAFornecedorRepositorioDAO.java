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

    public ArrayList<Fornecedor> removeDuplicado(ArrayList<Fornecedor> lista){
    	for (int i = 0; i < lista.size() - 1; i++) {
			if (lista.get(i).getId() == lista.get(i+1).getId()) {
				lista.remove(i);
			}
		}
    	return lista;
    }
    
    @Override
    public List<Fornecedor> buscarPesquisa(String valor, String campo) {
    	ArrayList<Fornecedor> fornecedores = new ArrayList<>();
    	if (campo.equals("todos")) {
    		fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, "id", valor));
    		fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, "razaoSocial", valor));
    		fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, "cnpj", valor));
    		fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, "email", valor));
    		fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, "telefone", valor));
    		
    		fornecedores = removeDuplicado(fornecedores);
    	}
    	
    	else {
    		fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, campo, valor));
    	}
    	
    	return fornecedores;
    	
    }


    @Override
    public Fornecedor buscarFornecedor(int id) {
        return manipulaArquivo.buscaUnica(Fornecedor.class, id);

    }

}