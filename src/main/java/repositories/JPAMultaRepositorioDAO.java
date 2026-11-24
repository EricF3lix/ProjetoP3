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
    public List<Multa> buscarMultas(String tipofiltro, String filtro) {
        return manipulaArquivo.buscar(Multa.class, tipofiltro, filtro);
    }

    @Override
    public Multa buscarPorId(int id) {
        return manipulaArquivo.buscaUnica(Multa.class, id);
    }

    @Override
    public List<Multa> buscarPorContrato(int idContrato) {
        return manipulaArquivo.buscaExata(Multa.class, "idContrato", idContrato);
    }

    @Override
    public List<Multa> buscarPendentes() {
        return manipulaArquivo.buscaExata(Multa.class, "paga", false);
    }

    @Override
    public void atualizar(String coluna, Object valor, int id) {
        manipulaArquivo.atualizar(Multa.class, coluna, valor, id);
    }

}