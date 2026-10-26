package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Categoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();

    @Override
    protected String getTitulo() {
        return "Gerenciar Categorias";
    }

    @Override
    protected void aoExibir() {
        configurarColunas();
        configurarFiltroPesquisa();
        carregarTabela();
    }

    private void carregarTabela() {
        if (sistema != null && tabela != null) {
            List<Categoria> categorias = sistema.listarCategorias();
            if (categorias != null) {
                listaCategorias.setAll(categorias);
            }
        }
    }

    private void configurarColunas() {
        if (colID != null) colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colNome != null) colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        if (colDescricao != null) colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        if (colAtiva != null) {
            colAtiva.setCellValueFactory(new PropertyValueFactory<>("ativa"));
            colAtiva.setCellFactory(col -> criarCelulaAtiva());
        }
    }

    private TableCell<Categoria, Boolean> criarCelulaAtiva() {
        return new TableCell<>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(Boolean ativa, boolean empty) {
                super.updateItem(ativa, empty);

                if (empty || ativa == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
 
                boolean disponivel = ativa;
                badge.setText(disponivel ? "● Disponível" : "● Indisponível");
                badge.setStyle(disponivel ? ESTILO_BADGE_DISPONIVEL : ESTILO_BADGE_INDISPONIVEL);

                setAlignment(Pos.CENTER);
                setGraphic(badge);
                setText(null);
            }
        };
    }

    private void configurarFiltroPesquisa() {
        FilteredList<Categoria> listaFiltrada = new FilteredList<>(listaCategorias, p -> true);

        if (pesquisa != null) {
            pesquisa.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro(listaFiltrada, novo));
        }

        SortedList<Categoria> listaOrdenada = new SortedList<>(listaFiltrada);
        if (tabela != null) {
            tabela.setPlaceholder(new Label(""));
            listaOrdenada.comparatorProperty().bind(tabela.comparatorProperty());
            tabela.setItems(listaOrdenada);
        }
    }

    private void aplicarFiltro(FilteredList<Categoria> listaFiltrada, String termo) {
        if (listaFiltrada == null) return;

        if (termo == null || termo.trim().isEmpty()) {
            listaFiltrada.setPredicate(c -> true);
            return;
        }

        String termoFormatado = termo.trim().toLowerCase();
        listaFiltrada.setPredicate(c -> categoriaCorrespondeAoFiltro(c, termoFormatado));
    }

    private boolean categoriaCorrespondeAoFiltro(Categoria c, String termo) {
        if (c == null) return false;
        boolean bateuId = String.valueOf(c.getId()).contains(termo);
        boolean bateuNome = c.getNome() != null && c.getNome().toLowerCase().contains(termo);
        boolean bateuDesc = c.getDescricao() != null && c.getDescricao().toLowerCase().contains(termo);
        return bateuId || bateuNome || bateuDesc;
    }

    private Categoria obterCategoriaAlvo() {
        if (tabela != null && tabela.getSelectionModel().getSelectedItem() != null) {
            return tabela.getSelectionModel().getSelectedItem();
        }

        return pedirInteiro(ID_CATEGORIA)
                .map(id -> {
                    Categoria c = sistema.buscarCategoria(id);
                    if (c == null) erro(NAO_ENCONTRADA);
                    return c;
                })
                .orElse(null);
    }

    @FXML
    public void cadastrarCategoria(ActionEvent event) {
        Optional<String> nome = pedirTexto("Nome:");
        if (nome.isEmpty()) return;

        Optional<String> descricao = pedirTexto("Descrição:");
        if (descricao.isEmpty()) return;

        int id = sistema.gerarProximoIdCategoria();
        Categoria categoria = new Categoria(id, nome.get(), descricao.get());

        if (sistema.cadastrarCategoria(categoria)) {
            info("Categoria cadastrada com sucesso! ID: " + id);
            carregarTabela();
        } else {
            erro("Já existe uma categoria com esse nome");
        }
    }

    @FXML
    public void buscarCategoria(ActionEvent event) {
        pedirInteiro(ID_CATEGORIA).ifPresent(id -> {
            Categoria c = sistema.buscarCategoria(id);
            if (c != null) {
                if (pesquisa != null) pesquisa.setText(String.valueOf(id));
            } else {
                erro(NAO_ENCONTRADA);
            }
        });
    }

    @FXML
    public void listarCategorias(ActionEvent event) {
        if (pesquisa != null) pesquisa.clear();
        carregarTabela();
    }

    @FXML
    public void atualizarCategoria(ActionEvent event) {
        Categoria atual = obterCategoriaAlvo();
        if (atual == null) return;

        Optional<String> nome = pedirTexto("Novo nome:", atual.getNome());
        if (nome.isEmpty()) return;

        Optional<String> descricao = pedirTexto("Nova descrição:", atual.getDescricao());
        if (descricao.isEmpty()) return;

        String novoNome = nome.get().isBlank() ? atual.getNome() : nome.get().trim();
        String novaDescricao = descricao.get().isBlank() ? atual.getDescricao() : descricao.get().trim();

        Categoria categoriaAtualizada = new Categoria(atual.getId(), novoNome, novaDescricao);

        if (sistema.atualizarCategoria(categoriaAtualizada)) {
            info("Categoria atualizada com sucesso!");
            carregarTabela();
        } else {
            erro("Erro ao atualizar categoria.");
        }
    }

    @FXML
    public void desativarCategoria(ActionEvent event) {
        Categoria categoria = obterCategoriaAlvo();
        if (categoria == null) return;

        if (!confirmar("Tem certeza que deseja desativar \"" + categoria.getNome() + "\"?")) {
            return;
        }

        if (sistema.desativarCategoria(categoria.getId())) {
            info("Categoria desativada com sucesso.");
            carregarTabela();
        } else {
            erro("Erro ao desativar categoria.");
        }
    }
}