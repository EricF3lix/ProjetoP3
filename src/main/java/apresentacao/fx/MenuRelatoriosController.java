package apresentacao.fx;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

public class MenuRelatoriosController extends MenuController {

    private enum Parametro {
        CLIENTE, PERIODO
    }

    private static final String ESTILO_BASE = "-fx-background-color: #ffffff; -fx-background-radius: 10px; "
            + "-fx-border-color: #d1d1d6; -fx-border-radius: 10px; -fx-text-fill: #1d1d1f; "
            + "-fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 13px;";

    private static final String ESTILO_ATIVO = "-fx-background-color: #ffffff; -fx-background-radius: 10px; "
            + "-fx-border-color: #007AFF; -fx-border-radius: 10px; -fx-text-fill: #007AFF; "
            + "-fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 13px;";

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private TableView<ObservableList<String>> tabela;

    @FXML
    private Label lblTituloRelatorio;

    @FXML
    private Button btnCategoria;

    @FXML
    private Button btnHistorico;

    @FXML
    private Button btnAlugados;

    @FXML
    private Button btnPeriodo;

    @FXML
    private FlowPane barraParametros;

    @FXML
    private VBox grupoCliente;

    @FXML
    private VBox grupoInicial;

    @FXML
    private VBox grupoFinal;

    @FXML
    private ComboBox<String> cbCliente;

    @FXML
    private DatePicker dpInicial;

    @FXML
    private DatePicker dpFinal;

    private List<Cliente> clientes = new ArrayList<>();
    private Parametro parametroAtual;

    @FXML
    private Button relatorioadm;

    private String[] ultimosCabecalhos = new String[0];
    private String nomeRelatorioAtual = "Relatório do Sistema";

    @Override
    protected String getTitulo() {
        return "Menu Relatórios";
    }

    @FXML
    private void initialize() {
        configurarTabelaVazia("Escolha uma opção ao lado para visualizar os dados");

        configurarDatePicker(dpInicial);
        configurarDatePicker(dpFinal);

        LocalDate hoje = LocalDate.now(ZoneId.of("America/Recife"));
        dpInicial.setValue(hoje.withDayOfMonth(1));
        dpFinal.setValue(hoje);

        mostrarParametros(null);
    }

    private void configurarTabelaVazia(String mensagem) {
        if (tabela == null) return;

        VBox estadoVazio = new VBox(8);
        estadoVazio.setAlignment(Pos.CENTER);
        estadoVazio.setStyle("-fx-padding: 30px;");

        FontIcon icone = new FontIcon("fas-chart-bar");
        icone.setIconSize(34);
        icone.setStyle("-fx-icon-color: #8e8e93;");

        Label titulo = new Label("Painel de Consultas & Relatórios");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1c1c1e;");

        Label subtitulo = new Label(mensagem);
        subtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #8e8e93;");

        estadoVazio.getChildren().addAll(icone, titulo, subtitulo);
        tabela.setPlaceholder(estadoVazio);
    }

    private void configurarDatePicker(DatePicker datePicker) {
        if (datePicker == null) return;
        datePicker.setEditable(false);
        datePicker.setPromptText("Selecione a data");
        datePicker.getEditor().setStyle("-fx-background-color: transparent;");
        datePicker.getEditor().setOnMouseClicked(e -> {
            if (!datePicker.isShowing()) {
                datePicker.show();
            }
        });
    }

    private void mostrarParametros(Parametro parametro) {
        parametroAtual = parametro;
        exibir(barraParametros, parametro != null);
        exibir(grupoCliente, parametro == Parametro.CLIENTE);
        exibir(grupoInicial, parametro == Parametro.PERIODO);
        exibir(grupoFinal, parametro == Parametro.PERIODO);
    }

    private void exibir(Node no, boolean visivel) {
        if (no != null) {
            no.setVisible(visivel);
            no.setManaged(visivel);
        }
    }

    private void marcarAtivo(Button ativo) {
        if (btnCategoria != null) btnCategoria.setStyle(ESTILO_BASE);
        if (btnHistorico != null) btnHistorico.setStyle(ESTILO_BASE);
        if (btnAlugados != null) btnAlugados.setStyle(ESTILO_BASE);
        if (btnPeriodo != null) btnPeriodo.setStyle(ESTILO_BASE);
        if (ativo != null) ativo.setStyle(ESTILO_ATIVO);
    }

