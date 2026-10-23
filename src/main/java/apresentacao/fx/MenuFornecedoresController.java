package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Fornecedor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
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

public class MenuFornecedoresController extends MenuController {

    private static final String ID = "Digite o ID do fornecedor: ";
    private static final String NAO_ENCONTRADO = "Fornecedor não encontrado.";
    
    @FXML private TableView<Fornecedor> tabela;
    private boolean listener = false;

    @Override
    protected String getTitulo() {
        return "Gerenciar Fornecedores";
    }

    @FXML
    public void initialize() {
        configurarResponsividade();
        configurarTabelaVazia();
        configurarColunas();
    }

    @Override
    protected void aoExibir() {
        aplicarEstiloTabela();
        if (!listener && pesquisa != null) {
            pesquisa.textProperty().addListener((obj, antigo, novo) -> fazPesquisa(novo));
            this.listener = true;
        }
        listarFornecedores();
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

    private void aplicarEstiloTabela() {
        if (tabela == null) return;
        boolean escuro = GerenciadorTema.isModoEscuro();
        if (escuro) {
            tabela.setStyle(
                "-fx-background-color: #0D0E12 !important; " +
                "-fx-control-inner-background: #0D0E12 !important; " +
                "-fx-control-inner-background-alt: #08090B !important; " +
                "-fx-table-cell-border-color: transparent transparent #161820 transparent !important; " +
                "-fx-border-color: #1F2229 !important; " +
                "-fx-border-radius: 10px; -fx-background-radius: 10px;"
            );
        } else {
            tabela.setStyle(
                "-fx-background-color: #FFFFFF; " +
                "-fx-control-inner-background: #FFFFFF; " +
                "-fx-control-inner-background-alt: #FAFAFC; " +
                "-fx-border-color: #E5E7EB; " +
                "-fx-border-radius: 10px; -fx-background-radius: 10px;"
            );
        }
        tabela.refresh();
    }

    private void configurarColunas() {
        if (tabela == null) return;

        for (TableColumn<Fornecedor, ?> col : tabela.getColumns()) {
            String titulo = col.getText() != null ? col.getText().trim() : "";

            if (titulo.equalsIgnoreCase("ID")) {
                col.setMinWidth(45);
                col.setMaxWidth(65);
                col.setStyle("-fx-alignment: CENTER;");
            } 
            else if (titulo.equalsIgnoreCase("Ativo")) {
                @SuppressWarnings("unchecked")
                TableColumn<Fornecedor, Object> colAtivo = (TableColumn<Fornecedor, Object>) col;
                colAtivo.setMinWidth(110);
                colAtivo.setPrefWidth(115);

                colAtivo.setCellFactory(c -> new TableCell<>() {
                    private final Label badge = new Label();

                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            boolean ativo = Boolean.parseBoolean(item.toString()) 
                                    || "sim".equalsIgnoreCase(item.toString()) 
                                    || "true".equalsIgnoreCase(item.toString());

                            boolean escuro = GerenciadorTema.isModoEscuro();

                            if (escuro) {
                                if (ativo) {
                                    badge.setText("● Ativo");
                                    badge.setStyle(
                                        "-fx-background-color: rgba(16, 185, 129, 0.20) !important; " +
                                        "-fx-text-fill: #34D399 !important; " +
                                        "-fx-border-color: rgba(52, 211, 153, 0.50) !important; " +
                                        "-fx-border-radius: 10px; -fx-background-radius: 10px; " +
                                        "-fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8 3 8;"
                                    );
                                } else {
                                    badge.setText("● Inativo");
                                    badge.setStyle(
                                        "-fx-background-color: rgba(239, 68, 68, 0.20) !important; " +
                                        "-fx-text-fill: #F87171 !important; " +
                                        "-fx-border-color: rgba(248, 113, 113, 0.50) !important; " +
                                        "-fx-border-radius: 10px; -fx-background-radius: 10px; " +
                                        "-fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8 3 8;"
                                    );
                                }
                            } else {
                                if (ativo) {
                                    badge.setText("● Ativo");
                                    badge.setStyle(
                                        "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; " +
                                        "-fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8 3 8; " +
                                        "-fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-color: #A7F3D0;"
                                    );
                                } else {
                                    badge.setText("● Inativo");
                                    badge.setStyle(
                                        "-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; " +
                                        "-fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8 3 8; " +
                                        "-fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-color: #FECACA;"
                                    );
                                }
                            }

                            setAlignment(Pos.CENTER);
                            setGraphic(badge);
                            setText(null);
                        }
                    }
                });
            }
        }
    }

    private void configurarTabelaVazia() {
        if (tabela == null) return;

        VBox estadoVazio = new VBox(8);
        estadoVazio.setAlignment(Pos.CENTER);
        estadoVazio.setStyle("-fx-padding: 30px;");

        FontIcon icone = new FontIcon("fas-truck");
        icone.setIconSize(32);
        icone.setStyle("-fx-icon-color: #8e8e93;");

        Label titulo = new Label("Nenhum fornecedor encontrado");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        Label subtitulo = new Label("Utilize o campo de busca acima ou cadastre um novo parceiro.");
        subtitulo.getStyleClass().add("label-subtitulo");
        subtitulo.setStyle("-fx-font-size: 13px;");

        estadoVazio.getChildren().addAll(icone, titulo, subtitulo);
        tabela.setPlaceholder(estadoVazio);
    }

    @FXML
    private void cadastrarFornecedor() {
        if (sistema == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cadastrar Fornecedor");
        dialog.setHeaderText("Informe os dados da empresa parceira");

        ButtonType btnSalvar = new ButtonType("Cadastrar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtNome = new TextField();
        txtNome.setPromptText("Razão Social / Nome Fantasia");

        TextField txtCnpj = new TextField();
        txtCnpj.setPromptText("CNPJ da empresa");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("contato@empresa.com");

        TextField txtTelefone = new TextField();
        txtTelefone.setPromptText("(00) 00000-0000");

        grid.add(new Label("Razão Social:"), 0, 0);
        grid.add(txtNome, 1, 0);
        grid.add(new Label("CNPJ:"), 0, 1);
        grid.add(txtCnpj, 1, 1);
        grid.add(new Label("E-mail:"), 0, 2);
        grid.add(txtEmail, 1, 2);
        grid.add(new Label("Telefone:"), 0, 3);
        grid.add(txtTelefone, 1, 3);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(txtNome::requestFocus);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isEmpty() || res.get() != btnSalvar) {
            return;
        }

        String nome = txtNome.getText().trim();
        String cnpj = txtCnpj.getText().trim();
        String email = txtEmail.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isBlank() || cnpj.isBlank() || email.isBlank() || telefone.isBlank()) {
            animarErroShake(tabela);
            notificarPopUp("Todos os campos são obrigatórios e não podem ficar em branco!", false);
            return;
        }

        int id = sistema.gerarProximoIdFornecedor();
        Fornecedor fornecedor = new Fornecedor(id, nome, cnpj, email, telefone);

        if (sistema.cadastrarFornecedor(fornecedor)) {
            notificarPopUp("Fornecedor cadastrado com sucesso! ID: " + id, true);
            listarFornecedores();
        } else {
            animarErroShake(tabela);
            notificarPopUp("Fornecedor ou CNPJ já cadastrado no sistema!", false);
        }
    }

    @FXML
    private void buscarFornecedor() {
        Optional<Integer> idFornecedor = pedirInteiro(ID);
        if (idFornecedor.isEmpty()) return;

        mostraFornecedor(idFornecedor.get());
    }

    private void fazPesquisa(String pesquisado) {
        if (sistema == null || tabela == null) return;
        List<Fornecedor> fornecedoresFiltrado = sistema.filtrarFornecedor(pesquisado);
        if (fornecedoresFiltrado == null || fornecedoresFiltrado.isEmpty()) {
            tabela.getItems().clear();
            return;
        }
        tabela.setItems(FXCollections.observableArrayList(fornecedoresFiltrado));
    }

    @FXML
    private void listarFornecedores() {
        if (sistema == null || tabela == null) return;
        List<Fornecedor> listaFornecedores = sistema.listarFornecedores();

        if (listaFornecedores == null || listaFornecedores.isEmpty()) {
            tabela.getItems().clear();
            return;
        }

        tabela.setItems(FXCollections.observableArrayList(listaFornecedores));
    }

    @FXML
    private void atualizarFornecedor() {
        Fornecedor atual = obterFornecedorAlvo();
        if (atual == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Atualizar Fornecedor");
        dialog.setHeaderText("Editar dados de: " + atual.getRazaoSocial() + " (ID: " + atual.getId() + ")");

        ButtonType btnSalvar = new ButtonType("Salvar Alterações", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtNome = new TextField(atual.getRazaoSocial());
        TextField txtCnpj = new TextField(atual.getCnpj());
        TextField txtEmail = new TextField(atual.getEmail());
        TextField txtTelefone = new TextField(atual.getTelefone());

        grid.add(new Label("Razão Social:"), 0, 0);
        grid.add(txtNome, 1, 0);
        grid.add(new Label("CNPJ:"), 0, 1);
        grid.add(txtCnpj, 1, 1);
        grid.add(new Label("E-mail:"), 0, 2);
        grid.add(txtEmail, 1, 2);
        grid.add(new Label("Telefone:"), 0, 3);
        grid.add(txtTelefone, 1, 3);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(txtNome::requestFocus);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isEmpty() || res.get() != btnSalvar) {
            return;
        }

        String novoNome = txtNome.getText().trim();
        String novoCnpj = txtCnpj.getText().trim();
        String novoEmail = txtEmail.getText().trim();
        String novoTelefone = txtTelefone.getText().trim();

        if (novoNome.isBlank() || novoCnpj.isBlank() || novoEmail.isBlank() || novoTelefone.isBlank()) {
            animarErroShake(tabela);
            notificarPopUp("Nenhum campo pode ficar em branco!", false);
            return;
        }

        if (sistema.atualizarFornecedor(new Fornecedor(atual.getId(), novoNome, novoCnpj, novoEmail, novoTelefone))) {
            notificarPopUp("Fornecedor atualizado com sucesso!", true);
            listarFornecedores();
        } else {
            animarErroShake(tabela);
            notificarPopUp("Erro ao atualizar dados do fornecedor.", false);
        }
    }

    @FXML
    private void desativarFornecedor() {
        Fornecedor fornecedor = obterFornecedorAlvo();
        if (fornecedor == null) return;

        Optional<Integer> id = Optional.of(fornecedor.getId());

        if (!confirmar("Tem certeza que deseja desativar \"" + fornecedor.getRazaoSocial() + "\"?")) {
            return;
        }

        if (sistema.desativarFornecedor(id.get())) {
            notificarPopUp("Fornecedor desativado com sucesso.", true);
            listarFornecedores();
        } else {
            animarErroShake(tabela);
            notificarPopUp("Erro ao desativar fornecedor.", false);
        }
    }

    private Fornecedor obterFornecedorAlvo() {
        Fornecedor atual = tabela.getSelectionModel().getSelectedItem();
        tabela.getSelectionModel().clearSelection();

        if (atual == null) {
            Optional<Integer> id = pedirInteiro(ID);
            if (id.isEmpty()) return null;
            atual = sistema.buscarFornecedor(id.get());
            if (atual == null) {
                animarErroShake(tabela);
                notificarPopUp(NAO_ENCONTRADO, false);
            }
        }
        return atual;
    }

    private void mostraFornecedor(int id) {
        Fornecedor fornecedor = sistema.buscarFornecedor(id);
        if (fornecedor == null) {
            animarErroShake(tabela);
            notificarPopUp("Fornecedor não cadastrado.", false);
            return;
        }

        tabela.getItems().setAll(fornecedor);
    }
}