package repositories;

import entidades.Categoria;

import java.util.ArrayList;
import java.util.List;

public interface ICategoriaRepositorioDAO{

    public abstract void adicionar(Categoria categoria);

    public abstract List<Categoria> listarTodos();

    public ArrayList<Categoria> buscar(String tipo, String valor);

    public abstract void atualizar(String coluna, String valor, int id);

    public abstract void desativarCtegoria(int id);

}