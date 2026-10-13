package apresentacao.fx;

import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MenuRelatoriosController extends MenuController {

    @FXML private TableView<ObservableList<String>> tabela;

    @Override
    protected String getTitulo() {
        return "Menu Relatorios";
    }

    private void montarTabela(String[] cabecalhos, List<List<String>> linhas) {
        tabela.getColumns().clear();
        tabela.getItems().clear();

        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        for (int i = 0; i < cabecalhos.length; i++) {
            final int indice = i;
            TableColumn<ObservableList<String>, String> coluna = new TableColumn<>(cabecalhos[i]);
            coluna.setCellValueFactory(c ->
                    new SimpleStringProperty(c.getValue().get(indice)));

            coluna.setMinWidth(90);
            tabela.getColumns().add(coluna);
        }
        for (List<String> linha : linhas) {
            tabela.getItems().add(FXCollections.observableArrayList(linha));
        }
    }

    @FXML
    private void itensPorCategoria() {
        List<Item> itens = sistema.gerarRelatorioItensDisponiveis();
        if (itens.isEmpty()) {
            erro("Erro ao encontrar relatorios");
            return;
        }

        List<List<String>> linhas = new ArrayList<>();
        for (Item i : itens) {
            linhas.add(Arrays.asList(
                    i.getCategoria().getNome(),
                    String.valueOf(i.getId()),
                    i.getNome(),
                    String.format("%.2f", i.getTaxaDiaria())
            ));
        }

        montarTabela(new String[]{"CATEGORIA", "ID", "NOME", "TAXA"}, linhas);
    }

    @FXML
    private void historicoAlugueisCliente() {
        Optional<Integer> id = pedirInteiro("ID do cliente:");

        if (id.isEmpty()){
            erro("O ID não pode ser vazio");
            return;
        }

        Usuario usuario = sistema.buscarUsuario(id.get());

        if (usuario == null) {
            erro("Usuario não encontrado");
            return;
        }

        List<ContratoAluguel> historico = sistema.gerarHistoricoCliente(id.get());

        if (historico.isEmpty()) {
            escrever("Cliente sem historico");
            return;

        }

        List<List<String>> linhas = new ArrayList<>();
        for (ContratoAluguel c : historico) {
            linhas.add(Arrays.asList(
                    String.valueOf(c.getId()),
                    c.getItem().getNome(),
                    String.valueOf(c.getDataRetirada()),
                    String.valueOf(c.getDataDevolucaoPrevista()),
                    String.valueOf(c.getStatus()),
                    String.format("%.2f", c.getValorTotal()),
                    String.format("%.2f", c.getValorMulta())
            ));
        }

        montarTabela(new String[]{"ID", "ITEM", "RETIRADA", "PREVISTA", "STATUS", "VALOR", "MULTA"}, linhas);

    }


    @FXML
    private void alugados() {

        List<ContratoAluguel> contratosAtivos = sistema.gerarRelatorioItensAlugados();

        if (contratosAtivos.isEmpty()) {
            escrever("Não existem contratos ativos.");
        }

        LocalDate hoje = LocalDate.now(ZoneId.of("America/Recife"));
        List<List<String>> linhas = new ArrayList<>();

        for (ContratoAluguel c : contratosAtivos) {

            LocalDate dataPrevista = LocalDate.parse(c.getDataDevolucaoPrevista());
            String situacao = hoje.isAfter(dataPrevista) ? "ATRASADO" : "EM DIA";

            linhas.add(Arrays.asList(
                    String.valueOf(c.getId()),
                    c.getCliente().getNome(),
                    c.getItem().getNome(),
                    c.getDataDevolucaoPrevista(),
                    situacao
            ));
        }

        montarTabela(new String[]{"ID", "CLIENTE", "ITEM", "DEVOLUÇÃO", "SITUAÇÃO"}, linhas);

    }

    @FXML
    private void relatorioPeriodo(){
        Optional<String> dataInicial= pedirData("Data inicial");
        Optional<String> dataFinal = pedirData("Data final");

        List<ContratoAluguel> contratosPeriodo = sistema.gerarRelatorioFaturamento(dataInicial.get(), dataFinal.get());
        if (!contratosPeriodo.isEmpty()) {

            List<List<String>> linhas = new ArrayList<>();

            for (ContratoAluguel c : contratosPeriodo) {

                linhas.add(Arrays.asList(
                        String.valueOf(c.getId()),
                        c.getCliente().getNome(),
                        String.format("%.2f", c.getValorTotal()),
                        String.format("%.2f", c.getValorMulta()),
                        c.isAtivo() ? "Sim" : "Não"
                ));

            }

            montarTabela(new String[]{"ID", "CLIENTE", "ALUGUEL", "MULTA", "ATIVO"}, linhas);
        }
        else {
            erro( "Nenhum contrato encontrado no período.");
        }

    }
}

