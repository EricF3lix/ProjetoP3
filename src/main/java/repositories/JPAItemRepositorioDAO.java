package repositories;

import java.util.ArrayList;
import java.util.List;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;

public class JPAItemRepositorioDAO implements IItemRepositorioDAO {

    private final ManipulaArquivo manipulaArquivo;

    public JPAItemRepositorioDAO() {
        manipulaArquivo = new ManipulaArquivo();
    }

    @Override
    public void adicionar(Item item) {

        manipulaArquivo.escrever(item);
    }

    @Override
    public List<Item> listarTodos() {

        return manipulaArquivo.carregarTudo(Item.class);
    }

    @Override
    public List<Item> buscarItens(String tipofiltro, String filtro){
        return manipulaArquivo.buscar(Item.class,tipofiltro,filtro);
    }

    @Override
    public boolean atualizar(String coluna, String valor, int id) {

        manipulaArquivo.atualizar(Item.class,coluna,valor,id);
        return true;
    }

}