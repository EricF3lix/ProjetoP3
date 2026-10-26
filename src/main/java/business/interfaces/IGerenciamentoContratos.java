package business.interfaces;

import entidades.ContratoAluguel;

import java.util.List;

public interface IGerenciamentoContratos {

    public abstract boolean cadastrarContrato(ContratoAluguel contrato);

    public abstract ContratoAluguel buscarContrato(int id);

    public abstract List<ContratoAluguel> listarContratos();

    public abstract List<ContratoAluguel> filtrarContratos(String filtro);

    public abstract boolean finalizarContrato(int id);

    public abstract boolean cancelarContrato(int id);

    public abstract boolean quitarMultaContrato(int idContrato);

    public abstract int gerarProximoId();

    public boolean clientePossuiMultaPendente(int idCliente);

    public boolean clientePossuiHistorico(int idCliente);

    public double calcularMulta(long diasAtraso, double taxaDiaria);
    
    public abstract List<ContratoAluguel> filtrarPorId(String pesquisado);
    
    public abstract List<ContratoAluguel> filtrarPorItem(String pesquisado);
    
    public abstract List<ContratoAluguel> filtrarPorDataRetirada(String pesquisado);
    
    public abstract List<ContratoAluguel> filtrarPorDataDevolucao(String pesquisado);
    
    public abstract List<ContratoAluguel> filtrarPorStatus(String pesquisado);
    
    public abstract List<ContratoAluguel> filtrarPorValorTotal(String pesquisado);
    
    public abstract List<ContratoAluguel> filtrarPorValorMulta(String pesquisado);

}