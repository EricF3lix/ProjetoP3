package repositories;

import java.util.List;

import entidades.ContratoAluguel;

public class JPAContratoRepositorioDAO implements IContratoRepositorioDAO {

    private final ManipulaArquivo manipulaArquivo;

    public JPAContratoRepositorioDAO() {
        this(new ManipulaArquivo());
    }

    public JPAContratoRepositorioDAO(ManipulaArquivo manipulaArquivo) {
        this.manipulaArquivo = manipulaArquivo;
    }

    @Override
    public void adicionar(ContratoAluguel contrato) {
        manipulaArquivo.escrever(contrato);
    }

    @Override
    public List<ContratoAluguel> listarTodos() {
        return manipulaArquivo.carregarTudo(ContratoAluguel.class);
    }

    @Override
    public List<ContratoAluguel> buscarContratos(String tipofiltro, String filtro) {
        return manipulaArquivo.buscar(ContratoAluguel.class, tipofiltro, filtro);
    }

    @Override
    public ContratoAluguel buscarPorId(int id) {
        return manipulaArquivo.buscaUnica(ContratoAluguel.class, id);
    }

    @Override
    public List<ContratoAluguel> buscarPorCliente(int idCliente) {
        return manipulaArquivo.buscaExata(ContratoAluguel.class, "cliente.id", idCliente);
    }

    @Override
    public ContratoAluguel buscarContratoAtivoPorItem(int idItem) {
        return manipulaArquivo.buscaExata(ContratoAluguel.class, "item.id", idItem).stream()
                .filter(ContratoAluguel::estaAtivo)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void atualizar(String coluna, Object valor, int id) {
        manipulaArquivo.atualizar(ContratoAluguel.class, coluna, valor, id);
    }

}