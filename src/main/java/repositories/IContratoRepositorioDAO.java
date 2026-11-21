package repositories;

import java.util.List;

import entidades.ContratoAluguel;

public interface IContratoRepositorioDAO {

    public abstract void adicionar(ContratoAluguel contrato);

    public abstract List<ContratoAluguel> listarTodos();

    public abstract List<ContratoAluguel> buscarContratos(String tipofiltro, String filtro);

    public abstract ContratoAluguel buscarPorId(int id);

    public abstract List<ContratoAluguel> buscarPorCliente(int idCliente);

    public abstract ContratoAluguel buscarContratoAtivoPorItem(int idItem);

    public abstract void atualizar(String coluna, Object valor, int id);
}