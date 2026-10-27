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
    
    public abstract List<Categoria> buscarPorId(String id);
    
    public abstract List<Categoria> buscarPorNome(String nome);
    
    public abstract List<Categoria> buscarPorDescricao(String descricao);
    
    public abstract List<Categoria> buscarPorTodos(String texto);
}
