package repositories;

import java.util.ArrayList;
import java.util.List;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;

public class JPAItemRepositorioDAO implements IItemRepositorioDAO {

    private static final String ARQUIVO = "dados/itens.csv";

    private final ICategoriaRepositorioDAO categoriaRepositorio;
    private final IFornecedorRepositorioDAO fornecedorRepositorio;
    private final ManipulaArquivo manipulaArquivo;

    public JPAItemRepositorioDAO(ICategoriaRepositorioDAO categoriaRepositorio, IFornecedorRepositorioDAO fornecedorRepositorio) {

        this.categoriaRepositorio = categoriaRepositorio;
        this.fornecedorRepositorio = fornecedorRepositorio;

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
        ArrayList<Item> retorno = manipulaArquivo.busca(Item.class, "id", id);

        if (retorno.isEmpty()){
            return null;
        }
        return retorno.getFirst();
    }
    

}