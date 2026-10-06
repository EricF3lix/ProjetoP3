package business.relatorios;

import entidades.ContratoAluguel;

import java.util.List;

public interface IRelatorios {

    public abstract List gerarRelatorioItensDisponiveis();

    public abstract List<ContratoAluguel> gerarHistoricoCliente(int idCliente);

    public abstract List<ContratoAluguel> gerarRelatorioItensAlugados();

    public abstract List<ContratoAluguel> gerarRelatorioFaturamento(String dataInicial, String dataFinal);

}