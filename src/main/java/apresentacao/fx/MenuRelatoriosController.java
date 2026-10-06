package apresentacao.fx;

import apresentacao.ValidaEntrada;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import javafx.fxml.FXML;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public class MenuRelatoriosController extends MenuController {

    @Override
    protected String getTitulo() {
        return "Menu Relatorios";
    }

    @FXML
    private void itensPorCategoria() {
        List<Item> itens = sistema.gerarRelatorioItensDisponiveis();
        if (!itens.isEmpty()) {
            limpar();
            escreverf("%-20s %-5s %-25s %-10s%n", "CATEGORIA", "ID", "NOME", "TAXA");

            for (Item i : itens) {
                escreverf("%-20s %-5s %-25s %-10.2f", (i.getCategoria()).getNome(), i.getId(), i.getNome(), i.getTaxaDiaria());
            }
        } else {
            erro("Erro ao encontrar relatorios");
        }
    }

    @FXML
    private void historicoAlugueisCliente() {
        Optional<Integer> id = pedirInteiro("ID do cliente:");

        if (!id.isEmpty()) {
            Usuario usuario = sistema.buscarUsuario(id.get());

            if (usuario != null) {
                List<ContratoAluguel> historico = sistema.gerarHistoricoCliente(id.get());

                limpar();
                if (historico.isEmpty()) {
                    escrever("Cliente sem historico");
                } else {
                    escrever("Cliente: " + usuario.getNome());
                    escreverf("%-5s %-20s %-12s %-12s %-12s %-10s %-10s%n", "ID", "ITEM", "RETIRADA", "PREVISTA", "STATUS", "VALOR", "MULTA");

                    for (ContratoAluguel contrato : historico) {

                        escreverf("%-5d %-20s %-12s %-12s %-12s %-10.2f %-10.2f%n", contrato.getId(), contrato.getItem().getNome(), contrato.getDataRetirada(),
                                contrato.getDataDevolucaoPrevista(), contrato.getStatus(), contrato.getValorTotal(), contrato.getValorMulta());
                    }

                    escrever("\nArquivo CSV gerado na pasta relatorios");
                }

            } else {
                erro("Usuario não encontrado");
            }
        } else {
            erro("O ID não pode ser vazio!");
        }
    }

    @FXML
    private void alugados() {

        List<ContratoAluguel> contratosAtivos = sistema.gerarRelatorioItensAlugados();

        limpar();
        if (contratosAtivos.isEmpty()) {

            escrever("Não existem contratos ativos.");
        } else {

            escrever("\n===== ITENS ALUGADOS ATUALMENTE =====");

            escreverf("%-5s %-20s %-20s %-15s %-10s%n", "ID", "CLIENTE", "ITEM", "DEVOLUÇÃO", "SITUAÇÃO");

            LocalDate hoje = LocalDate.now(ZoneId.of("America/Recife"));

            for (ContratoAluguel contrato : contratosAtivos) {

                LocalDate dataPrevista = LocalDate.parse(contrato.getDataDevolucaoPrevista());

                String situacao;

                if (hoje.isAfter(dataPrevista)) {

                    situacao = "ATRASADO";

                } else {

                    situacao = "EM DIA";

                }

                escreverf("%-5d %-20s %-20s %-15s %-10s%n", contrato.getId(), contrato.getCliente().getNome(), contrato.getItem().getNome(), contrato.getDataDevolucaoPrevista(), situacao);
                escrever("\nArquivo CSV gerado na pasta relatorios");
            }
        }
    }

    @FXML
    private void relatorioPeriodo(){
        Optional<String> dataInicial= pedirData("Data inicial");
        if (!dataInicial.isEmpty()) {
            Optional<String> dataFinal = pedirData("Data final");
            if (!dataFinal.isEmpty()) {

                List<ContratoAluguel> contratosPeriodo = sistema.gerarRelatorioFaturamento(dataInicial.get(), dataFinal.get());
                if (contratosPeriodo.isEmpty()) {
                    escrever( "Nenhum contrato encontrado no período.");

                }
                else {

                    escrever("\n===== RELATÓRIO DE FATURAMENTO =====");

                    escrever("Período: " + dataInicial.get()+ " até " + dataFinal.get());

                    escrever("");

                    escreverf("%-5s %-20s %-12s %-12s%n", "ID", "CLIENTE", "ALUGUEL", "MULTA");

                    for (ContratoAluguel contrato : contratosPeriodo) {

                        escreverf("%-5d %-20s %-12.2f %-12.2f%n", contrato.getId(), contrato.getCliente().getNome(), contrato.getValorTotal(), contrato.getValorMulta());

                    }
                }

            }
            else {erro("A data nao pode ser vazia");}


        }
        else {erro("A data nao pode ser vazia");}
    }
}

