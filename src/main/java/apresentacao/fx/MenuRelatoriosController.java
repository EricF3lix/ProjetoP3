package apresentacao.fx;

import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MenuRelatoriosController extends MenuController {

    @FXML
    private TableView<ObservableList<String>> tabela;

    @FXML
    private Label lblTituloRelatorio;

    private String[] ultimosCabecalhos = new String[0];
    private String nomeRelatorioAtual = "Relatório do Sistema";

    @Override
    protected String getTitulo() {
        return "Menu Relatorios";
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
        List<List<String>> linhas = itens.stream()
                .map(i -> Arrays.asList(
                        i.getCategoria().getNome(),
                        String.valueOf(i.getId()),
                        i.getNome(),
                        String.format("%.2f", i.getTaxaDiaria())))
                .toList();

        montarTabela("Itens por Categoria", new String[]{"CATEGORIA", "ID", "NOME", "TAXA"}, linhas);
    }

    @FXML
    private void historicoAlugueisCliente() {
        Optional<Integer> id = pedirInteiro("ID do cliente:");

        if (id.isEmpty()) {
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

        List<List<String>> linhas = historico.stream()
                .map(c -> Arrays.asList(
                        String.valueOf(c.getId()),
                        c.getItem().getNome(),
                        String.valueOf(c.getDataRetirada()),
                        String.valueOf(c.getDataDevolucaoPrevista()),
                        String.valueOf(c.getStatus()),
                        String.format("%.2f", c.getValorTotal()),
                        String.format("%.2f", c.getValorMulta())))
                .toList();

        montarTabela("Histórico do Cliente: " + usuario.getNome(),
                new String[]{"ID", "ITEM", "RETIRADA", "PREVISTA", "STATUS", "VALOR", "MULTA"}, linhas);
    }

    @FXML
    private void alugados() {
        List<ContratoAluguel> contratosAtivos = sistema.gerarRelatorioItensAlugados();

        if (contratosAtivos.isEmpty()) {
            erro("Não existem contratos ativos.");
            return;
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

        montarTabela("Itens Alugados Agora",
                new String[]{"ID", "CLIENTE", "ITEM", "DEVOLUÇÃO", "SITUAÇÃO"}, linhas);
    }

    @FXML
    private void relatorioPeriodo() {
        Optional<String> dataInicial = pedirData("Data inicial");
        if (dataInicial.isEmpty()) {
            return;
        }
        Optional<String> dataFinal = pedirData("Data final");
        if (dataFinal.isEmpty()) {
            return;
        }

        List<ContratoAluguel> contratosPeriodo =
                sistema.gerarRelatorioFaturamento(dataInicial.get(), dataFinal.get());

        if (contratosPeriodo.isEmpty()) {
            erro("Nenhum contrato encontrado no período.");
            return;
        }

        List<List<String>> linhas = contratosPeriodo.stream()
                .map(c -> Arrays.asList(
                        String.valueOf(c.getId()),
                        c.getCliente().getNome(),
                        String.format("%.2f", c.getValorTotal()),
                        String.format("%.2f", c.getValorMulta()),
                        c.isAtivo() ? "Sim" : "Não"))
                .toList();

        montarTabela("Faturamento (" + dataInicial.get() + " até " + dataFinal.get() + ")",
                new String[]{"ID", "CLIENTE", "ALUGUEL", "MULTA", "ATIVO"}, linhas);
    }

    // ==========================================
    // PRÉ-VISUALIZAÇÃO E EXPORTAÇÃO NATIVA
    // ==========================================

    @FXML
    private void abrirPreviaExportacao() {
        if (tabela.getItems().isEmpty()) {
            erro("Selecione e gere um relatório antes de exportar.");
            return;
        }

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Pré-visualização do Relatório");

        VBox raiz = new VBox(16);
        raiz.setPadding(new Insets(24));
        raiz.setStyle("-fx-background-color: #F5F5F7;");
        raiz.setAlignment(Pos.TOP_CENTER);

        // Cartão estilo folha A4 com prévia
        VBox folha = new VBox(12);
        folha.setPadding(new Insets(24));
        folha.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);");
        folha.setPrefWidth(650);

        Label lblTitulo = new Label(nomeRelatorioAtual);
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1D1D1F;");

        String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Label lblData = new Label("Emitido em: " + dataHoje + " • Total de registros: " + tabela.getItems().size());
        lblData.setStyle("-fx-font-size: 12px; -fx-text-fill: #86868B;");

        // Clone da tabela para a prévia visual
        TableView<ObservableList<String>> tabelaPrevia = new TableView<>();
        tabelaPrevia.setPrefHeight(300);
        tabelaPrevia.setStyle("-fx-background-radius: 8px; -fx-border-color: #E5E5EA;");

        for (int i = 0; i < ultimosCabecalhos.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(ultimosCabecalhos[i]);
            col.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(idx)));
            tabelaPrevia.getColumns().add(col);
        }
        tabelaPrevia.setItems(tabela.getItems());

        folha.getChildren().addAll(lblTitulo, lblData, new Separator(), tabelaPrevia);

        // Barra inferior com botões de ação
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

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Documento PDF (*.pdf)", "*.pdf")
        );

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

            // Cabeçalho da tabela com azul moderno
            Font fontCabecalho = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            for (String col : ultimosCabecalhos) {
                PdfPCell cell = new PdfPCell(new Phrase(col, fontCabecalho));
                cell.setBackgroundColor(new Color(0, 122, 255));
                cell.setPadding(6);
                tabelaPdf.addCell(cell);
            }

            // Linhas
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

            escrever("Relatório exportado com sucesso em: " + arquivoDestino.getName());

        } catch (Exception e) {
            erro("Falha ao gerar o arquivo PDF: " + e.getMessage());
        }
    }
}