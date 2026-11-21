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
    public ContratoAluguel buscarPorId(int id) {
        return manipulaArquivo.buscar(ContratoAluguel.class, "id", id).stream().filter(contrato -> contrato.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<ContratoAluguel> buscarPorCliente(int idCliente) {
        return manipulaArquivo.buscar(ContratoAluguel.class, "cliente.id", idCliente).stream().filter(contrato -> contrato.getCliente().getId() == idCliente).toList();
    }

    @Override
    public ContratoAluguel buscarContratoAtivoPorItem(int idItem) {
        return manipulaArquivo.buscar(ContratoAluguel.class, "item.id", idItem).stream()
                .filter(contrato -> contrato.getItem().getId() == idItem && contrato.estaAtivo())
                .findFirst()
                .orElse(null);
    }

    @Override
    public void atualizar(String coluna, Object valor, int id) {
        manipulaArquivo.atualizar(ContratoAluguel.class, coluna, valor, id);
    }

}