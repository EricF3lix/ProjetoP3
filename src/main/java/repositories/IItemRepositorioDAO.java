package repositories;

import java.util.List;

import entidades.Item;

public interface IItemRepositorioDAO {

    public abstract void adicionar(Item item);

    public abstract List<Item> listarTodos();

    public abstract List<Item> buscarItens(String tipofiltro, String filtro);

    public abstract boolean atualizar(String coluna, String valor, int id);
}