    private void limparTabela(String titulo, String aviso) {
        nomeRelatorioAtual = titulo;
        ultimosCabecalhos = new String[0];
        if (lblTituloRelatorio != null) {
            lblTituloRelatorio.setText(titulo);
        }
        tabela.getColumns().clear();
        tabela.getItems().clear();
        configurarTabelaVazia(aviso);
    }

    private void montarTabela(String titulo, String[] cabecalhos, List<List<String>> linhas) {
        this.nomeRelatorioAtual = titulo;
        this.ultimosCabecalhos = cabecalhos;

        if (lblTituloRelatorio != null) {
            lblTituloRelatorio.setText(titulo);
        }

        tabela.getColumns().clear();
        tabela.getItems().clear();

        for (int i = 0; i < cabecalhos.length; i++) {
            final int indice = i;
            TableColumn<ObservableList<String>, String> coluna = new TableColumn<>(cabecalhos[i]);
            coluna.setCellValueFactory(c ->
                    new SimpleStringProperty(c.getValue().get(indice)));

            coluna.setMinWidth(95);
            tabela.getColumns().add(coluna);
        }
        for (List<String> linha : linhas) {
            tabela.getItems().add(FXCollections.observableArrayList(linha));
        }

        notificarPopUp("Relatório carregado (" + linhas.size() + " registros)", true);
    }

    @FXML
    private void itensPorCategoria() {
        marcarAtivo(btnCategoria);
        mostrarParametros(null);

        List<Item> itens = sistema.gerarRelatorioItensDisponiveis();
        if (itens == null || itens.isEmpty()) {
            limparTabela("Itens por Categoria", "Nenhum item disponível registrado no sistema.");
            notificarPopUp("Nenhum item disponível encontrado.", false);
            return;
        }
        List<List<String>> linhas = itens.stream()
                .map(i -> Arrays.asList(
                        i.getCategoria() != null ? i.getCategoria().getNome() : "Sem categoria",
                        String.valueOf(i.getId()),
                        i.getNome(),
                        String.format("%.2f", i.getTaxaDiaria())))
                .toList();

        montarTabela("Itens por Categoria", new String[]{"CATEGORIA", "ID", "NOME", "TAXA DIÁRIA"}, linhas);
    }

    @FXML
    private void historicoAlugueisCliente() {
        marcarAtivo(btnHistorico);
        mostrarParametros(Parametro.CLIENTE);
        carregarClientes();
        limparTabela("Histórico de Aluguéis", "Selecione o cliente acima e clique em 'Gerar Relatório'.");
    }

    @FXML
    private void alugados() {
        marcarAtivo(btnAlugados);
        mostrarParametros(null);

        List<ContratoAluguel> contratosAtivos = sistema.gerarRelatorioItensAlugados();

        if (contratosAtivos == null || contratosAtivos.isEmpty()) {
            limparTabela("Itens Alugados Agora", "Não há contratos ou itens em locação ativa no momento.");
            notificarPopUp("Não existem contratos em locação ativa.", true);
            return;
        }

        LocalDate hoje = LocalDate.now(ZoneId.of("America/Recife"));
        List<List<String>> linhas = new ArrayList<>();

        for (ContratoAluguel c : contratosAtivos) {
            LocalDate dataPrevista = LocalDate.parse(c.getDataDevolucaoPrevista());
            String situacao = hoje.isAfter(dataPrevista) ? "ATRASADO" : "EM DIA";

            linhas.add(Arrays.asList(
                    String.valueOf(c.getId()),
                    c.getCliente() != null ? c.getCliente().getNome() : "N/D",
                    c.getItem() != null ? c.getItem().getNome() : "N/D",
                    c.getDataDevolucaoPrevista(),
                    situacao
            ));
        }

        montarTabela("Itens Alugados Agora",
                new String[]{"ID", "CLIENTE", "ITEM", "DEVOLUÇÃO PREVISTA", "SITUAÇÃO"}, linhas);
    }

    @FXML
    private void relatorioPeriodo() {
        marcarAtivo(btnPeriodo);
        mostrarParametros(Parametro.PERIODO);
        limparTabela("Faturamento por Período", "Defina o período acima e clique em 'Gerar Relatório'.");
    }

    @FXML
    private void gerarRelatorio() {
        if (parametroAtual == Parametro.CLIENTE) {
            gerarHistorico();
        } else if (parametroAtual == Parametro.PERIODO) {
            gerarPeriodo();
        }
    }

