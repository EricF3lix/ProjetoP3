package business.interfaces;

import entidades.Item;

import java.util.List;

public interface IGerenciamentoItens {

    public abstract boolean cadastrarItem(Item item);

    public abstract Item buscarItem(int id);

    public abstract List<Item> listarItens();

    public abstract List<Item> filtrarItem(String filtro);

    public abstract boolean atualizarItem(Item itemNovo);

    public abstract boolean excluirItem(int id);

    public abstract int gerarProximoId();
    
    public abstract List<Item> filtrarPorId(String pesquisado);
    
    public abstract List<Item> filtrarPorNome(String pesquisado);
    
    public abstract List<Item> filtrarPorCategoria(String pesquisado);
    
    public abstract List<Item> filtrarPorTaxa(String pesquisado);
}