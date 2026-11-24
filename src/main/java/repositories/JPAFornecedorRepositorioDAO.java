package repositories;

import java.util.ArrayList;
import java.util.List;

import entidades.Fornecedor;

public class JPAFornecedorRepositorioDAO implements IFornecedorRepositorioDAO {

    private static final String CAMPO_RAZAO_SOCIAL = "razaoSocial";

    private final ManipulaArquivo manipulaArquivo;

    public JPAFornecedorRepositorioDAO() {

        manipulaArquivo = new ManipulaArquivo();

    }

    @Override
    public boolean adicionar(Fornecedor fornecedor) {
        return manipulaArquivo.escrever(fornecedor);

    }

    @Override
    public List<Fornecedor> listarTodos() {
        return manipulaArquivo.carregarTudo(Fornecedor.class);

    }

    @Override
    public boolean atualizar(Fornecedor fornecedor) { 
    	ArrayList<Boolean> sucesso = new ArrayList<>();
    	
    	sucesso.add(manipulaArquivo.atualizar(Fornecedor.class, CAMPO_RAZAO_SOCIAL, fornecedor.getRazaoSocial(), fornecedor.getId()));
    	sucesso.add(manipulaArquivo.atualizar(Fornecedor.class, "cnpj", fornecedor.getCnpj(), fornecedor.getId()));
    	sucesso.add(manipulaArquivo.atualizar(Fornecedor.class, "email", fornecedor.getEmail(), fornecedor.getId()));
    	sucesso.add(manipulaArquivo.atualizar(Fornecedor.class, "telefone", fornecedor.getTelefone(), fornecedor.getId()));
    	sucesso.add(manipulaArquivo.atualizar(Fornecedor.class, "ativo", true, fornecedor.getId()));
    	
    	return !sucesso.contains(false);
    }
    
    @Override
    public boolean desativar(int id) {
    	return manipulaArquivo.desativar(Fornecedor.class, id);

        }

    public List<Fornecedor> removeDuplicado(List<Fornecedor> lista){
    	for (int i = lista.size() - 1; i > 0; i--) {
    	    if (lista.get(i).getId() == lista.get(i - 1).getId()) {
    	        lista.remove(i);
    	    }
    	}
    	return lista;
    }
    
    @Override
    public List<Fornecedor> buscarPesquisa(String valor, String campo) {
        ArrayList<Fornecedor> fornecedores = new ArrayList<>();

        if ("todos".equalsIgnoreCase(campo)) {
            fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, "id", valor));
            fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, CAMPO_RAZAO_SOCIAL, valor));
            fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, "cnpj", valor));
            fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, "email", valor));
            fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, "telefone", valor));

            return new ArrayList<>(removeDuplicado(fornecedores));
        }

        String campoBusca = "nome".equalsIgnoreCase(campo) ? CAMPO_RAZAO_SOCIAL : campo;
        fornecedores.addAll(manipulaArquivo.buscar(Fornecedor.class, campoBusca, valor));

        return fornecedores;
    }


    @Override
    public Fornecedor buscarFornecedor(int id) {
        return manipulaArquivo.buscaUnica(Fornecedor.class, id);

    }

}