package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Categoria;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class MenuCategoriasController extends MenuController {

    @FXML private TextField txtPesquisa;
    @FXML private TableView<Categoria> tabela;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNome;
    @FXML private TableColumn<Categoria, String> colDescricao;
    @FXML private TableColumn<Categoria, Boolean> colStatus;

    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private FilteredList<Categoria> listaFiltrada;

    @Override
    protected String getTitulo() {
        return "Gerenciar Categorias";
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

    private void configurarColunas() {
        if (colId != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colId.setStyle("-fx-alignment: CENTER;");
        }
        if (colNome != null) {
            colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
            colNome.setStyle("-fx-font-weight: bold; -fx-text-fill: #1c1c1e;");
        }
        if (colDescricao != null) {
            colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
            colDescricao.setStyle("-fx-text-fill: #48484a;");
        }
        if (colStatus != null) {
            colStatus.setCellValueFactory(new PropertyValueFactory<>("ativa"));
            colStatus.setCellFactory(col -> new TableCell<>() {
                private final Label badge = new Label();

                @Override
                protected void updateItem(Boolean ativa, boolean empty) {
                    super.updateItem(ativa, empty);
                    if (empty || ativa == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        if (ativa) {
                            badge.setText("● Ativa");
                            badge.setStyle(
                                "-fx-background-color: #d1fae5; -fx-text-fill: #065f46; " +
                                "-fx-font-weight: bold; -fx-padding: 3 9 3 9; " +
                                "-fx-background-radius: 10; -fx-font-size: 11px;"
                            );
                        } else {
                            badge.setText("● Inativa");
                            badge.setStyle(
                                "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; " +
                                "-fx-font-weight: bold; -fx-padding: 3 9 3 9; " +
                                "-fx-background-radius: 10; -fx-font-size: 11px;"
                            );
                        }
                        setAlignment(Pos.CENTER);
                        setGraphic(badge);
                        setText(null);
                    }
                }
            });
        }
    }

    private void configurarTabelaVazia() {
        if (tabela == null) return;

        VBox estadoVazio = new VBox(8);
        estadoVazio.setAlignment(Pos.CENTER);
        estadoVazio.setStyle("-fx-padding: 30px;");

        FontIcon icone = new FontIcon("fas-tags");
        icone.setIconSize(34);
        icone.setStyle("-fx-icon-color: #8e8e93;");

        Label titulo = new Label("Nenhuma categoria encontrada");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1c1c1e;");

        Label subtitulo = new Label("Utilize a barra de pesquisa ou cadastre uma nova categoria.");
        subtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #8e8e93;");

        estadoVazio.getChildren().addAll(icone, titulo, subtitulo);
        tabela.setPlaceholder(estadoVazio);
    }

    private void configurarFiltroPesquisa() {
        listaFiltrada = new FilteredList<>(listaCategorias, p -> true);

        if (txtPesquisa != null) {
            txtPesquisa.textProperty().addListener((obs, antigo, novo) -> {
                String termo = (novo == null) ? "" : novo.trim().toLowerCase();
                listaFiltrada.setPredicate(cat -> {
                    if (cat == null) return false;
                    if (termo.isEmpty()) return true;

                    boolean bateuId = String.valueOf(cat.getId()).contains(termo);
                    boolean bateuNome = cat.getNome() != null && cat.getNome().toLowerCase().contains(termo);
                    boolean bateuDesc = cat.getDescricao() != null && cat.getDescricao().toLowerCase().contains(termo);

                    return bateuId || bateuNome || bateuDesc;
                });
            });
        }

        SortedList<Categoria> ordenada = new SortedList<>(listaFiltrada);
        if (tabela != null) {
            ordenada.comparatorProperty().bind(tabela.comparatorProperty());
            tabela.setItems(ordenada);
        }
    }

    private void carregarTabela() {
        if (sistema == null) return;
        List<Categoria> categorias = sistema.listarCategorias();
        if (categorias != null) {
            listaCategorias.setAll(categorias);
        } else {
            listaCategorias.clear();
        }
    }

    @FXML
    private void aoLimparPesquisa() {
        if (txtPesquisa != null) {
            txtPesquisa.clear();
        }
    }

    @FXML
    private void listarCategorias() {
        aoLimparPesquisa();
        carregarTabela();
        notificarPopUp("Lista de categorias recarregada.", true);
    }

    @FXML
    private void cadastrarCategoria() {
        if (sistema == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cadastrar Categoria");
        dialog.setHeaderText("Nova Categoria de Catálogo");

        ButtonType btnSalvar = new ButtonType("Cadastrar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtNome = new TextField();
        txtNome.setPromptText("Ex: Ferramentas Elétricas");

        TextField txtDesc = new TextField();
        txtDesc.setPromptText("Descrição da finalidade dos itens");

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(txtNome, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(txtDesc, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(txtNome::requestFocus);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isEmpty() || res.get() != btnSalvar) return;

        String nome = txtNome.getText().trim();
        String descricao = txtDesc.getText().trim();

        if (nome.isBlank() || descricao.isBlank()) {
            animarErroShake(tabela);
            notificarPopUp("Nome e descrição são campos obrigatórios!", false);
            return;
        }

        int id = sistema.gerarProximoIdCategoria();
        Categoria categoria = new Categoria(id, nome, descricao);

        if (sistema.cadastrarCategoria(categoria)) {
            notificarPopUp("Categoria '" + nome + "' cadastrada com sucesso!", true);
            carregarTabela();
        } else {
            animarErroShake(tabela);
            notificarPopUp("Já existe uma categoria cadastrada com esse nome.", false);
        }
    }

    @FXML
    private void atualizarCategoria() {
        if (tabela == null || sistema == null) return;

        Categoria selecionada = tabela.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            animarErroShake(tabela);
            notificarPopUp("Selecione uma categoria na tabela para editar.", false);
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Atualizar Categoria");
        dialog.setHeaderText("Editar Categoria: " + selecionada.getNome() + " (ID: " + selecionada.getId() + ")");

        ButtonType btnSalvar = new ButtonType("Salvar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtNome = new TextField(selecionada.getNome());
        TextField txtDesc = new TextField(selecionada.getDescricao());

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(txtNome, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(txtDesc, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(txtNome::requestFocus);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isEmpty() || res.get() != btnSalvar) return;

        String novoNome = txtNome.getText().trim();
        String novaDescricao = txtDesc.getText().trim();

        if (novoNome.isBlank() || novaDescricao.isBlank()) {
            animarErroShake(tabela);
            notificarPopUp("Nome e descrição não podem ficar vazios!", false);
            return;
        }

        if (sistema.atualizarCategoria(new Categoria(selecionada.getId(), novoNome, novaDescricao))) {
            notificarPopUp("Categoria atualizada com sucesso!", true);
            carregarTabela();
        } else {
            animarErroShake(tabela);
            notificarPopUp("Erro ao atualizar categoria.", false);
        }
    }

    @FXML
    private void desativarCategoria() {
        if (tabela == null || sistema == null) return;

        Categoria selecionada = tabela.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            animarErroShake(tabela);
            notificarPopUp("Selecione uma categoria na tabela para desativar.", false);
            return;
        }

        if (!confirmar("Tem certeza que deseja desativar a categoria \"" + selecionada.getNome() + "\"?")) {
            return;
        }

        if (sistema.desativarCategoria(selecionada.getId())) {
            notificarPopUp("Categoria desativada com sucesso.", true);
            carregarTabela();
        } else {
            animarErroShake(tabela);
            notificarPopUp("Não foi possível desativar a categoria.", false);
        }
    }
}