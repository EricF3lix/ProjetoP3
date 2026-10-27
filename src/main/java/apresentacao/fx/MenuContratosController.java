package apresentacao.fx;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MenuContratosController extends MenuController {

    private static final String ID_CONTRATO = "Digite o ID do contrato: ";
    private static final String CONTRATO_NAO_ENCONTRADO = "Contrato não encontrado.";
    private static final String FILTRO_TODOS = "Todos";
    private static final String FILTRO_ID = "ID";
    private static final String FILTRO_CLIENTE = "Cliente";
    private static final String FILTRO_ITEM = "Item";
    private static final String FILTRO_STATUS = "Status";
    private static final ZoneId FUSO_HORARIO = ZoneId.of("America/Recife");

    @FXML
    private ComboBox<String> cbFiltro;

    @FXML
    private Button btnLimparPesquisa;

    @FXML 
    private TableView<ContratoAluguel> tabela;

    @FXML private VBox dadosContrato;
    @FXML private VBox tabelaContrato;

    @FXML private ComboBox<String> cbCliente;
    @FXML private ComboBox<String> cbItem;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpDataFinal;

    private final ObservableList<ContratoAluguel> listaContratos = FXCollections.observableArrayList();
    private FilteredList<ContratoAluguel> listaFiltrada;

    private List<Cliente> listaClientes = new ArrayList<>();
    private List<Item> listaItensDisponiveis = new ArrayList<>();

    @Override
    protected String getTitulo() {
        return "Gerenciar Contratos";
    }

    @FXML
    public void initialize() {
        configurarResponsividade();
        configurarFiltroPesquisa();
        inicializarDatas();
    }

    @Override
    protected void aoExibir() {
        mostrarTabela();
        carregarTabela();
    }

    private void inicializarDatas() {
        LocalDate hoje = LocalDate.now(FUSO_HORARIO);
        if (dpDataInicio != null) {
            dpDataInicio.setValue(hoje);
        }
        if (dpDataFinal != null) {
            dpDataFinal.setValue(hoje.plusDays(1));
        }
    }

    private void carregarOpcoesFormulario() {
        if (sistema == null) return;

       
        List<Usuario> usuarios = sistema.listarUsuarios();
        if (usuarios != null) {
            listaClientes = usuarios.stream()
                    .filter(Cliente.class::isInstance)
                    .map(Cliente.class::cast)
                    .toList();
        } else {
            listaClientes = List.of();
        }

        if (cbCliente != null) {
            cbCliente.getItems().setAll(listaClientes.stream()
                    .map(c -> c.getId() + " - " + c.getNome())
                    .toList());
            cbCliente.getSelectionModel().clearSelection();

            if (listaClientes.isEmpty()) {
                cbCliente.setPromptText("Nenhum cliente cadastrado");
            } else {
                cbCliente.setPromptText("Selecione o cliente");
            }
        }

        
        List<ContratoAluguel> contratos = sistema.listarContratos();
        List<Integer> idsItensAlugados;
        if (contratos != null) {
            idsItensAlugados = contratos.stream()
                    .filter(c -> c != null && c.estaAtivo() && c.getItem() != null)
                    .map(c -> c.getItem().getId())
                    .toList();
        } else {
            idsItensAlugados = List.of();
        }

       
        List<Item> todosItens = sistema.listarItens();
        if (todosItens != null) {
            listaItensDisponiveis = todosItens.stream()
                    .filter(i -> i != null)
                    .filter(i -> !idsItensAlugados.contains(i.getId()))
                    .filter(i -> i.estaDisponivel() || "DISPONIVEL".equalsIgnoreCase(i.getStatus()) || "DISPONÍVEL".equalsIgnoreCase(i.getStatus()))
                    .toList();
        } else {
            listaItensDisponiveis = List.of();
        }

        if (cbItem != null) {
            cbItem.getItems().setAll(listaItensDisponiveis.stream()
                    .map(i -> i.getId() + " - " + i.getNome() + " (R$ " + String.format("%.2f", i.getTaxaDiaria()) + "/dia)")
                    .toList());
            cbItem.getSelectionModel().clearSelection();

            if (listaItensDisponiveis.isEmpty()) {
                cbItem.setPromptText("Nenhum item disponível");
            } else {
                cbItem.setPromptText("Selecione o item");
            }
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

    private void configurarFiltroPesquisa() {
        if (cbFiltro != null) {
            cbFiltro.setItems(FXCollections.observableArrayList(FILTRO_TODOS, FILTRO_ID, FILTRO_CLIENTE, FILTRO_ITEM, FILTRO_STATUS));
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
                case FILTRO_ID -> bateuId;
                case FILTRO_CLIENTE -> bateuCliente;
                case FILTRO_ITEM -> bateuItem;
                case FILTRO_STATUS -> bateuStatus;
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
    private void mostrarTabela() {
        if (tabelaContrato != null && dadosContrato != null) {
            dadosContrato.setVisible(false);
            dadosContrato.setManaged(false);
            tabelaContrato.setVisible(true);
            tabelaContrato.setManaged(true);
        }
    }

    @FXML 
    private void escondeTabela() {
        if (tabelaContrato != null && dadosContrato != null) {
            tabelaContrato.setVisible(false);
            tabelaContrato.setManaged(false);
            dadosContrato.setVisible(true);
            dadosContrato.setManaged(true);
            carregarOpcoesFormulario();
        }
    }

    @FXML
    private void registrarAluguel(ActionEvent event) {
        if (sistema == null) return;

        int posCliente = cbCliente != null ? cbCliente.getSelectionModel().getSelectedIndex() : -1;
        int posItem = cbItem != null ? cbItem.getSelectionModel().getSelectedIndex() : -1;
        LocalDate dataInicio = dpDataInicio != null ? dpDataInicio.getValue() : null;
        LocalDate dataFinal = dpDataFinal != null ? dpDataFinal.getValue() : null;

        if (posCliente < 0 || posItem < 0 || dataInicio == null || dataFinal == null) {
            erro("Por favor, selecione o cliente, o item e as datas de início e fim.");
            return;
        }

        if (dataInicio.isAfter(dataFinal) || dataInicio.equals(dataFinal)) {
            erro("A data de devolução deve ser posterior à data de retirada.");
            return;
        }

        Cliente cliente = listaClientes.get(posCliente);
        Item item = listaItensDisponiveis.get(posItem);

        if (sistema.clientePossuiMultaPendente(cliente.getId())) {
            erro("Cliente possui multa pendente e não pode realizar novos aluguéis.");
            return;
        }

        if (!item.estaDisponivel()) {
            erro("Item indisponível para aluguel. Status atual: " + item.getStatus());
            return;
        }

        long quantidadeDias = ChronoUnit.DAYS.between(dataInicio, dataFinal);
        double valorTotal = quantidadeDias * item.getTaxaDiaria();
        int idContrato = sistema.gerarProximoIdContrato();

        ContratoAluguel aluguel = new ContratoAluguel(
                idContrato, cliente, item, dataInicio.toString(), dataFinal.toString(), valorTotal);

        if (sistema.cadastrarContrato(aluguel)) {
            item.alugar();
            info("Aluguel registrado com sucesso! ID do Contrato: " + idContrato);
            limparFormulario();
            carregarTabela();
            mostrarTabela();
        } else {
            erro("Erro ao cadastrar o contrato no sistema.");
        }
    }

    private void limparFormulario() {
        if (cbCliente != null) cbCliente.getSelectionModel().clearSelection();
        if (cbItem != null) cbItem.getSelectionModel().clearSelection();
        inicializarDatas();
    }

    @FXML
    private void finalizarContrato(ActionEvent event) {
        mostrarTabela();
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
            erro("Erro ao finalizar contrato.");
        }
    }

    @FXML
    private void cancelarContrato(ActionEvent event) {
        mostrarTabela();
        ContratoAluguel contrato = obterContratoAlvo();
        if (contrato == null) return;

        if ("FINALIZADO".equalsIgnoreCase(contrato.getStatus())) {
            erro("Não é possível cancelar um contrato já finalizado.");
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