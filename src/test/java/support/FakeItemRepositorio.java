package support;

import entidades.Item;
import repositories.IItemRepositorioDAO;

import java.util.ArrayList;
import java.util.List;


public class FakeItemRepositorio implements IItemRepositorioDAO {

    @Override
    public void adicionar(Item item) {

    }

    @Override
    public List<Item> listarTodos() {
        return List.of();
    }

    @Override
    public List<Item> buscarItens(String tipofiltro, String filtro) {
        return List.of();
    }

    @Override
    public boolean atualizar(String coluna, String valor, int id) {
        return false;
    }

    @Override
    public void desativarItem(int id) {

    }
}
