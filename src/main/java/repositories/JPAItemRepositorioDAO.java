package repositories;

import java.util.ArrayList;
import java.util.List;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;

public class JPAItemRepositorioDAO implements IItemRepositorioDAO {

    private static final String ARQUIVO = "dados/itens.csv";

    private final ManipulaArquivo manipulaArquivo;

    public JPAItemRepositorioDAO() {
        manipulaArquivo = new ManipulaArquivo(ARQUIVO);

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
    public Item buscarPorId(int id) {
        ArrayList<Item> retorno = manipulaArquivo.buscar(Item.class, "id", id);

        if (retorno.isEmpty()){
            return null;
        }
        return retorno.getFirst();
    }
    

}