package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Categoria;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MenuCategoriasController extends MenuController {

    private static final String ID_CATEGORIA = "Digite o ID da categoria: ";
    private static final String NAO_ENCONTRADA = "Categoria não encontrada.";

    @FXML private TableView<Categoria> tabela;
    @FXML private TableColumn<Categoria, Integer> colID;
    @FXML private TableColumn<Categoria, String> colNome;
    @FXML private TableColumn<Categoria, String> colDescricao;
    @FXML private TableColumn<Categoria, Boolean> colAtiva;

    private boolean listener = false;

    @Override
    protected String getTitulo() {
        return "Gerenciar Categorias";
    }

    @Override
    protected void aoExibir() {
        tabela.setPlaceholder(new Label(""));

        if (!listener) {
            configurarColunas();
            if (pesquisa != null) {
                pesquisa.textProperty().addListener((obj, antigo, novo) -> fazPesquisa(novo));
            }
            this.listener = true;
        }

        listarCategorias();
    }

    private void configurarColunas() {
    colID.setCellValueFactory(new PropertyValueFactory<>("id"));
    colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
    colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
    colAtiva.setCellValueFactory(new PropertyValueFactory<>("ativa"));

    colAtiva.setCellFactory(col -> new TableCell<>() {
        private final Label badge = new Label();

        @Override
        protected void updateItem(Boolean ativa, boolean empty) {
            super.updateItem(ativa, empty);
            if (empty || ativa == null) {
                setGraphic(null);
            } else {
                if (ativa) {
                    badge.setText("• Disponível");
                    badge.setStyle("-fx-background-color: #E6F4EA; "
                                 + "-fx-text-fill: #137333; "
                                 + "-fx-background-radius: 12px; "
                                 + "-fx-padding: 3px 10px; "
                                 + "-fx-font-size: 12px; "
                                 + "-fx-font-weight: bold;");
                } else {
                    badge.setText("• Indisponível");
                    badge.setStyle("-fx-background-color: #FCE8E6; "
                                 + "-fx-text-fill: #C5221F; "
                                 + "-fx-background-radius: 12px; "
                                 + "-fx-padding: 3px 10px; "
                                 + "-fx-font-size: 12px; "
                                 + "-fx-font-weight: bold;");
                }
                setGraphic(badge);
                setAlignment(javafx.geometry.Pos.CENTER);
            }
        }
    });
}

    @FXML
    private void cadastrarCategoria() {
        Optional<String> nome = pedirTexto("Nome:");
        if (nome.isEmpty()) return;

        Optional<String> descricao = pedirTexto("Descrição:");
        if (descricao.isEmpty()) return;

        int id = sistema.gerarProximoIdCategoria();
        Categoria categoria = new Categoria(id, nome.get(), descricao.get());

        if (sistema.cadastrarCategoria(categoria)) {
            info("Categoria cadastrada com sucesso! ID: " + id);
            mostraCategoria(id);
        } else {
            erro("Já existe uma categoria com esse nome");
        }
    }

    @FXML
    private void buscarCategoria() {
        pedirInteiro(ID_CATEGORIA).ifPresent(this::mostraCategoria);
    }

    @FXML
    private void listarCategorias() {
        List<Categoria> categorias = sistema.listarCategorias();
        if (categorias.isEmpty()) {
            tabela.getItems().clear();
            return;
        }
        tabela.getItems().setAll(categorias);
    }

    @FXML
    private void atualizarCategoria() {
        Categoria atual = obterCategoriaAlvo();
        if (atual == null) return;

        Optional<String> nome = pedirTexto("Nome:", atual.getNome());
        if (nome.isEmpty()) return;

        Optional<String> descricao = pedirTexto("Descrição:", atual.getDescricao());
        if (descricao.isEmpty()) return;

        String novoNome = pegaNovo(nome.get(), atual.getNome());
        String novaDescricao = pegaNovo(descricao.get(), atual.getDescricao());

        if (sistema.atualizarCategoria(new Categoria(atual.getId(), novoNome, novaDescricao))) {
            info("Categoria atualizada com sucesso.");
            mostraCategoria(atual.getId());
        } else {
            erro("Erro ao atualizar categoria.");
        }
    }

    @FXML
    private void desativarCategoria() {
        Categoria categoria = obterCategoriaAlvo();
        if (categoria == null) return;

        if (!confirmar("Tem certeza que deseja desativar \"" + categoria.getNome() + "\"?")) {
            return;
        }

        if (sistema.desativarCategoria(categoria.getId())) {
            info("Categoria desativada com sucesso.");
            mostraCategoria(categoria.getId());
        } else {
            erro("Erro ao desativar categoria.");
        }
    }

    private void fazPesquisa(String pesquisado) {
        String termo = pesquisado == null ? "" : pesquisado.trim().toLowerCase();

        List<Categoria> filtradas = sistema.listarCategorias().stream()
                .filter(c -> termo.isEmpty()
                        || String.valueOf(c.getId()).contains(termo)
                        || (c.getNome() != null && c.getNome().toLowerCase().contains(termo))
                        || (c.getDescricao() != null && c.getDescricao().toLowerCase().contains(termo)))
                .toList();

        tabela.setItems(FXCollections.observableArrayList(filtradas));
    }

    private Categoria obterCategoriaAlvo() {
        Categoria selecionada = tabela.getSelectionModel().getSelectedItem();
        tabela.getSelectionModel().clearSelection();

        if (selecionada != null) {
            return selecionada;
        }

        Optional<Integer> id = pedirInteiro(ID_CATEGORIA);
        if (id.isEmpty()) return null;

        Categoria categoria = sistema.buscarCategoria(id.get());
        if (categoria == null) {
            erro(NAO_ENCONTRADA);
        }
        return categoria;
    }

    private void mostraCategoria(int id) {
        Categoria categoria = sistema.buscarCategoria(id);
        if (categoria == null) {
            info(NAO_ENCONTRADA);
            return;
        }

        tabela.getItems().setAll(categoria);
    }
}