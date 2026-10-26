package apresentacao.fx;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class MenuContratosController extends MenuController {

    private static final String ID_CONTRATO = "Digite o ID do contrato: ";
    private static final String CONTRATO_NAO_ENCONTRADO = "Contrato não encontrado.";
    private static final ZoneId FUSO_HORARIO = ZoneId.of("America/Recife");

    @FXML 
    private TableView<ContratoAluguel> tabela;

    private record DadosAluguelForm(String idClienteStr, String idItemStr, LocalDate dataRet, LocalDate dataDev) {}

    @Override
    protected String getTitulo() {
        return "Gerenciar Contratos";
    }

    @FXML
    public void initialize() {
        configurarResponsividade();
    }

    @Override
    protected void aoExibir() {
        configurarResponsividade();
        configurarTabelaEPesquisa(tabela, this::fazPesquisa);
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

    @FXML
    private void registrarAluguel() {
        if (sistema == null) return;

        DadosAluguelForm form = exibirDialogoAluguel();
        if (form == null) return;

        processarRegistroAluguel(form);
    }

    private DadosAluguelForm exibirDialogoAluguel() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Aluguel");
        dialog.setHeaderText("Preencha os dados do novo contrato de aluguel");

        ButtonType btnConfirmar = new ButtonType("Registrar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField txtIdCliente = new TextField();
        txtIdCliente.setPromptText("Ex: 1");

        TextField txtIdItem = new TextField();
        txtIdItem.setPromptText("Ex: 10");

        DatePicker dpRetirada = new DatePicker(LocalDate.now(FUSO_HORARIO));
        DatePicker dpDevolucao = new DatePicker(LocalDate.now(FUSO_HORARIO).plusDays(1));

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
            return null;
        }

        return new DadosAluguelForm(
                txtIdCliente.getText().trim(),
                txtIdItem.getText().trim(),
                dpRetirada.getValue(),
                dpDevolucao.getValue()
        );
    }

    private void processarRegistroAluguel(DadosAluguelForm form) {
        if (form.dataRet() == null || form.dataDev() == null) {
            erro("Por favor, selecione as duas datas no calendário.");
            return;
        }

        long quantidadeDias = ChronoUnit.DAYS.between(form.dataRet(), form.dataDev());
        if (quantidadeDias <= 0) {
            erro("A data de devolução deve ser posterior à data de retirada.");
            return;
        }

        Cliente cliente = validarEObterCliente(form.idClienteStr());
        if (cliente == null) return;

        Item item = validarEObterItem(form.idItemStr());
        if (item == null) return;

        executarCadastroContrato(cliente, item, form.dataRet(), form.dataDev(), quantidadeDias);
    }

    private Cliente validarEObterCliente(String idClienteStr) {
        int idCliente;
        try {
            idCliente = Integer.parseInt(idClienteStr);
        } catch (NumberFormatException e) {
            erro("Os IDs do cliente e do item devem ser numéricos.");
            return null;
        }

        Usuario usuario = sistema.buscarUsuario(idCliente);
        if (!(usuario instanceof Cliente cliente)) {
            erro("Cliente não encontrado.");
            return null;
        }

        if (sistema.clientePossuiMultaPendente(cliente.getId())) {
            erro("Cliente possui multa pendente e não pode realizar novos aluguéis.");
            return null;
        }

        return cliente;
    }

    private Item validarEObterItem(String idItemStr) {
        int idItem;
        try {
            idItem = Integer.parseInt(idItemStr);
        } catch (NumberFormatException e) {
            erro("Os IDs do cliente e do item devem ser numéricos.");
            return null;
        }

        Item item = sistema.buscarItem(idItem);
        if (item == null) {
            erro("Item não encontrado.");
            return null;
        }

        if (!item.estaDisponivel()) {
            erro("Item indisponível para aluguel. Status atual: " + item.getStatus());
            return null;
        }

        return item;
    }

    private void executarCadastroContrato(Cliente cliente, Item item, LocalDate dataRet, LocalDate dataDev, long quantidadeDias) {
        double valorTotal = quantidadeDias * item.getTaxaDiaria();
        int idContrato = sistema.gerarProximoIdContrato();

        ContratoAluguel contrato = new ContratoAluguel(
                idContrato, cliente, item, dataRet.toString(), dataDev.toString(), valorTotal);

        if (!sistema.cadastrarContrato(contrato)) {
            erro("Erro ao registrar aluguel.");
            return;
        }

        item.alugar();
        info("Aluguel registrado com sucesso! ID do Contrato: " + idContrato);
        listarContratos();
    }

    @FXML
    private void buscarContrato() {
        pedirInteiro(ID_CONTRATO).ifPresent(this::mostraContrato);
    }

    private void fazPesquisa(String pesquisado) {
        if (pesquisado == null || pesquisado.trim().isEmpty()) {
            listarContratos();
            return;
        }
        List<ContratoAluguel> filtrados = sistema.filtrarContratos(pesquisado);
        if (filtrados != null) {
            tabela.setItems(FXCollections.observableArrayList(filtrados));
        }
    }

    @FXML
    private void listarContratos() {
        if (sistema == null || tabela == null) return;
        List<ContratoAluguel> contratos = sistema.listarContratos();
        if (contratos != null) {
            tabela.setItems(FXCollections.observableArrayList(contratos));
        }
    }

    @FXML
    private void finalizarContrato() {
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
            listarContratos();
        } else {
            erro("Erro ao finalizar contrato");
        }
    }

    @FXML
    private void cancelarContrato() {
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
            listarContratos();
        } else {
            erro("Erro ao cancelar contrato.");
        }
    }

    private ContratoAluguel obterContratoAlvo() {
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

    private void mostraContrato(int id) {
        ContratoAluguel contrato = sistema.buscarContrato(id);
        if (contrato == null) {
            info(CONTRATO_NAO_ENCONTRADO);
            return;
        }
        tabela.getItems().setAll(contrato);
    }
}