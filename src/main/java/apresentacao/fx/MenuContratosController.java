package apresentacao.fx;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class MenuContratosController extends MenuController {

    private static final String ID_CONTRATO = "Digite o ID do contrato: ";
    private static final String CONTRATO_NAO_ENCONTRADO = "Contrato não encontrado.";
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    @FXML private TextField pesquisa;
    @FXML private TableView<ContratoAluguel> tabela;
    @FXML private TableColumn<ContratoAluguel, Integer> colId;
    @FXML private TableColumn<ContratoAluguel, String> colCliente;
    @FXML private TableColumn<ContratoAluguel, String> colItem;
    @FXML private TableColumn<ContratoAluguel, String> colRetirada;
    @FXML private TableColumn<ContratoAluguel, String> colDevolucao;
    @FXML private TableColumn<ContratoAluguel, ContratoAluguel> colStatus;
    @FXML private TableColumn<ContratoAluguel, Double> colValor;

    private final ObservableList<ContratoAluguel> listaContratos = FXCollections.observableArrayList();
    private FilteredList<ContratoAluguel> listaFiltrada;

    @Override
    protected String getTitulo() {
        return "Gerenciar Contratos";
    }

    @FXML
    public void initialize() {
        configurarResponsividade();
        configurarColunas();
        configurarTabelaVazia();
        configurarFiltroPesquisa();
    }

    @Override
    protected void aoExibir() {
        listarContratos();
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

    private void configurarTabelaVazia() {
        if (tabela == null) return;

        VBox estadoVazio = new VBox(8);
        estadoVazio.setAlignment(Pos.CENTER);
        estadoVazio.setStyle("-fx-padding: 30px;");

        FontIcon icone = new FontIcon("fas-file-signature");
        icone.setIconSize(34);
        icone.setStyle("-fx-icon-color: #8e8e93;");

        Label titulo = new Label("Nenhum contrato encontrado");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        Label subtitulo = new Label("Utilize o campo de busca acima ou registre uma nova locação.");
        subtitulo.getStyleClass().add("label-subtitulo");
        subtitulo.setStyle("-fx-font-size: 13px;");

        estadoVazio.getChildren().addAll(icone, titulo, subtitulo);
        tabela.setPlaceholder(estadoVazio);
    }

    private void configurarColunas() {
        if (colId != null) {
            colId.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getId()));
            colId.setStyle("-fx-alignment: CENTER;");
        }

        if (colCliente != null) {
            colCliente.setCellValueFactory(d -> {
                Cliente c = d.getValue().getCliente();
                return new SimpleStringProperty(c != null ? c.getNome() : "N/D");
            });
            colCliente.setStyle("-fx-font-weight: bold;");
        }

        if (colItem != null) {
            colItem.setCellValueFactory(d -> {
                Item i = d.getValue().getItem();
                return new SimpleStringProperty(i != null ? i.getNome() : "N/D");
            });
        }

        if (colRetirada != null) {
            colRetirada.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDataRetirada()));
            colRetirada.setStyle("-fx-alignment: CENTER;");
        }

        if (colDevolucao != null) {
            colDevolucao.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDataDevolucaoPrevista()));
            colDevolucao.setStyle("-fx-alignment: CENTER;");
        }

        if (colValor != null) {
            colValor.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getValorTotal()));
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
                        setStyle("-fx-font-weight: bold;");
                    }
                }
            });
        }

        if (colStatus != null) {
            colStatus.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue()));
            colStatus.setCellFactory(col -> new TableCell<>() {
                private final Label badge = new Label();

                @Override
                protected void updateItem(ContratoAluguel contrato, boolean empty) {
                    super.updateItem(contrato, empty);
                    if (empty || contrato == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        String st = contrato.getStatus() != null ? contrato.getStatus().toUpperCase() : "";
                        LocalDate hoje = LocalDate.now(ZoneId.of("America/Recife"));
                        LocalDate prevista = null;
                        try {
                            prevista = LocalDate.parse(contrato.getDataDevolucaoPrevista());
                        } catch (Exception ignored) {}

                        badge.getStyleClass().removeAll("badge-sucesso", "badge-perigo", "badge-aviso", "badge-info", "badge-neutro");
                        badge.setStyle(""); // Limpa travas inline para a folha CSS atuar em claro/escuro

                        if ("FINALIZADO".equals(st)) {
                            badge.setText("● Concluído");
                            badge.getStyleClass().add("badge-neutro");
                        } else if ("CANCELADO".equals(st)) {
                            badge.setText("● Cancelado");
                            badge.getStyleClass().add("badge-perigo");
                        } else if (prevista != null && hoje.isAfter(prevista)) {
                            badge.setText("● Atrasado");
                            badge.getStyleClass().add("badge-perigo");
                        } else {
                            badge.setText("● Em Dia");
                            badge.getStyleClass().add("badge-sucesso");
                        }

                        setAlignment(Pos.CENTER);
                        setGraphic(badge);
                        setText(null);
                    }
                }
            });
        }
    }

    private void configurarFiltroPesquisa() {
        listaFiltrada = new FilteredList<>(listaContratos, p -> true);

        if (pesquisa != null) {
            pesquisa.textProperty().addListener((obs, antigo, novo) -> {
                String termo = (novo == null) ? "" : novo.trim().toLowerCase();
                listaFiltrada.setPredicate(c -> {
                    if (c == null) return false;
                    if (termo.isEmpty()) return true;

                    boolean bateuId = String.valueOf(c.getId()).contains(termo);
                    boolean bateuCliente = c.getCliente() != null && c.getCliente().getNome().toLowerCase().contains(termo);
                    boolean bateuItem = c.getItem() != null && c.getItem().getNome().toLowerCase().contains(termo);
                    boolean bateuStatus = c.getStatus() != null && c.getStatus().toLowerCase().contains(termo);

                    return bateuId || bateuCliente || bateuItem || bateuStatus;
                });
            });
        }

        SortedList<ContratoAluguel> ordenada = new SortedList<>(listaFiltrada);
        if (tabela != null) {
            ordenada.comparatorProperty().bind(tabela.comparatorProperty());
            tabela.setItems(ordenada);
        }
    }

    @FXML
    private void aoLimparPesquisa() {
        if (pesquisa != null) {
            pesquisa.clear();
        }
    }

    @FXML
    private void listarContratos() {
        if (sistema == null) return;
        List<ContratoAluguel> contratos = sistema.listarContratos();
        if (contratos != null) {
            listaContratos.setAll(contratos);
        } else {
            listaContratos.clear();
        }
    }

    @FXML
    private void registrarAluguel() {
        if (sistema == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Aluguel");
        dialog.setHeaderText("Novo Contrato de Locação");

        ButtonType btnConfirmar = new ButtonType("Registrar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtIdCliente = new TextField();
        txtIdCliente.setPromptText("Ex: 1");

        TextField txtIdItem = new TextField();
        txtIdItem.setPromptText("Ex: 10");

        DatePicker dpRetirada = new DatePicker(LocalDate.now());
        DatePicker dpDevolucao = new DatePicker(LocalDate.now().plusDays(1));

        grid.add(new Label("ID do Cliente:"), 0, 0);
        grid.add(txtIdCliente, 1, 0);
        grid.add(new Label("ID do Item:"), 0, 1);
        grid.add(txtIdItem, 1, 1);
        grid.add(new Label("Data de Retirada:"), 0, 2);
        grid.add(dpRetirada, 1, 2);
        grid.add(new Label("Devolução Prevista:"), 0, 3);
        grid.add(dpDevolucao, 1, 3);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(txtIdCliente::requestFocus);

        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isEmpty() || resultado.get() != btnConfirmar) {
            return;
        }

        int idCliente;
        int idItem;

        try {
            idCliente = Integer.parseInt(txtIdCliente.getText().trim());
            idItem = Integer.parseInt(txtIdItem.getText().trim());
        } catch (NumberFormatException e) {
            animarErroShake(tabela);
            notificarPopUp("IDs do cliente e item devem ser números válidos.", false);
            return;
        }

        Usuario usuario = sistema.buscarUsuario(idCliente);
        if (!(usuario instanceof Cliente cliente)) {
            animarErroShake(tabela);
            notificarPopUp("Cliente não encontrado com o ID informado.", false);
            return;
        }

        if (sistema.clientePossuiMultaPendente(cliente.getId())) {
            animarErroShake(tabela);
            notificarPopUp("Cliente possui multa(s) pendente(s). Locação bloqueada.", false);
            return;
        }

        Item item = sistema.buscarItem(idItem);
        if (item == null) {
            animarErroShake(tabela);
            notificarPopUp("Item não encontrado com o ID informado.", false);
            return;
        }

        if (!item.estaDisponivel()) {
            animarErroShake(tabela);
            notificarPopUp("Item indisponível no momento. Status: " + item.getStatus(), false);
            return;
        }

        LocalDate dataRet = dpRetirada.getValue();
        LocalDate dataDev = dpDevolucao.getValue();

        if (dataRet == null || dataDev == null) {
            animarErroShake(tabela);
            notificarPopUp("Defina as datas de retirada e devolução.", false);
            return;
        }

        long quantidadeDias = ChronoUnit.DAYS.between(dataRet, dataDev);
        if (quantidadeDias <= 0) {
            animarErroShake(tabela);
            notificarPopUp("A data de devolução deve ser posterior à de retirada.", false);
            return;
        }

        double valorTotal = quantidadeDias * item.getTaxaDiaria();
        int idContrato = sistema.gerarProximoIdContrato();

        ContratoAluguel contrato = new ContratoAluguel(
                idContrato, cliente, item, dataRet.toString(), dataDev.toString(), valorTotal);

        if (!sistema.cadastrarContrato(contrato)) {
            animarErroShake(tabela);
            notificarPopUp("Erro ao salvar contrato no sistema.", false);
            return;
        }

        item.alugar();
        notificarPopUp("Aluguel registrado com sucesso! Contrato #" + idContrato, true);
        listarContratos();
    }

    @FXML
    private void buscarContrato() {
        pedirInteiro(ID_CONTRATO).ifPresent(this::mostraContrato);
    }

    @FXML
    private void finalizarContrato() {
        ContratoAluguel contrato = obterContratoAlvo();
        if (contrato == null) return;

        if (!contrato.estaAtivo() || contrato.estaFinalizado()) {
            animarErroShake(tabela);
            notificarPopUp("Este contrato já se encontra encerrado.", false);
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Finalizar Contrato");
        dialog.setHeaderText("Devolução do Item: " + contrato.getItem().getNome() + " (Contrato #" + contrato.getId() + ")");

        ButtonType btnFinalizar = new ButtonType("Finalizar Devolução", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnFinalizar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        DatePicker dpReal = new DatePicker(LocalDate.now());
        dpReal.setDisable(true);

        CheckBox chkHoje = new CheckBox("Devolvido hoje (" + LocalDate.now() + ")");
        chkHoje.setSelected(true);

        chkHoje.selectedProperty().addListener((obs, antigo, selecionado) -> {
            dpReal.setDisable(selecionado);
            if (selecionado) {
                dpReal.setValue(LocalDate.now());
            }
        });

        grid.add(chkHoje, 0, 0, 2, 1);
        grid.add(new Label("Data real da devolução:"), 0, 1);
        grid.add(dpReal, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isEmpty() || resultado.get() != btnFinalizar) {
            return;
        }

        LocalDate real = dpReal.getValue();
        if (real == null) {
            animarErroShake(tabela);
            notificarPopUp("Selecione uma data de devolução válida.", false);
            return;
        }

        LocalDate prevista = LocalDate.parse(contrato.getDataDevolucaoPrevista());
        contrato.setDataDevolucaoReal(real.toString());

        long diasAtraso = ChronoUnit.DAYS.between(prevista, real);

        if (diasAtraso > 0) {
            double multa = sistema.calcularMulta(diasAtraso, contrato.getItem().getTaxaDiaria());
            contrato.setValorMulta(multa);
            contrato.setMultaPaga(false);
            notificarPopUp("Atraso de " + diasAtraso + " dia(s). Multa gerada: " + MOEDA.format(multa), false);
        } else {
            notificarPopUp("Devolução realizada dentro do prazo!", true);
        }

        contrato.getItem().devolver();

        if (sistema.finalizarContrato(contrato.getId())) {
            listarContratos();
        } else {
            animarErroShake(tabela);
            notificarPopUp("Erro ao atualizar encerramento do contrato.", false);
        }
    }

    @FXML
    private void cancelarContrato() {
        ContratoAluguel contrato = obterContratoAlvo();
        if (contrato == null) return;

        if ("FINALIZADO".equalsIgnoreCase(contrato.getStatus())) {
            animarErroShake(tabela);
            notificarPopUp("Não é possível cancelar um contrato já finalizado.", false);
            return;
        }

        if (!confirmar("Tem certeza que deseja cancelar o contrato #" + contrato.getId() + "?")) {
            return;
        }

        if (sistema.cancelarContrato(contrato.getId())) {
            contrato.getItem().devolver();
            notificarPopUp("Contrato #" + contrato.getId() + " cancelado. Item retornado ao catálogo.", true);
            listarContratos();
        } else {
            animarErroShake(tabela);
            notificarPopUp("Erro ao cancelar o contrato.", false);
        }
    }

    private ContratoAluguel obterContratoAlvo() {
        ContratoAluguel selecionado = tabela.getSelectionModel().getSelectedItem();

        if (selecionado != null) {
            return selecionado;
        }

        return pedirInteiro(ID_CONTRATO)
                .map(id -> {
                    ContratoAluguel c = sistema.buscarContrato(id);
                    if (c == null) {
                        animarErroShake(tabela);
                        notificarPopUp(CONTRATO_NAO_ENCONTRADO, false);
                    }
                    return c;
                })
                .orElse(null);
    }

    private void mostraContrato(int id) {
        ContratoAluguel contrato = sistema.buscarContrato(id);
        if (contrato == null) {
            animarErroShake(tabela);
            notificarPopUp(CONTRATO_NAO_ENCONTRADO, false);
            return;
        }
        listaContratos.setAll(contrato);
        notificarPopUp("Contrato #" + id + " localizado.", true);
    }
}