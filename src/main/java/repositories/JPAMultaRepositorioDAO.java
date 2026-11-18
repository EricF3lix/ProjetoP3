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

    /**
     * O buscar() genérico compara pelo COMEÇO do texto (buscar o id 1 também traz 10, 11, 100...),
     * por isso o filtro exato logo depois.
     */
    @Override
    public Multa buscarPorId(int id) {
        return manipulaArquivo.buscar(Multa.class, "id", id).stream().filter(multa -> multa.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Multa> buscarPorContrato(int idContrato) {
        return manipulaArquivo.buscar(Multa.class, "idContrato", idContrato).stream().filter(multa -> multa.getIdContrato() == idContrato).toList();
    }

    @Override
    public List<Multa> buscarPendentes() {
        return manipulaArquivo.carregarTudo(Multa.class).stream().filter(multa -> !multa.isPaga()).toList();
    }

    @Override
    public void atualizar(String coluna, Object valor, int id) {
        manipulaArquivo.atualizar(Multa.class, coluna, valor, id);
    }

}