    private void carregarClientes() {
        clientes = sistema.listarUsuarios().stream()
                .filter(Cliente.class::isInstance)
                .map(Cliente.class::cast)
                .toList();

        cbCliente.getItems().setAll(clientes.stream().map(this::rotuloCliente).toList());
        cbCliente.getSelectionModel().clearSelection();
        cbCliente.setPromptText(clientes.isEmpty() ? "Nenhum cliente cadastrado" : "Selecione o cliente");
    }

    private String rotuloCliente(Cliente cliente) {
        return "[" + cliente.getId() + "] " + cliente.getNome();
    }

    private void gerarHistorico() {
        int posicao = cbCliente.getSelectionModel().getSelectedIndex();
        if (posicao < 0) {
            animarErroShake(cbCliente);
            notificarPopUp("Por favor, selecione um cliente da lista.", false);
            return;
        }

        Cliente cliente = clientes.get(posicao);
        List<ContratoAluguel> historico = sistema.gerarHistoricoCliente(cliente.getId());

        if (historico == null || historico.isEmpty()) {
            limparTabela("Histórico do Cliente: " + cliente.getNome(), "Este cliente não possui histórico de aluguéis.");
            notificarPopUp("Cliente sem histórico de aluguéis registrado.", true);
            return;
        }

        List<List<String>> linhas = historico.stream()
                .map(c -> Arrays.asList(
                        String.valueOf(c.getId()),
                        c.getItem() != null ? c.getItem().getNome() : "N/D",
                        String.valueOf(c.getDataRetirada()),
                        String.valueOf(c.getDataDevolucaoPrevista()),
                        String.valueOf(c.getStatus()),
                        String.format("%.2f", c.getValorTotal()),
                        String.format("%.2f", c.getValorMulta())))
                .toList();

        montarTabela("Histórico do Cliente: " + cliente.getNome(),
                new String[]{"ID", "ITEM", "RETIRADA", "PREVISTA", "STATUS", "VALOR (R$)", "MULTA (R$)"}, linhas);
    }

    private void gerarPeriodo() {
        LocalDate inicial = dpInicial.getValue();
        LocalDate fim = dpFinal.getValue();

        if (inicial == null || fim == null) {
            animarErroShake(barraParametros);
            notificarPopUp("Selecione a data inicial e a data final.", false);
            return;
        }

        if (inicial.isAfter(fim)) {
            animarErroShake(barraParametros);
            notificarPopUp("A data inicial não pode ser posterior à final.", false);
            return;
        }

        List<ContratoAluguel> contratosPeriodo =
                sistema.gerarRelatorioFaturamento(inicial.toString(), fim.toString());

        if (contratosPeriodo == null || contratosPeriodo.isEmpty()) {
            limparTabela("Faturamento por Período", "Nenhum contrato encontrado no intervalo selecionado.");
            notificarPopUp("Nenhum contrato no período especificado.", true);
            return;
        }

        List<List<String>> linhas = contratosPeriodo.stream()
                .map(c -> Arrays.asList(
                        String.valueOf(c.getId()),
                        c.getCliente() != null ? c.getCliente().getNome() : "N/D",
                        String.format("%.2f", c.getValorTotal()),
                        String.format("%.2f", c.getValorMulta()),
                        c.isAtivo() ? "Sim" : "Não"))
                .toList();

        montarTabela("Faturamento (" + inicial.format(FORMATO_DATA) + " até " + fim.format(FORMATO_DATA) + ")",
                new String[]{"ID", "CLIENTE", "ALUGUEL (R$)", "MULTA (R$)", "ATIVO"}, linhas);
    }

    @FXML
    private void abrirPreviaExportacao() {
        if (tabela.getItems().isEmpty()) {
            animarErroShake(tabela);
            notificarPopUp("Gere um relatório antes de realizar a exportação.", false);
            return;
        }

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Pré-visualização do Relatório");

        VBox raiz = new VBox(16);
        raiz.setPadding(new Insets(24));
        raiz.setStyle("-fx-background-color: #F5F5F7;");
        raiz.setAlignment(Pos.TOP_CENTER);

        VBox folha = new VBox(12);
        folha.setPadding(new Insets(24));
        folha.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);");
        folha.setPrefWidth(650);

        Label lblTitulo = new Label(nomeRelatorioAtual);
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1D1D1F;");

        String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Label lblData = new Label("Emitido em: " + dataHoje + " • Total de registros: " + tabela.getItems().size());
        lblData.setStyle("-fx-font-size: 12px; -fx-text-fill: #86868B;");

