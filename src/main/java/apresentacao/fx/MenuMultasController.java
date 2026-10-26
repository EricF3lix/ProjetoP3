package apresentacao.fx;

import entidades.Multa;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MenuMultasController extends MenuController {

    private static final String ID = "Digite o ID da multa: ";
    private static final String ID_CONTRATO = "Digite o ID do contrato: ";
    private static final String NAO_ENCONTRADA = "Multa não encontrada.";
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    @FXML private TableView<Multa> tabela;
    @FXML private TableColumn<Multa, Integer> colID;
    @FXML private TableColumn<Multa, Integer> colContrato;
    @FXML private TableColumn<Multa, String> colTipo;
    @FXML private TableColumn<Multa, String> colDescricao;
    @FXML private TableColumn<Multa, Double> colValor;
    @FXML private TableColumn<Multa, Boolean> colStatus;
    @FXML private TableColumn<Multa, String> colData;

    private final ObservableList<Multa> listaMultas = FXCollections.observableArrayList();
    private FilteredList<Multa> listaFiltrada;
    private boolean inicializado = false;

    @Override
    protected String getTitulo() {
        return "Gerenciar Multas";
    }

    @FXML
    public void initialize() {
        configurarResponsividade();
        configurarColunas();
    }

    @Override
    protected void aoExibir() {
        if (!inicializado) {
            configurarFiltroPesquisa();
            this.inicializado = true;
        }
        listarMultas();
    }

    @SuppressWarnings("deprecation")
    private void configurarResponsividade() {
        if (tabela != null) {
            VBox.setVgrow(tabela, Priority.ALWAYS);
            tabela.setMaxWidth(Double.MAX_VALUE);
            tabela.setMaxHeight(Double.MAX_VALUE);
            tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }
    }

    private void configurarColunas() {
    	configurarValueFactory();

        if (colValor != null) {
            colValor.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double valor, boolean empty) {
                    super.updateItem(valor, empty);
                    if (empty || valor == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(MOEDA.format(valor));
                        setAlignment(Pos.CENTER_RIGHT);
                    }
                }
            });
        }

        if (colStatus != null) {
            colStatus.setCellFactory(col -> new TableCell<>() {
                private final Label badge = new Label();

                @Override
                protected void updateItem(Boolean paga, boolean empty) {
                    super.updateItem(paga, empty);
                    
                    if (empty || paga == null) {
                    	setGraphic(null);
                    	setText(null);
                    } 
                    else {
                    	configurarBadge(badge, paga);
                    	
                    	setAlignment(Pos.CENTER);
                    	setGraphic(badge);
                    	setText(null);
                        }       
                    	
                    }
                });
            }
        
    }
        
    private void configurarValueFactory() {
        if (colID != null) {
            colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        }
        if (colContrato != null) {
            colContrato.setCellValueFactory(new PropertyValueFactory<>("idContrato"));
        }
        if (colTipo != null) {
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        }
        if (colDescricao != null) {
            colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        }
        if (colValor != null) {
            colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        }
        if (colStatus != null) {
            colStatus.setCellValueFactory(new PropertyValueFactory<>("paga"));
        }
        if (colData != null) {
            colData.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
        }
    }
    
    private void configurarBadge(Label badge, boolean paga) {
    	
    	if (paga) {
            badge.setText("● Paga");
            badge.setStyle(
                "-fx-background-color: #d4edda; -fx-text-fill: #155724; " +
                "-fx-font-weight: bold; -fx-padding: 3 8 3 8; " +
                "-fx-background-radius: 10; -fx-font-size: 11px;"
            );}
            
            else {
                badge.setText("● Pendente");
                badge.setStyle(
                    "-fx-background-color: #fff3cd; -fx-text-fill: #856404; " +
                    "-fx-font-weight: bold; -fx-padding: 3 8 3 8; " +
                    "-fx-background-radius: 10; -fx-font-size: 11px;"
                );}
    	
    }
    
    private void configurarFiltroPesquisa() {
        listaFiltrada = new FilteredList<>(listaMultas, p -> true);

        if (pesquisa != null) {
            pesquisa.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro(novo));
        }

        SortedList<Multa> listaOrdenada = new SortedList<>(listaFiltrada);
        if (tabela != null) {
            listaOrdenada.comparatorProperty().bind(tabela.comparatorProperty());
            tabela.setItems(listaOrdenada);
        }
    }

    private void aplicarFiltro(String termo) {
        if (listaFiltrada == null) return;

        listaFiltrada.setPredicate(multa -> {
            if (multa == null) return false;
            if (termo == null || termo.trim().isEmpty()) {
                return true;
            }

            String termoFormatado = termo.trim().toLowerCase();
            boolean bateuId = String.valueOf(multa.getId()).contains(termoFormatado);
            boolean bateuContrato = String.valueOf(multa.getIdContrato()).contains(termoFormatado);
            boolean bateuTipo = multa.getTipo() != null && multa.getTipo().toLowerCase().contains(termoFormatado);
            boolean bateuDescricao = multa.getDescricao() != null && multa.getDescricao().toLowerCase().contains(termoFormatado);
            boolean bateuStatus = (multa.isPaga() ? "paga" : "pendente").contains(termoFormatado);

            return bateuId || bateuContrato || bateuTipo || bateuDescricao || bateuStatus;
        });
    }

    @FXML
    private void cadastrarMulta() {
        if (sistema == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cadastrar Multa");
        dialog.setHeaderText("Registro de infração / penalidade");

        ButtonType btnSalvar = new ButtonType("Registrar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField txtIdContrato = new TextField();
        txtIdContrato.setPromptText("Ex: 1");

        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList(
            "Atraso na Devolução",
            "Dano Material",
            "Extravio de Item",
            "Mau Uso",
            "Outro"
        ));
        cbTipo.setValue("Atraso na Devolução");
        cbTipo.setMaxWidth(Double.MAX_VALUE);

        TextField txtDescricao = new TextField();
        txtDescricao.setPromptText("Descrição da ocorrência");

        TextField txtValor = new TextField();
        txtValor.setPromptText("Ex: 50.00");

        grid.add(new Label("ID do Contrato:"), 0, 0);
        grid.add(txtIdContrato, 1, 0);
        grid.add(new Label("Tipo de Multa:"), 0, 1);
        grid.add(cbTipo, 1, 1);
        grid.add(new Label("Descrição:"), 0, 2);
        grid.add(txtDescricao, 1, 2);
        grid.add(new Label("Valor (R$):"), 0, 3);
        grid.add(txtValor, 1, 3);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(txtIdContrato::requestFocus);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isEmpty() || res.get() != btnSalvar) {
            return;
        }

        try {
            int idContrato = Integer.parseInt(txtIdContrato.getText().trim());
            String tipo = cbTipo.getValue();
            String descricao = txtDescricao.getText().trim();
            double valor = Double.parseDouble(txtValor.getText().trim());

            if (descricao.isEmpty()) {
                erro("Preencha a descrição da ocorrência.");
                return;
            }

            String data = LocalDate.now(ZoneId.of("America/Recife")).toString();
            int id = sistema.gerarProximoIdMulta();

            Multa multa = new Multa(id, idContrato, tipo, descricao, valor, data);

            if (sistema.registrarMulta(multa)) {
                info("Multa cadastrada com sucesso! ID: " + id);
                listarMultas();
            } else {
                erro("Erro ao cadastrar multa.");
            }
        } catch (NumberFormatException e) {
            erro("ID do contrato e Valor devem ser números válidos.");
        }
    }

    @FXML
    private void buscarMultas() {
        Optional<Integer> id = pedirInteiro(ID);
        if (id.isEmpty()) return;
        mostraMulta(id.get());
    }

    @FXML
    private void listarMultas() {
        if (sistema == null) return;
        List<Multa> multas = sistema.listarMultas();
        if (multas != null) {
            listaMultas.setAll(multas);
        } else {
            listaMultas.clear();
        }
    }

    @FXML
    private void buscarContrato() {
        Optional<Integer> id = pedirInteiro(ID_CONTRATO);
        if (id.isEmpty()) return;

        List<Multa> multas = sistema.listarMultasPorContrato(id.get());
        if (multas == null || multas.isEmpty()) {
            listaMultas.clear();
            info("Nenhuma multa encontrada para esse contrato.");
            return;
        }

        listaMultas.setAll(multas);
    }

    @FXML
    private void multasPedentens() {
        if (sistema == null) return;
        List<Multa> multas = sistema.listarMultasPendentes();
        if (multas == null || multas.isEmpty()) {
            listaMultas.clear();
            info("Nenhuma multa pendente.");
            return;
        }

        listaMultas.setAll(multas);
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
            listarMultas();
        } else {
            erro("Erro ao quitar multa.");
        }
    }

    private Multa obterMultaAlvo() {
        Multa atual = (tabela != null) ? tabela.getSelectionModel().getSelectedItem() : null;
        if (tabela != null) tabela.getSelectionModel().clearSelection();

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
        listaMultas.setAll(multa);
    }
}