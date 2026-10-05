package business.relatorios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import business.interfaces.IGerenciamentoContratos;
import business.interfaces.IGerenciamentoItens;
import business.interfaces.IGerenciamentoUsuarios;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import repositories.SalvaRelatorioFaturamento;
import repositories.SalvaRelatorioHistoricoCliente;
import repositories.SalvaRelatorioItensAlugados;

@SuppressWarnings("java:S106")
public class GeradorRelatorios implements IRelatorios {

    private IGerenciamentoItens gerenciamentoItens;
    private IGerenciamentoContratos gerenciamentoContratos;
    private IGerenciamentoUsuarios gerenciamentoUsuarios;

    public GeradorRelatorios(IGerenciamentoItens gerenciamentoItens, IGerenciamentoContratos gerenciamentoContratos, IGerenciamentoUsuarios gerenciamentoUsuarios) {
        this.gerenciamentoItens = gerenciamentoItens;
        this.gerenciamentoContratos = gerenciamentoContratos;
        this.gerenciamentoUsuarios = gerenciamentoUsuarios;

    }

    @Override
    public List<Item> gerarRelatorioItensDisponiveis() {

        List<Item> itensDisponiveis = gerenciamentoItens.listarItens().stream()
                .filter(Item::estaDisponivel)
                .toList();

        return itensDisponiveis;
    }

    @Override
    public List<ContratoAluguel> gerarHistoricoCliente(int idCliente) {
        List<ContratoAluguel> resposta = new ArrayList<>();
        Usuario usuario = gerenciamentoUsuarios.buscarUsuario(idCliente);

        if (usuario != null) {

            List<ContratoAluguel> historico = gerenciamentoContratos.listarContratos().stream()
                    .filter(contrato -> contrato.getCliente().getId() == idCliente)
                    .toList();
            resposta = historico;

            SalvaRelatorioHistoricoCliente salva = new SalvaRelatorioHistoricoCliente();
            salva.salvar(historico);

        }
        return resposta;
    }

    @Override
    public List<ContratoAluguel> gerarRelatorioItensAlugados() {

        List<ContratoAluguel> contratosAtivos = gerenciamentoContratos.listarContratos().stream()
                .filter(ContratoAluguel::estaAtivo)
                .toList();

            SalvaRelatorioItensAlugados salva = new SalvaRelatorioItensAlugados();

            salva.salvar(contratosAtivos);

        return contratosAtivos;

    }

    @Override
    public void gerarRelatorioFaturamento(String dataInicial, String dataFinal) {
        LocalDate inicio;
        LocalDate fim;

        try {

            inicio = LocalDate.parse(dataInicial);
            fim = LocalDate.parse(dataFinal);

        } catch (Exception e) {

            System.out.println(
                    "Formato de data inválido.");

            return; //perguntar a jackson se pode usar isso

        }

        List<ContratoAluguel> contratosPeriodo = new ArrayList<>();

        double totalAlugueis = 0;
        double totalMultas = 0;

        for (ContratoAluguel contrato : gerenciamentoContratos.listarContratos()) {

            LocalDate dataContrato = LocalDate.parse(contrato.getDataRetirada());

            if (!dataContrato.isBefore(inicio) && !dataContrato.isAfter(fim)) {

                contratosPeriodo.add(contrato);

                totalAlugueis += contrato.getValorTotal();

                totalMultas += contrato.getValorMulta();

            }

        }

        if (contratosPeriodo.isEmpty()) {
            System.out.println( "Nenhum contrato encontrado no período.");

        } else{

            System.out.println("\n===== RELATÓRIO DE FATURAMENTO =====");

            System.out.println("Período: " + dataInicial + " até "  + dataFinal);

            System.out.println();

            System.out.printf( "%-5s %-20s %-12s %-12s%n","ID", "CLIENTE", "ALUGUEL",  "MULTA");

            for (ContratoAluguel contrato : contratosPeriodo) {

                System.out.printf("%-5d %-20s %-12.2f %-12.2f%n", contrato.getId(), contrato.getCliente().getNome(), contrato.getValorTotal(), contrato.getValorMulta());

            }

            System.out.println();

            System.out.println("Quantidade de contratos: " + contratosPeriodo.size());

            System.out.printf("Total de aluguéis: R$ %.2f%n", totalAlugueis);

            System.out.printf("Total de multas: R$ %.2f%n",totalMultas);

            System.out.printf("Total geral: R$ %.2f%n", totalAlugueis + totalMultas);

            SalvaRelatorioFaturamento salva = new SalvaRelatorioFaturamento();

            salva.salvar(contratosPeriodo, dataInicial, dataFinal);
        }

    }

}