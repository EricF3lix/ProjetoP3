package business.cruds;

import business.interfaces.IGerenciamentoCategoria;
import entidades.Categoria;
import repositories.ICategoriaRepositorio;

import java.util.ArrayList;
import java.util.List;

public class GerenciamentoCategoria implements IGerenciamentoCategoria {

    private final ICategoriaRepositorio repositorio;

    public GerenciamentoCategoria(ICategoriaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean cadastrarCategoria(Categoria categoria) {

        if (repositorio.buscarPorId(categoria.getId()) != null) {
            return false;
        }

        if (repositorio.buscarPorNome(categoria.getNome()) != null) {
            return false;
        }

        repositorio.adicionar(categoria);

        return true;
    }

    @Override
    public Categoria buscarCategoria(int id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Categoria> listarCategorias() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean atualizarCategoria(Categoria categoriaNova) {

        List<Categoria> categorias = repositorio.listarTodos();

        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == categoriaNova.getId()) {
                categorias.set(i, categoriaNova);
                repositorio.salvar();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean desativarCategoria(int id) {

        Categoria categoria = repositorio.buscarPorId(id);

        if (categoria == null) {
            return false;
        }

        categoria.desativar();

        repositorio.salvar();

        return true;
    }

    @Override
    public int gerarProximoId() {
        return repositorio.gerarProximoId();
    }

    @Override
    public List<Categoria> buscarPorId(String id) {
        List<Categoria> resultado = new ArrayList<>();
        for (Categoria c : repositorio.listarTodos()) {
            if (String.valueOf(c.getId()).contains(id)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    @Override
    public List<Categoria> buscarPorNome(String nome) {
        List<Categoria> resultado = new ArrayList<>();
        for (Categoria c : repositorio.listarTodos()) {
            if (c.getNome() != null && c.getNome().toLowerCase().contains(nome.toLowerCase())) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    @Override
    public List<Categoria> buscarPorDescricao(String descricao) {
        List<Categoria> resultado = new ArrayList<>();
        for (Categoria c : repositorio.listarTodos()) {
            if (c.getDescricao() != null && c.getDescricao().toLowerCase().contains(descricao.toLowerCase())) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    @Override
    public List<Categoria> buscarPorTodos(String texto) {
        List<Categoria> resultado = new ArrayList<>();
        String t = texto == null ? "" : texto.toLowerCase();
        for (Categoria c : repositorio.listarTodos()) {
            if (String.valueOf(c.getId()).contains(t)
                    || (c.getNome() != null && c.getNome().toLowerCase().contains(t))
                    || (c.getDescricao() != null && c.getDescricao().toLowerCase().contains(t))) {
                resultado.add(c);
            }
        }
        return resultado;
    }
}