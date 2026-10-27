package apresentacao.fx;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import entidades.ContratoAluguel;
import entidades.Multa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MenuMultasController extends MenuController {

    private static final String ID_MULTA = "Digite o ID da multa:";
    private static final String ID_CONTRATO = "Digite o ID do contrato:";
    private static final String NAO_ENCONTRADA = "Multa não encontrada.";
    private static final String FILTRO_TODOS = "Todos";
    private static final String FILTRO_ID_MULTA = "ID Multa";
    private static final String ESTILO_BADGE_COMUM =
            "-fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 10; -fx-font-size: 11px;";
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    @FXML
    private ComboBox<String> cbFiltro;

    @FXML
    private Button btnLimparPesquisa;

    @FXML private TableView<Multa> tabela;
    @FXML private TableColumn<Multa, Integer> colID;
    @FXML private TableColumn<Multa, Integer> colContrato;
    @FXML private TableColumn<Multa, String> colTipo;
    @FXML private TableColumn<Multa, String> colDescricao;
    @FXML private TableColumn<Multa, Double> colValor;
    @FXML private TableColumn<Multa, Boolean> colStatus;
    @FXML private TableColumn<Multa, String> colData;

    @FXML private ComboBox<String> cbContrato;
    @FXML private TextField descricaoMulta;
    @FXML private TextField valorMulta;

    @FXML private ComboBox<String> tipoMulta;

    @FXML private VBox tabelaMulta;
    @FXML private VBox dadosMulta;

    private final ObservableList<Multa> listaMultas = FXCollections.observableArrayList();
    private FilteredList<Multa> listaFiltrada;

    private List<ContratoAluguel> listaContratosOpcoes = new ArrayList<>();

    @Override
    protected String getTitulo() {
        return "Gerenciar Multas";
    }

    @Override
    protected void aoExibir() {
        configurarResponsividade();
        configurarColunas();
        if (tipoMulta != null) {
            tipoMulta.getItems().setAll("Atraso na Devolução", "Dano Material", "Extravio de Item", "Mau Uso", "Outro");
        }
        atualizarTabela();
        configurarFiltroPesquisa();
    }

    private void carregarContratosFormulario() {
        if (sistema == null) return;

        List<ContratoAluguel> contratos = sistema.listarContratos();
        if (contratos != null) {
            listaContratosOpcoes = contratos;
        } else {
            listaContratosOpcoes = new ArrayList<>();
        }

        if (cbContrato != null) {
            cbContrato.getItems().setAll(listaContratosOpcoes.stream()
                    .map(c -> "Contrato #" + c.getId() + " - " + 
                             (c.getCliente() != null ? c.getCliente().getNome() : "Sem Cliente") + 
                             " (" + (c.getItem() != null ? c.getItem().getNome() : "Sem Item") + ")")
                    .toList());
            cbContrato.getSelectionModel().clearSelection();
            cbContrato.setPromptText(listaContratosOpcoes.isEmpty() ? "Nenhum contrato cadastrado" : "Selecione o contrato");
        }
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
            colStatus.setCellFactory(col -> criarCelulaStatus());
        }
    }

    private TableCell<Multa, Boolean> criarCelulaStatus() {
        return new TableCell<>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(Boolean paga, boolean empty) {
                super.updateItem(paga, empty);

                if (empty || paga == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                boolean foiPaga = paga;
                badge.setText(foiPaga ? "● Paga" : "● Pendente");
                badge.setStyle(foiPaga
                        ? "-fx-background-color: #d4edda; -fx-text-fill: #155724; " + ESTILO_BADGE_COMUM
                        : "-fx-background-color: #fff3cd; -fx-text-fill: #856404; " + ESTILO_BADGE_COMUM
                );

                setAlignment(Pos.CENTER);
                setGraphic(badge);
                setText(null);
            }
        };
    }

    private void configurarValueFactory() {
        if (colID != null) colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colContrato != null) colContrato.setCellValueFactory(new PropertyValueFactory<>("idContrato"));
        if (colTipo != null) colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        if (colDescricao != null) colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        if (colValor != null) colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        if (colStatus != null) colStatus.setCellValueFactory(new PropertyValueFactory<>("paga"));
        if (colData != null) colData.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
    }

    private void configurarFiltroPesquisa() {
        if (cbFiltro != null) {
            cbFiltro.setItems(FXCollections.observableArrayList(FILTRO_TODOS, FILTRO_ID_MULTA, "ID Contrato", "Tipo", "Status"));
            cbFiltro.setValue(FILTRO_TODOS);
            cbFiltro.valueProperty().addListener((obs, antigo, novo) ->
                    aplicarFiltro(pesquisa != null ? pesquisa.getText() : "")
            );
        }

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
            String filtroSelecionado = cbFiltro != null && cbFiltro.getValue() != null ? cbFiltro.getValue() : FILTRO_TODOS;

            boolean bateuId = String.valueOf(multa.getId()).contains(termoFormatado);
            boolean bateuContrato = String.valueOf(multa.getIdContrato()).contains(termoFormatado);
            boolean bateuTipo = multa.getTipo() != null && multa.getTipo().toLowerCase().contains(termoFormatado);
            boolean bateuDescricao = multa.getDescricao() != null && multa.getDescricao().toLowerCase().contains(termoFormatado);
            boolean bateuStatus = (multa.isPaga() ? "paga" : "pendente").contains(termoFormatado);

            return switch (filtroSelecionado) {
                case FILTRO_ID_MULTA -> bateuId;
                case "ID Contrato" -> bateuContrato;
                case "Tipo" -> bateuTipo;
                case "Status" -> bateuStatus;
                default -> bateuId || bateuContrato || bateuTipo || bateuDescricao || bateuStatus;
            };
        });
    }

    @FXML
    public void aoLimparPesquisa(ActionEvent event) {
        if (pesquisa != null) {
            pesquisa.clear();
        }
        if (cbFiltro != null) {
            cbFiltro.setValue(FILTRO_TODOS);
        }
    }

    @FXML
    private void mostrarTabela() {
        if (tabelaMulta != null && dadosMulta != null) {
            tabelaMulta.setVisible(true);
            tabelaMulta.setManaged(true);
            dadosMulta.setVisible(false);
            dadosMulta.setManaged(false);
        }
    }

    @FXML
    private void atualizarTabela() {
        listarMultas();
        mostrarTabela();
    }

    @FXML
    private void esconderTabela() {
        if (tabelaMulta != null && dadosMulta != null) {
            tabelaMulta.setVisible(false);
            tabelaMulta.setManaged(false);
            dadosMulta.setVisible(true);
            dadosMulta.setManaged(true);
            carregarContratosFormulario();
        }
    }

    @FXML
    private void cadastrarMulta() {
        if (cbContrato != null) cbContrato.getSelectionModel().clearSelection();
        if (descricaoMulta != null) descricaoMulta.clear();
        if (valorMulta != null) valorMulta.clear();
        if (tipoMulta != null) tipoMulta.setValue(null);

        esconderTabela();
    }

    @FXML
    private void registrarMulta() {
        int posContrato = cbContrato != null ? cbContrato.getSelectionModel().getSelectedIndex() : -1;
        String descricaomulta = descricaoMulta != null ? descricaoMulta.getText().trim() : "";
        String txtvalor = valorMulta != null ? valorMulta.getText().trim() : "";
        String tipo = tipoMulta != null ? tipoMulta.getValue() : null;

        if (posContrato < 0 || descricaomulta.isEmpty() || txtvalor.isEmpty() || tipo == null) {
        	notificarPopUpErro("Preencha todos os campos!");
            return;
        }

        float valor;
        try {
            valor = Float.parseFloat(txtvalor.replace(",", "."));
        } catch (NumberFormatException e) {
        	notificarPopUpErro("Valor inválido.");
            return;
        }

        if (Float.isNaN(valor) || Float.isInfinite(valor) || valor <= 0) {
        	notificarPopUpErro("Valor inválido.");
            return;
        }

        ContratoAluguel contratoSelecionado = listaContratosOpcoes.get(posContrato);
        int idcontrato = contratoSelecionado.getId();

        int id = sistema.gerarProximoIdMulta();
        String data = LocalDate.now(ZoneId.of("America/Recife")).toString();

        Multa multa = new Multa(id, idcontrato, tipo, descricaomulta, valor, data);

        if (sistema.registrarMulta(multa)) {
        	notificarPopUp("Multa cadastrada com sucesso! ID: " + id);
            atualizarTabela();
        } else {
        	notificarPopUpErro("Erro ao cadastrar multa.");
        }
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
        mostrarTabela();
        Optional<Integer> id = pedirInteiro(ID_CONTRATO);
        if (id.isEmpty()) return;

        List<Multa> multas = sistema.listarMultasPorContrato(id.get());
        if (multas == null || multas.isEmpty()) {
            listaMultas.clear();
            notificarPopUp("Nenhuma multa encontrada para esse contrato.");
            return;
        }

        listaMultas.setAll(multas);
    }

    @FXML
    private void multasPedentens() {
        mostrarTabela();
        if (sistema == null) return;
        List<Multa> multas = sistema.listarMultasPendentes();
        if (multas == null || multas.isEmpty()) {
            listaMultas.clear();
            notificarPopUp("Nenhuma multa pendente.");
            return;
        }

        listaMultas.setAll(multas);
    }

    @FXML
    private void quitarMulta() {
        mostrarTabela();
        Multa multa = obterMultaAlvo();
        if (multa == null) return;

        if (multa.isPaga()) {
        	notificarPopUp("Essa multa já está paga.");
            return;
        }

        if (!confirmar("Tem certeza que deseja quitar a multa " + multa.getId() + "?")) {
            return;
        }

        if (sistema.quitarMulta(multa.getId())) {
        	notificarPopUp("Multa quitada com sucesso.");
            listarMultas();
        } else {
        	notificarPopUpErro("Erro ao quitar multa.");
        }
    }

    private Multa obterMultaAlvo() {
        if (tabela == null) return null;
        Multa atual = tabela.getSelectionModel().getSelectedItem();
        tabela.getSelectionModel().clearSelection();

        if (atual == null) {
            Optional<Integer> id = pedirInteiro(ID_MULTA);
            if (id.isEmpty()) return null;
            atual = sistema.buscarMulta(id.get());
            if (atual == null) {
            	notificarPopUpErro(NAO_ENCONTRADA);
            }
        }
        return atual;
    }
}