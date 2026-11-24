package repositories;

import entidades.Categoria;

import java.util.List;

public interface ICategoriaRepositorioDAO{

    public abstract boolean adicionar(Categoria categoria);

    public abstract List<Categoria> listarTodos();

    public List<Categoria> buscar(String tipo, String valor);

    public abstract boolean atualizar(String coluna, String valor, int id);

    public abstract boolean desativarCtegoria(int id);

}