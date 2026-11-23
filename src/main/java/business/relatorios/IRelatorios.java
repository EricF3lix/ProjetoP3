package business.relatorios;

import entidades.ContratoAluguel;
import entidades.Item;

import java.util.List;

public interface IRelatorios {

    List<Item> gerarRelatorioItensDisponiveis();

    List<ContratoAluguel> gerarHistoricoCliente(int idCliente);

    List<ContratoAluguel> gerarRelatorioItensAlugados();

    List<ContratoAluguel> gerarRelatorioFaturamento(String dataInicial, String dataFinal);

}