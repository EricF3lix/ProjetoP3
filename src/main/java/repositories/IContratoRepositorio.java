package repositories;

import entidades.ContratoAluguel;

import java.util.List;

public interface IContratoRepositorio {

    public abstract void adicionar(ContratoAluguel contrato);

    public abstract List<ContratoAluguel> listarTodos();

    public abstract ContratoAluguel buscarPorId(int id);

    public abstract List<ContratoAluguel> buscarPorCliente(int idCliente);

    public abstract ContratoAluguel buscarContratoAtivoPorItem(int idItem);

    public abstract void remover(ContratoAluguel contrato);
    
    public abstract void salvar();

    public abstract int gerarProximoId();

}