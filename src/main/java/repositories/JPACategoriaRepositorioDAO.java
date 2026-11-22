package repositories;

import entidades.Categoria;

import java.util.ArrayList;
import java.util.List;

public class JPACategoriaRepositorioDAO implements ICategoriaRepositorioDAO {

    private final ManipulaArquivo manipulaArquivo;

    public JPACategoriaRepositorioDAO() {

        manipulaArquivo = new ManipulaArquivo();
    }

    @Override
    public boolean atualizar(String coluna, String valor, int id) {

        return manipulaArquivo.atualizar(Categoria.class,coluna,valor,id);
    }

    @Override
    public boolean adicionar(Categoria categoria) {

        return manipulaArquivo.escrever(categoria);
    }

    @Override
    public List<Categoria> listarTodos() {

        return manipulaArquivo.carregarTudo(Categoria.class);
    }

    @Override
    public ArrayList<Categoria> buscar(String tipo, String valor){
        if (tipo.equals("id")){
            ArrayList<Categoria> lista = new ArrayList<>();
            lista.add(manipulaArquivo.buscaUnica(Categoria.class,Integer.parseInt(valor)));
            return lista;
        }
        return manipulaArquivo.buscar(Categoria.class,tipo,valor);
    }

    @Override
    public boolean desativarCtegoria(int id){
        return manipulaArquivo.desativar(Categoria.class,id);
    }

}