package repositories;

import entidades.Categoria;

import java.util.ArrayList;
import java.util.List;

import entidades.Categoria;

public class JPACategoriaRepositorioDAO implements ICategoriaRepositorioDAO {

    private static final String ARQUIVO = "dados/categorias.csv";

    private final List<Categoria> categorias;
    private final ManipulaArquivo manipulaArquivo;

    public JPACategoriaRepositorioDAO() {

        manipulaArquivo = new ManipulaArquivo(ARQUIVO);
        categorias = carregar();

    }

    @Override
    public void atualizar(String coluna, String valor, int id) {

        manipulaArquivo.salvar(Categoria.class,coluna,valor,id);

    }

    @Override
    public void adicionar(Categoria categoria) {

        manipulaArquivo.escrever(categoria);

    }

    @Override
    public List<Categoria> listarTodos() {

        return manipulaArquivo.carregarTudo(Categoria.class);
    }

    @Override
    public Categoria buscarPorId(int id) {
        ArrayList<Categoria> retorno = manipulaArquivo.buscar(Categoria.class,"id",id);

        if (retorno.isEmpty()){return null;}

        return retorno.getFirst();
    }

    @Override
    public Categoria buscarPorNome(String nome) {
        ArrayList<Categoria> retorno = manipulaArquivo.buscar(Categoria.class,"nome",nome);

        if (retorno.isEmpty()){return null;}

        return retorno.getFirst();

    }

}