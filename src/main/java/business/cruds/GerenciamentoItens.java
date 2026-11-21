package business.cruds;

import business.interfaces.IGerenciamentoItens;
import entidades.Item;
import repositories.IItemRepositorioDAO;

import java.util.List;

public class GerenciamentoItens implements IGerenciamentoItens {

    private final IItemRepositorioDAO repositorio;

    public GerenciamentoItens(IItemRepositorioDAO repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean cadastrarItem(Item item) {

        return repositorio.adicionar(item);

    }

    @Override
    public Item buscarItem(int id) {
        return repositorio.buscarItens("id",String.valueOf(id)).getFirst();
    }

    @Override
    public List<Item> listarItens() {
        return repositorio.listarTodos();
    }

    @Override 
    public List<Item> filtrarItem(String tipofiltro, String filtro) {
        return repositorio.buscarItens(tipofiltro,filtro);
    }

    @Override
    public boolean atualizarItem(String coluna,String valor,int id) {

        return repositorio.atualizar(coluna,valor,id);
    }
    
    @Override
    public Item filtrarPorId(String pesquisado) {
        return repositorio.buscarItens("id",pesquisado).getFirst();
    }

    @Override
    public List<Item> filtrarPorNome(String pesquisado) {
        return repositorio.buscarItens("nome",pesquisado);
    }

    @Override
    public List<Item> filtrarPorCategoria(String pesquisado) {
        return repositorio.buscarItens("categoria",pesquisado);
    }

    @Override
    public List<Item> filtrarPorTaxa(String pesquisado) {
        return repositorio.buscarItens("taxa",pesquisado);
    }

    @Override
    public boolean desativarItem(int id){
        return repositorio.desativarItem(id);
    }
}