        TableView<ObservableList<String>> tabelaPrevia = new TableView<>();
        tabelaPrevia.setPrefHeight(300);
        tabelaPrevia.setStyle("-fx-background-radius: 8px; -fx-border-color: #E5E5EA;");

        for (int i = 0; i < ultimosCabecalhos.length; i++) {
            int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(ultimosCabecalhos[i]);
            col.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(idx)));
            tabelaPrevia.getColumns().add(col);
        }
        tabelaPrevia.setItems(tabela.getItems());

        folha.getChildren().addAll(lblTitulo, lblData, new Separator(), tabelaPrevia);

        HBox botoes = new HBox(12);
        botoes.setAlignment(Pos.CENTER_RIGHT);

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #E5E5EA; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 16;");
        btnCancelar.setOnAction(e -> modalStage.close());

        Button btnSalvar = new Button("Salvar como PDF...");
        btnSalvar.setStyle("-fx-background-color: #007AFF; -fx-background-radius: 8px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 16;");
        btnSalvar.setOnAction(e -> {
            modalStage.close();
            solicitarDestinoESalvarPDF();
        });

        botoes.getChildren().addAll(btnCancelar, btnSalvar);
        raiz.getChildren().addAll(folha, botoes);

        Scene scene = new Scene(raiz, 700, 480);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    private void solicitarDestinoESalvarPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório em PDF");

        String nomeArquivoSugerido = nomeRelatorioAtual.toLowerCase()
                .replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
        fileChooser.setInitialFileName(nomeArquivoSugerido);

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documento PDF (*.pdf)", "*.pdf"));

        Stage janelaPrincipal = (Stage) tabela.getScene().getWindow();
        File destino = fileChooser.showSaveDialog(janelaPrincipal);

        if (destino != null) {
            gerarArquivoPDF(destino);
        }
    }

    private void gerarArquivoPDF(File arquivoDestino) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(arquivoDestino));
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            document.add(new Paragraph(nomeRelatorioAtual, fontTitulo));

            Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            document.add(new Paragraph("Gerado em: " + dataHoje + " | Total de registros: " + tabela.getItems().size() + "\n\n", fontSub));

            PdfPTable tabelaPdf = new PdfPTable(ultimosCabecalhos.length);
            tabelaPdf.setWidthPercentage(100);

            Font fontCabecalho = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            for (String col : ultimosCabecalhos) {
                PdfPCell cell = new PdfPCell(new Phrase(col, fontCabecalho));
                cell.setBackgroundColor(new Color(0, 122, 255));
                cell.setPadding(6);
                tabelaPdf.addCell(cell);
            }

            Font fontCorpo = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            for (ObservableList<String> linha : tabela.getItems()) {
                for (String valor : linha) {
                    PdfPCell cell = new PdfPCell(new Phrase(valor != null ? valor : "", fontCorpo));
                    cell.setPadding(5);
                    tabelaPdf.addCell(cell);
                }
            }

            document.add(tabelaPdf);
            document.close();

            exibirSucessoExportacao(arquivoDestino);

        } catch (Exception e) {
            animarErroShake(tabela);
            notificarPopUp("Falha ao gerar o arquivo PDF.", false);
        }
    }

    private void exibirSucessoExportacao(File arquivoSalvo) {
        if (arquivoSalvo == null) return;

        notificarPopUp("PDF exportado com sucesso!", true);

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Exportação Concluída");
        alerta.setHeaderText("Relatório exportado com sucesso!");

        File pastaDestino = arquivoSalvo.getParentFile();
        String caminhoPasta = (pastaDestino != null) ? pastaDestino.getAbsolutePath() : System.getProperty("user.dir");

        alerta.setContentText("Arquivo: " + arquivoSalvo.getName() + "\nPasta: " + caminhoPasta);

        ButtonType btnAbrirPasta = new ButtonType("Abrir na Pasta");
        ButtonType btnConcluir = new ButtonType("Concluir", ButtonData.CANCEL_CLOSE);

        alerta.getButtonTypes().setAll(btnAbrirPasta, btnConcluir);

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnAbrirPasta) {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(pastaDestino != null ? pastaDestino : new File(System.getProperty("user.dir")));
                } else {
                    new ProcessBuilder("open", caminhoPasta).start();
                }
            } catch (Exception e) {
                notificarPopUp("Não foi possível abrir a pasta do relatório.", false);
            }
        }
    }
}