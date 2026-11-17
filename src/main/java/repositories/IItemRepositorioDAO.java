package repositories;

import java.util.List;

import entidades.Item;

public interface IItemRepositorioDAO {

    public abstract void adicionar(Item item);

    public abstract List<Item> listarTodos();

    public abstract Item buscarPorId(int id);
    
}