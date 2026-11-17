package apresentacao.fx;

import java.util.Optional;

import entidades.Categoria;
import javafx.collections.FXCollections;
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
import javafx.scene.layout.VBox;

public class MenuCategoriasController extends MenuController {

    private static final String ID_CATEGORIA = "Digite o ID da categoria: ";
    private static final String NAO_ENCONTRADA = "Categoria não encontrada.";

    private static final String ESTILO_BADGE_COMUM =
            "-fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 10; -fx-font-size: 11px;";
    private static final String ESTILO_BADGE_DISPONIVEL =
            "-fx-background-color: #d4edda; -fx-text-fill: #155724; " + ESTILO_BADGE_COMUM;
    private static final String ESTILO_BADGE_INDISPONIVEL =
            "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; " + ESTILO_BADGE_COMUM;

    @FXML private TableView<Categoria> tabela;
    @FXML private TableColumn<Categoria, Integer> colID;
    @FXML private TableColumn<Categoria, String> colNome;
    @FXML private TableColumn<Categoria, String> colDescricao;
    @FXML private TableColumn<Categoria, Boolean> colAtiva;

    @FXML private ComboBox<String> filtro;

    @FXML private VBox dadosCategoria;
    @FXML private VBox tabelaCategoria;

    @FXML private Label tituloCategoria;
    @FXML private Label tituloTabela;

    @FXML private TextField txtNome;
    @FXML private TextField txtDescricao;

    @FXML private Button btnCadastrar;
    @FXML private Button btnAtualizar;

    private boolean listener = false;

    @Override
    protected String getTitulo() {
        return "Gerenciar Categorias";
    }

    @Override
    protected void aoExibir() {
        tabela.setPlaceholder(new Label("Nenhuma categoria cadastrada"));
        if (!listener) {
            pesquisa.textProperty().addListener((obj, antigo, novo) -> fazPesquisa(novo));
            this.listener = true;

            configurarColunas();
        }
        filtro.setValue("Todos");
        listarCategorias();
    }

