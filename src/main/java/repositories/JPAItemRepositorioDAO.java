package repositories;

import java.util.ArrayList;
import java.util.List;

import entidades.Item;

public class JPAItemRepositorioDAO implements IItemRepositorioDAO {

    private final ManipulaArquivo manipulaArquivo;

    public JPAItemRepositorioDAO() {
        manipulaArquivo = new ManipulaArquivo();
    }

    @Override
    public boolean adicionar(Item item) {

       return manipulaArquivo.escrever(item);
    }

    @Override
    public List<Item> listarTodos() {

        return manipulaArquivo.carregarTudo(Item.class);
    }

    @Override
    public List<Item> buscarItens(String tipofiltro, String filtro){
        if (tipofiltro.equals("id")){
            List<Item> lista = new ArrayList<>();
            lista.add(manipulaArquivo.buscaUnica(Item.class, Integer.parseInt(filtro)));
            return lista;

        }
        return manipulaArquivo.buscar(Item.class,tipofiltro,filtro);
    }

    @Override
    public boolean atualizar(String coluna, String valor, int id) {

        return manipulaArquivo.atualizar(Item.class,coluna,valor,id);

    }

    @Override
    public boolean desativarItem(int id){
        return manipulaArquivo.desativar(Item.class,id);
    }
}