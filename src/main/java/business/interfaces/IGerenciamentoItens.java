package business.interfaces;

import entidades.Item;

import java.util.List;

public interface IGerenciamentoItens {

    public abstract boolean cadastrarItem(Item item);

    public abstract Item buscarItem(int id);

    public abstract List<Item> listarItens();
    
    public abstract Item filtrarPorId(String pesquisado);
    
    public abstract List<Item> filtrarPorNome(String pesquisado);
    
    public abstract List<Item> filtrarPorCategoria(String pesquisado);
    
    public abstract List<Item> filtrarPorTaxa(String pesquisado);

    public abstract boolean atualizarItem(String coluna,String valor,int id);

    public abstract List<Item> filtrarItem(String tipofiltro, String filtro);

    public abstract void desativarItem(int id);
}