package business.cruds;

import java.util.ArrayList;
import java.util.List;

import business.interfaces.IGerenciamentoCategoria;
import entidades.Categoria;
import repositories.ICategoriaRepositorioDAO;

public class GerenciamentoCategoria implements IGerenciamentoCategoria {

    private final ICategoriaRepositorioDAO repositorio;

    public GerenciamentoCategoria(ICategoriaRepositorioDAO repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean cadastrarCategoria(Categoria categoria) {

        return repositorio.adicionar(categoria);
    }

    @Override
    public Categoria buscarCategoria(int id) {
        return repositorio.buscar("id",String.valueOf(id)).getFirst();
    }

    @Override
    public List<Categoria> listarCategorias() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean atualizarCategoria(String coluna, String valor,int id) {

        return repositorio.atualizar(coluna,valor,id);
    }

    @Override
    public boolean desativarCategoria(int id) {

        return repositorio.desativarCtegoria(id);
    }

    @Override
    public List<Categoria> buscarPorId(String id) {
        return repositorio.buscar("id",id);
    }

    @Override
    public List<Categoria> buscarPorNome(String nome) {
        return repositorio.buscar("nome",nome);
    }

    @Override
    public List<Categoria> buscarPorDescricao(String descricao) {
        return repositorio.buscar("descricao",descricao);
    }

    @Override
    public List<Categoria> buscarPorTodos(String texto) {
        return  repositorio.listarTodos();
    }
}