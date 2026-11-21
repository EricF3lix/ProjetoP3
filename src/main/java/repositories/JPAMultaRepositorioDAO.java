package repositories;

import java.util.List;

import entidades.Multa;

public class JPAMultaRepositorioDAO implements IMultaRepositorioDAO {

    private final ManipulaArquivo manipulaArquivo;

    public JPAMultaRepositorioDAO() {
        this(new ManipulaArquivo());
    }

    public JPAMultaRepositorioDAO(ManipulaArquivo manipulaArquivo) {
        this.manipulaArquivo = manipulaArquivo;
    }

    @Override
    public void adicionar(Multa multa) {
        manipulaArquivo.escrever(multa);
    }

    @Override
    public List<Multa> listarTodos() {
        return manipulaArquivo.carregarTudo(Multa.class);
    }

    @Override
    public Multa buscarPorId(int id) {
        return manipulaArquivo.buscar(Multa.class, "id", id).stream().findFirst().orElse(null);
    }

    @Override
    public List<Multa> buscarPorContrato(int idContrato) {
        return manipulaArquivo.buscar(Multa.class, "idContrato", idContrato);
    }

    @Override
    public List<Multa> buscarPendentes() {
        return manipulaArquivo.buscar(Multa.class, "paga", false);
    }

    @Override
    public void atualizar(String coluna, Object valor, int id) {
        manipulaArquivo.atualizar(Multa.class, coluna, valor, id);
    }

}