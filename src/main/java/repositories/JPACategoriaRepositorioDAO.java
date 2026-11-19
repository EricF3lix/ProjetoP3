package repositories;

import entidades.Categoria;

import java.util.ArrayList;
import java.util.List;

import entidades.Categoria;

public class JPACategoriaRepositorioDAO implements ICategoriaRepositorioDAO {

    private final ManipulaArquivo manipulaArquivo;

    public JPACategoriaRepositorioDAO() {

        manipulaArquivo = new ManipulaArquivo();
    }

    @Override
    public void atualizar(String coluna, String valor, int id) {

        manipulaArquivo.atualizar(Categoria.class,coluna,valor,id);
    }

    @Override
    public void adicionar(Categoria categoria) {

        manipulaArquivo.escrever(categoria);
    }

    @Override
    public List<Categoria> listarTodos() {

        return manipulaArquivo.carregarTudo(Categoria.class);
    }

    @Override
    public ArrayList<Categoria> buscar(String tipo, String valor){
        return manipulaArquivo.buscar(Categoria.class,tipo,valor);
    }

    @Override
    public void desativarCtegoria(int id){
        manipulaArquivo.desativar(Categoria.class,id);
    }

}