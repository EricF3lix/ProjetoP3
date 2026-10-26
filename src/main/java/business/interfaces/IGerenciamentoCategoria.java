package business.interfaces;

import entidades.Categoria;

import java.util.List;

public interface IGerenciamentoCategoria {
 
    public abstract boolean cadastrarCategoria(Categoria categoria);
 
    public abstract Categoria buscarCategoria(int id);
 
    public abstract List<Categoria> listarCategorias();
 
    public abstract boolean atualizarCategoria(Categoria categoriaNova);
 
    public abstract boolean desativarCategoria(int id);
 
    public abstract int gerarProximoId();
}
