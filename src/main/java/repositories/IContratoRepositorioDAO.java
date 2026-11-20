package repositories;

import java.util.List;

import entidades.ContratoAluguel;

public interface IContratoRepositorioDAO {

    public abstract void adicionar(ContratoAluguel contrato);

    public abstract List<ContratoAluguel> listarTodos();

    public abstract ContratoAluguel buscarPorId(int id);

    public abstract List<ContratoAluguel> buscarPorCliente(int idCliente);

    public abstract ContratoAluguel buscarContratoAtivoPorItem(int idItem);

    public abstract void atualizar(ContratoAluguel contrato);

}