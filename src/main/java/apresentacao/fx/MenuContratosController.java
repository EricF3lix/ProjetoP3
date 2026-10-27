package apresentacao.fx;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import entidades.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MenuContratosController extends MenuController {

    private static final String ID_CONTRATO = "Digite o ID do contrato: ";
    private static final String CONTRATO_NAO_ENCONTRADO = "Contrato não encontrado.";
    private static final String FILTRO_TODOS = "Todos";
    private static final ZoneId FUSO_HORARIO = ZoneId.of("America/Recife");

    @FXML
    private ComboBox<String> cbFiltro;

    @FXML
    private Button btnLimparPesquisa;

    @FXML 
    private TableView<ContratoAluguel> tabela;

    @FXML private VBox dadosContrato;
    @FXML private VBox tabelaContrato;

    @FXML private TextField idCliente;
    @FXML private TextField idItem;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpDataFinal;

    private final ObservableList<ContratoAluguel> listaContratos = FXCollections.observableArrayList();
    private FilteredList<ContratoAluguel> listaFiltrada;

    private record DadosAluguelForm(String idClienteStr, String idItemStr, LocalDate dataRet, LocalDate dataDev) {}

    @Override
    protected String getTitulo() {
        return "Gerenciar Contratos";
    }

    @FXML
    public void initialize() {
        configurarResponsividade();
        configurarFiltroPesquisa();
    }

    @Override
    protected void aoExibir() {
        carregarTabela();
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

    private void configurarFiltroPesquisa() {
        if (cbFiltro != null) {
            cbFiltro.setItems(FXCollections.observableArrayList(FILTRO_TODOS, "ID", "Cliente", "Item", "Status"));
            cbFiltro.setValue(FILTRO_TODOS);
            cbFiltro.valueProperty().addListener((obs, antigo, novo) ->
                aplicarFiltro(pesquisa != null ? pesquisa.getText() : "")
            );
        }

        listaFiltrada = new FilteredList<>(listaContratos, p -> true);

        if (pesquisa != null) {
            pesquisa.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro(novo));
        }

        SortedList<ContratoAluguel> listaOrdenada = new SortedList<>(listaFiltrada);
        if (tabela != null) {
            listaOrdenada.comparatorProperty().bind(tabela.comparatorProperty());
            tabela.setItems(listaOrdenada);
        }
    }

    private void aplicarFiltro(String termo) {
        if (listaFiltrada == null) return;

        listaFiltrada.setPredicate(contrato -> {
            if (contrato == null) return false;
            if (termo == null || termo.trim().isEmpty()) {
                return true;
            }

            String termoFormatado = termo.trim().toLowerCase();
            String filtroSelecionado = cbFiltro != null && cbFiltro.getValue() != null ? cbFiltro.getValue() : FILTRO_TODOS;

            boolean bateuId = String.valueOf(contrato.getId()).contains(termoFormatado);
            boolean bateuCliente = contrato.getCliente() != null && contrato.getCliente().getNome() != null 
                    && contrato.getCliente().getNome().toLowerCase().contains(termoFormatado);
            boolean bateuItem = contrato.getItem() != null && contrato.getItem().getNome() != null 
                    && contrato.getItem().getNome().toLowerCase().contains(termoFormatado);
            boolean bateuStatus = contrato.getStatus() != null && contrato.getStatus().toLowerCase().contains(termoFormatado);

            return switch (filtroSelecionado) {
                case "ID" -> bateuId;
                case "Cliente" -> bateuCliente;
                case "Item" -> bateuItem;
                case "Status" -> bateuStatus;
                default -> bateuId || bateuCliente || bateuItem || bateuStatus;
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

    private void carregarTabela() {
        if (sistema != null && tabela != null) {
            List<ContratoAluguel> contratos = sistema.listarContratos();
            if (contratos != null) {
                listaContratos.setAll(contratos);
            }
        }
    }

    @FXML
    public void listarContratos(ActionEvent event) {
        aoLimparPesquisa(event);
        carregarTabela();
        mostrarTabela();
    }


    @FXML
    public void buscarContrato(ActionEvent event) {
        mostrarTabela();
        String texto = pesquisa != null ? pesquisa.getText().trim() : "";

        if (texto.isEmpty()) {
            Optional<Integer> idOpt = pedirInteiro(ID_CONTRATO);
            if (idOpt.isEmpty()) return;
            texto = String.valueOf(idOpt.get());
        }

        try {
            int id = Integer.parseInt(texto);
            ContratoAluguel contrato = sistema != null ? sistema.buscarContrato(id) : null;

            if (contrato != null) {
                if (cbFiltro != null) cbFiltro.setValue("ID");
                if (pesquisa != null) pesquisa.setText(String.valueOf(id));
            } else {
                erro(CONTRATO_NAO_ENCONTRADO);
            }
        } catch (NumberFormatException e) {
            erro("Por favor, informe um ID numérico válido.");
        }
    }

    @FXML private void mostrarTabela(){
        tabelaContrato.setVisible(true);
        dadosContrato.setVisible(false);
    }

    @FXML private void escondeTabela(){
        tabelaContrato.setVisible(false);
        dadosContrato.setVisible(true);
    }

    @FXML
    private void registrarAluguel(ActionEvent event) {
        if (idCliente.getText().trim().isEmpty() || idItem.getText().trim().isEmpty() || dpDataInicio.getValue() == null || dpDataInicio.getValue() == null){
            erro("Todos os campos devem ser preenchidos");
            return;
        }

        int idcliente = Integer.parseInt(idCliente.getText().trim());
        int iditem = Integer.parseInt(idItem.getText().trim());
        LocalDate datainicio = dpDataInicio.getValue();
        LocalDate datafinal = dpDataFinal.getValue();

        if (datainicio.isAfter(datafinal)){
            erro("A data inicial nao pode ser apos a data final");
            return;
        }

        long quantidadedias = ChronoUnit.DAYS.between(datainicio,datafinal);

        String datainiciotxt = String.valueOf(datainicio);
        String datafinaltxt = String.valueOf(datafinal);

        int id = sistema.gerarProximoIdContrato();
        Usuario usuario = sistema.buscarUsuario(idcliente);

        if (!(usuario instanceof Cliente)){
            erro("Cliente não encontrado");
            return;
        }

        Cliente cliente = new Cliente(usuario.getId(),usuario.getNome(),usuario.getEmail(),usuario.getCpf(),usuario.getSenha());

        Item item = sistema.buscarItem(iditem);

        if (item == null){
            erro("Itemnão encontrado");
            return;
        }

        double valorTotal = quantidadedias * item.getTaxaDiaria();

        ContratoAluguel aluguel = new ContratoAluguel(id,cliente,item,datainiciotxt,datafinaltxt,valorTotal);

        sistema.cadastrarContrato(aluguel);

        escrever("Alguel cadastrado com sucesso");

    }

    @FXML
    private void finalizarContrato(ActionEvent event) {
        ContratoAluguel contrato = obterContratoAlvo();
        if (contrato == null) return;

        if (!contrato.estaAtivo() || contrato.estaFinalizado()) {
            erro("Este contrato não está ativo. Status: " + contrato.getStatus());
            return;
        }

        LocalDate real = exibirDialogoFinalizacao(contrato);
        if (real == null) return;

        processarFinalizacao(contrato, real);
    }

    private LocalDate exibirDialogoFinalizacao(ContratoAluguel contrato) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Finalizar Contrato");
        dialog.setHeaderText("Encerramento do Contrato ID: " + contrato.getId());

        ButtonType btnFinalizar = new ButtonType("Finalizar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnFinalizar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 10, 10));

        DatePicker dpReal = new DatePicker(LocalDate.now(FUSO_HORARIO));
        dpReal.setDisable(true);

        CheckBox chkHoje = new CheckBox("Data de hoje (" + LocalDate.now(FUSO_HORARIO) + ")");
        chkHoje.setSelected(true);

        chkHoje.selectedProperty().addListener((obs, antigo, selecionado) -> {
            boolean estaSelecionado = Boolean.TRUE.equals(selecionado);
            dpReal.setDisable(estaSelecionado);
            if (estaSelecionado) {
                dpReal.setValue(LocalDate.now(FUSO_HORARIO));
            }
        });

        grid.add(chkHoje, 0, 0, 2, 1);
        grid.add(new Label("Data real de devolução:"), 0, 1);
        grid.add(dpReal, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isEmpty() || resultado.get() != btnFinalizar) {
            return null;
        }

        return dpReal.getValue();
    }

    private void processarFinalizacao(ContratoAluguel contrato, LocalDate real) {
        LocalDate prevista = LocalDate.parse(contrato.getDataDevolucaoPrevista());
        contrato.setDataDevolucaoReal(real.toString());

        long diasAtraso = ChronoUnit.DAYS.between(prevista, real);

        if (diasAtraso > 0) {
            double multa = sistema.calcularMulta(diasAtraso, contrato.getItem().getTaxaDiaria());
            contrato.setValorMulta(multa);
            contrato.setMultaPaga(false);
            info(String.format("Contrato finalizado com atraso de %d dia(s). Multa gerada: R$ %.2f", diasAtraso, multa));
        } else {
            info("Devolução realizada dentro do prazo.");
        }

        contrato.getItem().devolver();

        if (sistema.finalizarContrato(contrato.getId())) {
            carregarTabela();
        } else {
            erro("Erro ao finalizar contrato");
        }
    }

    @FXML
    private void cancelarContrato(ActionEvent event) {
        mostrarTabela();
        ContratoAluguel contrato = obterContratoAlvo();
        if (contrato == null) return;

        if ("FINALIZADO".equalsIgnoreCase(contrato.getStatus())) {
            erro("Não é possível cancelar um contrato já finalizado");
            return;
        }

        if (!confirmar("Tem certeza que deseja cancelar o contrato ID " + contrato.getId() + "?")) {
            return;
        }

        if (sistema.cancelarContrato(contrato.getId())) {
            contrato.getItem().devolver();
            info("Contrato cancelado com sucesso. Item devolvido ao estoque.");
            carregarTabela();
        } else {
            erro("Erro ao cancelar contrato.");
        }
    }

    private ContratoAluguel obterContratoAlvo() {
        mostrarTabela();
        if (tabela == null) return null;
        ContratoAluguel selecionado = tabela.getSelectionModel().getSelectedItem();
        tabela.getSelectionModel().clearSelection();

        if (selecionado != null) {
            return selecionado;
        }

        return pedirInteiro(ID_CONTRATO)
                .map(id -> {
                    ContratoAluguel c = sistema.buscarContrato(id);
                    if (c == null) erro(CONTRATO_NAO_ENCONTRADO);
                    return c;
                })
                .orElse(null);
    }
}