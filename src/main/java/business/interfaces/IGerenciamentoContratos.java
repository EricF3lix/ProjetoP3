package business.interfaces;
import java.util.List;

import entidades.ContratoAluguel;

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
    
    public List<ContratoAluguel> filtrarPorId(String pesquisado);
    
    public List<ContratoAluguel> filtrarPorItem(String pesquisado);
    
    public List<ContratoAluguel> filtrarPorDataRetirada(String pesquisado);
    
    public List<ContratoAluguel> filtrarPorDataDevolucao(String pesquisado);
    
    public List<ContratoAluguel> filtrarPorStatus(String pesquisado);
    
    public List<ContratoAluguel> filtrarPorValorTotal(String pesquisado);
    
    public List<ContratoAluguel> filtrarPorValorMulta(String pesquisado);

}