    private void configurarColunas() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        colAtiva.setCellValueFactory(new PropertyValueFactory<>("ativa"));
        colAtiva.setCellFactory(c -> new TableCell<Categoria, Boolean>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(Boolean item, boolean vazio) {
                super.updateItem(item, vazio);
                if (vazio || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                boolean ativa = item;
                badge.setText(ativa ? "● Ativo" : "● Inativo");
                badge.setStyle(ativa ? ESTILO_BADGE_DISPONIVEL : ESTILO_BADGE_INDISPONIVEL);
                setAlignment(Pos.CENTER);
                setGraphic(badge);
                setText(null);
            }
        });
    }

    private void tiraTabela() {
        tabelaCategoria.setVisible(false);
        tabelaCategoria.setManaged(false);

        dadosCategoria.setVisible(true);
        dadosCategoria.setManaged(true);
    }

    private void colocaTabela() {
        tabelaCategoria.setVisible(true);
        tabelaCategoria.setManaged(true);

        dadosCategoria.setVisible(false);
        dadosCategoria.setManaged(false);
    }

    @FXML private void trocaCadastro() {
        tiraTabela();

        tituloCategoria.setText("Cadastrar Categoria");

        btnCadastrar.setVisible(true);
        btnCadastrar.setManaged(true);

        btnAtualizar.setVisible(false);
        btnAtualizar.setManaged(false);
        btnAtualizar.setUserData(null);

        txtNome.clear();
        txtDescricao.clear();
    }

    @FXML private void trocaAtualizar() {
        Categoria alvo = obterCategoriaAlvo();
        if (alvo == null) return;

        tiraTabela();
        tituloCategoria.setText("Atualizar Categoria");

        btnCadastrar.setVisible(false);
        btnCadastrar.setManaged(false);
        btnAtualizar.setVisible(true);
        btnAtualizar.setManaged(true);
        btnAtualizar.setUserData(alvo);

        txtNome.setText(alvo.getNome());
        txtDescricao.setText(alvo.getDescricao());
    }

    @FXML private void cadastrarCategoria() {
        String nome = txtNome.getText().trim();
        String descricao = txtDescricao.getText().trim();

        if (nome.isEmpty() || descricao.isEmpty()) {
        	notificarPopUpErro("Preencha todos os campos!");
            return;
        }

        int id = sistema.gerarProximoIdCategoria();
        Categoria categoria = new Categoria(id, nome, descricao);

        if (sistema.cadastrarCategoria(categoria)) {
        	notificarPopUp("Categoria cadastrada com sucesso! ID: " + id);
            txtNome.clear();
            txtDescricao.clear();
            listarCategorias();
        } else {
        	notificarPopUpErro("Já existe uma categoria com esse nome");
        }
    }

    @FXML private void atualizarCategoria() {
        Categoria atual = (Categoria) btnAtualizar.getUserData();
        if (atual == null) return;

        String nome = txtNome.getText().trim();
        String descricao = txtDescricao.getText().trim();

        if (nome.isEmpty() || descricao.isEmpty()) {
        	notificarPopUpErro("Preencha todos os campos!");
            return;
        }

        if (sistema.atualizarCategoria(new Categoria(atual.getId(), nome, descricao))) {
        	notificarPopUp("Categoria atualizada com sucesso!");
            btnAtualizar.setUserData(null);
            txtNome.clear();
            txtDescricao.clear();
            listarCategorias();
        } else {
        	notificarPopUpErro("Erro ao atualizar categoria.");
        }
    }

    @FXML private void desativarCategoria() {
        Categoria categoria = obterCategoriaAlvo();
        if (categoria == null) return;

        if (!confirmar("Tem certeza que deseja desativar \"" + categoria.getNome() + "\"?")) {
            return;
        }

        if (sistema.desativarCategoria(categoria.getId())) {
        	notificarPopUp("Categoria desativada com sucesso.");
            listarCategorias();
        } else {
        	notificarPopUpErro("Erro ao desativar categoria.");
        }
    }

    @FXML private void buscarCategoria() {
        pedirInteiro(ID_CATEGORIA).ifPresent(id -> {
            Categoria c = sistema.buscarCategoria(id);
            if (c != null) {
                pesquisa.setText(String.valueOf(id));
            } else {
            	notificarPopUpErro(NAO_ENCONTRADA);
            }
        });
    }

    @FXML private void listarCategorias() {
        colocaTabela();
        tabela.setItems(FXCollections.observableArrayList(sistema.listarCategorias()));
        tabela.refresh();
    }

    private void fazPesquisa(String pesquisado) {
        colocaTabela();

        if (filtro.getValue() == null) {
            filtro.getSelectionModel().selectFirst();
        }

        switch (filtro.getValue()) {
            case "Todos" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarCategoriaPorTodos(pesquisado)));
            case "ID" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarCategoriaPorId(pesquisado)));
            case "Nome" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarCategoriaPorNome(pesquisado)));
            case "Descrição" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarCategoriaPorDescricao(pesquisado)));
            default -> filtro.getSelectionModel().selectFirst();
        }
    }

    @FXML private void aoLimparPesquisa() {
        colocaTabela();
        pesquisa.clear();
        filtro.getSelectionModel().clearSelection();
    }

    private Categoria obterCategoriaAlvo() {
        Categoria atual = tabela.getSelectionModel().getSelectedItem();
        tabela.getSelectionModel().clearSelection();

        if (atual == null) {
            Optional<Integer> id = pedirInteiro(ID_CATEGORIA);
            if (id.isEmpty()) return null;
            atual = sistema.buscarCategoria(id.get());
            if (atual == null) {
            	notificarPopUpErro(NAO_ENCONTRADA);
            }
        }
        return atual;
    }
}