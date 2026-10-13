package apresentacao.fx;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import entidades.Multa;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MenuMultasController extends MenuController {

    private static final String ID = "Digite o ID da multa: ";
    private static final String ID_CONTRATO = "Digite o ID do contrato: ";
    private static final String NAO_ENCONTRADA = "Multa não encontrada.";
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML private TableView<Multa> tabela;
    @FXML private TableColumn<Multa, Integer> colID;
    @FXML private TableColumn<Multa, Integer> colContrato;
    @FXML private TableColumn<Multa, String> colTipo;
    @FXML private TableColumn<Multa, String> colDescricao;
    @FXML private TableColumn<Multa, Double> colValor;
    @FXML private TableColumn<Multa, Boolean> colStatus;
    @FXML private TableColumn<Multa, String> colData;

    private boolean listener = false;

    @Override
    protected String getTitulo() {
        return "Gerenciar Multas";
    }

    @Override
    protected void aoExibir() {
        tabela.setPlaceholder(new Label(""));

        if (!listener) {
            configurarColunas();
            pesquisa.textProperty().addListener((obj, antigo, novo) -> fazPesquisa(novo));
            this.listener = true;
        }
    }

    private void configurarColunas() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colContrato.setCellValueFactory(new PropertyValueFactory<>("idContrato"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("paga"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));


        colValor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double valor, boolean empty) {
                super.updateItem(valor, empty);
                setText(empty || valor == null ? null : MOEDA.format(valor));
            }
        });


        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean paga, boolean empty) {
                super.updateItem(paga, empty);
                if (empty || paga == null) {
                    setText(null);
                } else if (paga.booleanValue()) {
                    setText("Paga");
                } else {
                    setText("Pendente");
                }
            }
        });
    }

    @FXML
    private void cadastrarMulta() {
        Optional<Integer> idContrato = pedirInteiro(ID_CONTRATO);
        if (idContrato.isEmpty()) {return;}

        Optional<String> descricao = pedirTexto("Descrição: ");
        if (descricao.isEmpty()) {return;}

        Optional<String> tipo = pedirTexto("Tipo: ");
        if (tipo.isEmpty()) {return;}

        Optional<Double> valor = pedirDouble("Valor: ");
        if (valor.isEmpty()) {return;}

        String data = LocalDate.now(ZoneId.of("America/Recife")).toString();

        int id = sistema.gerarProximoIdCategoria();
        Multa multa = new Multa(id, idContrato.get(), tipo.get(), descricao.get(), valor.get(), data);

        if (sistema.registrarMulta(multa)) {
            info("Multa cadastrada com sucesso! ID: " + id);
            mostraMulta(id);
        } else {
            erro("Erro ao cadastrar multa.");
        }
    }

    @FXML
    private void buscarMultas() {
        Optional<Integer> id = pedirInteiro(ID);
        if (id.isEmpty()) {return;}

        mostraMulta(id.get());
    }

    @FXML
    private void listarMultas() {
        List<Multa> multas = sistema.listarMultas();

        if (multas.isEmpty()) {
            tabela.getItems().clear();
            return;
        }

        tabela.getItems().setAll(multas);
    }

    @FXML
    private void buscarContrato() {
        Optional<Integer> id = pedirInteiro(ID_CONTRATO);
        if (id.isEmpty()) {return;}

        List<Multa> multas = sistema.listarMultasPorContrato(id.get());

        if (multas.isEmpty()) {
            tabela.getItems().clear();
            info("Nenhuma multa encontrada para esse contrato.");
            return;
        }

        tabela.getItems().setAll(multas);
    }

    @FXML
    private void multasPedentens() {
        List<Multa> multas = sistema.listarMultasPendentes();

        if (multas.isEmpty()) {
            tabela.getItems().clear();
            info("Nenhuma multa pendente.");
            return;
        }

        tabela.getItems().setAll(multas);
    }

    @FXML
    private void quitarMulta() {
        Multa multa = obterMultaAlvo();
        if (multa == null) return;

        if (multa.isPaga()) {
            info("Essa multa já está paga.");
            return;
        }

        if (!confirmar("Tem certeza que deseja quitar a multa " + multa.getId() + "?")) {
            return;
        }

        if (sistema.quitarMulta(multa.getId())) {
            info("Multa quitada com sucesso.");
            mostraMulta(multa.getId());
        } else {
            erro("Erro ao quitar multa.");
        }
    }

    private void fazPesquisa(String pesquisado) {
        String termo = pesquisado == null ? "" : pesquisado.trim();

        List<Multa> filtradas = sistema.listarMultas().stream()
                .filter(m -> termo.isEmpty()
                        || String.valueOf(m.getId()).contains(termo)
                        || String.valueOf(m.getIdContrato()).contains(termo))
                .toList();

        tabela.getItems().setAll(filtradas);
    }

    private Multa obterMultaAlvo() {
        Multa atual = tabela.getSelectionModel().getSelectedItem();
        tabela.getSelectionModel().clearSelection();

        if (atual == null) {
            Optional<Integer> id = pedirInteiro(ID);
            if (id.isEmpty()) return null;
            atual = sistema.buscarMulta(id.get());
            if (atual == null) {
                erro(NAO_ENCONTRADA);
            }
        }
        return atual;
    }

    private void mostraMulta(int id) {
        Multa multa = sistema.buscarMulta(id);
        if (multa == null) {
            info("Multa não cadastrada.");
            return;
        }

        tabela.getItems().setAll(multa);
    }
}