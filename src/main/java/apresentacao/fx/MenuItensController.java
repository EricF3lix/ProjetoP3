package apresentacao.fx;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

public class MenuItensController extends MenuController {

    private static final String PEDIR_ID_ITEM = "Digite o ID do item:";
    private static final String ESTILO_BADGE_COMUM =
            "-fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 10; -fx-font-size: 11px;";
    private static final String ESTILO_BADGE_DISPONIVEL =
            "-fx-background-color: #d4edda; -fx-text-fill: #155724; " + ESTILO_BADGE_COMUM;
    private static final String ESTILO_BADGE_INDISPONIVEL =
            "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; " + ESTILO_BADGE_COMUM;
    private static final String FILTRO_TODOS = "Todos";
    private static final String SEPARADOR_OPCOES = " | Opções: ";
    private static final String NENHUM = "nenhum";
    private static final String INDISPONIVEL = "INDISPONIVEL";
    private static final List<String> STATUS_VALIDOS =
            List.of("DISPONIVEL", "MANUTENCAO", "ALUGADO", INDISPONIVEL);

    @FXML
    private ComboBox<String> cbFiltro;

    @FXML
    private TextField pesquisa;

    @FXML
    private Button btnLimparPesquisa;

    @FXML
    private TableView<Item> tabela;

    @FXML
    private TableColumn<Item, Boolean> colAtivo;

    private final ObservableList<Item> listaItens = FXCollections.observableArrayList();
    private FilteredList<Item> listaFiltrada;

    @Override
    public String getTitulo() {
        return "Gerenciar Itens";
    }

    @FXML
    public void initialize() {
        configurarFiltroPesquisa();
        configurarColunaAtivo();
    }

    @Override
    protected void aoExibir() {

        carregarTabela();
    }

    private void carregarTabela() {
        if (sistema != null && tabela != null) {
            List<Item> itens = sistema.listarItens();
            if (itens != null) {
                listaItens.setAll(itens);
            }
        }
    }

    private void configurarColunaAtivo() {
        if (colAtivo != null) {
            colAtivo.setCellFactory(col -> criarCelulaAtivo());
        }
    }

    private TableCell<Item, Boolean> criarCelulaAtivo() {
        return new TableCell<>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(Boolean ativo, boolean empty) {
                super.updateItem(ativo, empty);

                if (empty || ativo == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                boolean disponivel = Boolean.TRUE.equals(ativo);
                badge.setText(disponivel ? "● Disponível" : "● Indisponível");
                badge.setStyle(disponivel ? ESTILO_BADGE_DISPONIVEL : ESTILO_BADGE_INDISPONIVEL);

                setAlignment(Pos.CENTER);
                setGraphic(badge);
                setText(null);
            }
        };
    }

    private void configurarFiltroPesquisa() {
        if (cbFiltro != null) {
            cbFiltro.setItems(FXCollections.observableArrayList(FILTRO_TODOS, "Nome", "ID"));
            cbFiltro.setValue(FILTRO_TODOS);
            cbFiltro.valueProperty().addListener((obs, antigo, novo) ->
                aplicarFiltro(pesquisa != null ? pesquisa.getText() : "")
            );
        }

        listaFiltrada = new FilteredList<>(listaItens, p -> true);

        if (pesquisa != null) {
            pesquisa.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro(novo));
        }

        SortedList<Item> listaOrdenada = new SortedList<>(listaFiltrada);
        if (tabela != null) {
            listaOrdenada.comparatorProperty().bind(tabela.comparatorProperty());
            tabela.setItems(listaOrdenada);
        }
    }

    private void aplicarFiltro(String termo) {
        if (listaFiltrada == null) return;

        listaFiltrada.setPredicate(item -> {
            if (item == null) return false;
            if (termo == null || termo.trim().isEmpty()) {
                return true;
            }

            String termoFormatado = termo.trim().toLowerCase();
            String filtroSelecionado = cbFiltro != null && cbFiltro.getValue() != null ? cbFiltro.getValue() : FILTRO_TODOS;

            boolean bateuId = String.valueOf(item.getId()).contains(termoFormatado);
            boolean bateuNome = item.getNome() != null && item.getNome().toLowerCase().contains(termoFormatado);

            return switch (filtroSelecionado) {
                case "ID" -> bateuId;
                case "Nome" -> bateuNome;
                default -> bateuId || bateuNome;
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

    @FXML
    public void listarItens(ActionEvent event) {
        aoLimparPesquisa(event);
        carregarTabela();
    }

    @FXML
    public void buscarItem(ActionEvent event) {
        String texto = pesquisa != null ? pesquisa.getText().trim() : "";

        if (texto.isEmpty()) {
            Optional<Integer> idOpt = pedirInteiro(PEDIR_ID_ITEM);
            if (idOpt.isEmpty()) return;
            texto = String.valueOf(idOpt.get());
        }

        try {
            int id = Integer.parseInt(texto);
            Item item = sistema != null ? sistema.buscarItem(id) : null;

            if (item != null) {
                if (cbFiltro != null) cbFiltro.setValue("ID");
                if (pesquisa != null) pesquisa.setText(String.valueOf(id));
            } else {
                erro("Nenhum item encontrado com o ID " + id);
            }
        } catch (NumberFormatException e) {
            erro("Por favor, informe um ID numérico válido.");
        }
    }

    @FXML
    public void cadastrarItem(ActionEvent event) {
        Optional<String> nome = pedirTexto("Nome do item:");
        if (nome.isEmpty()) return;

        Optional<String> desc = pedirTexto("Descrição:");
        if (desc.isEmpty()) return;

        Optional<Double> taxa = pedirDouble("Taxa diária:");
        if (taxa.isEmpty()) return;

        Optional<String> estado = pedirTexto("Estado de conservação:");
        if (estado.isEmpty()) return;

        Optional<Double> valorRep = pedirDouble("Valor de reposição:");
        if (valorRep.isEmpty()) return;

        Optional<String> catInput = pedirTexto("Categoria (Opções: " + opcoesCategorias()
                + ")\n[Deixe em branco para sem categoria]:");
        if (catInput.isEmpty()) return;

        String termoCat = catInput.get();
        Categoria categoria = resolverCategoria(termoCat, null);
        if (referenciaInvalida(termoCat, categoria)) {
            erro("Categoria '" + termoCat + "' não encontrada. Cadastre-a primeiro ou deixe em branco.");
            return;
        }

        Optional<String> fornInput = pedirTexto("Fornecedor (Opções: " + opcoesFornecedores()
                + ")\n[Deixe em branco para sem fornecedor]:");
        if (fornInput.isEmpty()) return;

        String termoForn = fornInput.get();
        Fornecedor fornecedor = resolverFornecedor(termoForn, null);
        if (referenciaInvalida(termoForn, fornecedor)) {
            erro("Fornecedor '" + termoForn + "' não encontrado. Cadastre-o primeiro ou deixe em branco.");
            return;
        }

        int id = sistema.gerarProximoIdItem();
        Item novoItem = new Item(id, nome.get(), desc.get(), taxa.get(), estado.get(),
                valorRep.get(), categoria, fornecedor);

        if (sistema.cadastrarItem(novoItem)) {
            info("Item cadastrado com sucesso! ID: " + id);
            carregarTabela();
        } else {
            erro("Erro ao cadastrar o item.");
        }
    }

    @FXML
    public void atualizarItem(ActionEvent event) {
        Item atual = obterItemParaAtualizar();
        if (atual == null) return;

        Optional<String> nome = pedirTexto("Novo nome (atual: " + atual.getNome() + "):");
        if (nome.isEmpty()) return;

        Optional<String> catInput = pedirTexto("Categoria (Atual: " + nomeCategoria(atual)
                + SEPARADOR_OPCOES + opcoesCategorias() + ")\n[Enter mantém | Digite '" + NENHUM + "' para remover]:");
        if (catInput.isEmpty()) return;

        String termoCat = catInput.get();
        Categoria novaCategoria = resolverCategoria(termoCat, atual.getCategoria());
        if (referenciaInvalida(termoCat, novaCategoria)) {
            erro("Categoria '" + termoCat + "' não encontrada.");
            return;
        }

        Optional<String> fornInput = pedirTexto("Fornecedor (Atual: " + nomeFornecedor(atual)
                + SEPARADOR_OPCOES + opcoesFornecedores() + ")\n[Enter mantém | Digite '" + NENHUM + "' para remover]:");
        if (fornInput.isEmpty()) return;

        String termoForn = fornInput.get();
        Fornecedor novoFornecedor = resolverFornecedor(termoForn, atual.getFornecedor());
        if (referenciaInvalida(termoForn, novoFornecedor)) {
            erro("Fornecedor '" + termoForn + "' não encontrado.");
            return;
        }

        Optional<Double> taxa = pedirDouble("Nova taxa diária:", atual.getTaxaDiaria());
        if (taxa.isEmpty()) return;

        Optional<String> estado = pedirTexto("Novo estado (atual: " + atual.getEstadoConservacao() + "):");
        if (estado.isEmpty()) return;

        Optional<String> status = pedirNovoStatus(atual);
        if (status.isEmpty()) return;

        Item itemAtualizado = new Item(
                atual.getId(),
                pegaNovo(nome.get(), atual.getNome()),
                atual.getDescricao(),
                taxa.get(),
                pegaNovo(estado.get(), atual.getEstadoConservacao()),
                atual.getValorReposicao(),
                novaCategoria,
                novoFornecedor
        );
        itemAtualizado.setAtivo(atual.isAtivo());
        itemAtualizado.setStatus(status.get());

        if (sistema.atualizarItem(itemAtualizado)) {
            info("Item atualizado com sucesso!");
            carregarTabela();
        } else {
            erro("Erro ao atualizar o item.");
        }
    }

    private Item obterItemParaAtualizar() {
        Item selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado != null) return selecionado;

        Optional<Integer> id = pedirInteiro(PEDIR_ID_ITEM);
        if (id.isEmpty()) return null;

        Item item = sistema.buscarItem(id.get());
        if (item == null) {
            erro("Item não encontrado.");
        }
        return item;
    }

    private Optional<String> pedirNovoStatus(Item atual) {
        if (!atual.isAtivo()) {
            return Optional.of(INDISPONIVEL);
        }

        Optional<String> entrada = pedirTexto("Novo status (Atual: " + atual.getStatus()
                + SEPARADOR_OPCOES + String.join(", ", STATUS_VALIDOS) + ")\n[Enter mantém]:");
        if (entrada.isEmpty()) return Optional.empty();

        String status = entrada.get().toUpperCase();
        if (status.isEmpty()) return Optional.of(atual.getStatus());

        if (!STATUS_VALIDOS.contains(status)) {
            erro("Status inválido. Digite: " + String.join(", ", STATUS_VALIDOS) + ".");
            return Optional.empty();
        }
        return Optional.of(status);
    }

    @FXML
    public void desativarItem(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Desativar Item");
        dialog.setHeaderText("Desativar Item");
        dialog.setContentText(PEDIR_ID_ITEM);

        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            try {
                int id = Integer.parseInt(resultado.get().trim());
                if (sistema != null) {
                    boolean sucesso = sistema.excluirItem(id);
                    if (sucesso) {
                        mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Item desativado com sucesso!");
                        carregarTabela();
                    } else {
                        mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível desativar o item.");
                    }
                }
            } catch (NumberFormatException e) {
                mostrarAlerta(AlertType.ERROR, "Erro", "Digite um ID numérico válido.");
            }
        }
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private String opcoesCategorias() {
        List<Categoria> categorias = sistema.listarCategorias();
        if (categorias.isEmpty()) return "Nenhuma";

        return categorias.stream().map(this::rotuloCategoria).collect(Collectors.joining(", "));
    }

    private String opcoesFornecedores() {
        List<Fornecedor> fornecedores = sistema.listarFornecedores();
        if (fornecedores.isEmpty()) return "Nenhum";

        return fornecedores.stream().map(this::rotuloFornecedor).collect(Collectors.joining(", "));
    }

    private String rotuloCategoria(Categoria categoria) {
        return categoria.getId() + "-" + categoria.getNome();
    }

    private String rotuloFornecedor(Fornecedor fornecedor) {
        return fornecedor.getId() + "-" + fornecedor.getRazaoSocial();
    }

    private String nomeCategoria(Item item) {
        return item.getCategoria() != null ? item.getCategoria().getNome() : "Sem Categoria";
    }

    private String nomeFornecedor(Item item) {
        return item.getFornecedor() != null ? item.getFornecedor().getRazaoSocial() : "Sem Fornecedor";
    }

    private Categoria resolverCategoria(String termo, Categoria atual) {
        if (termo.isBlank()) return atual;
        if (termo.equalsIgnoreCase(NENHUM)) return null;
        return localizarCategoria(termo);
    }

    private Fornecedor resolverFornecedor(String termo, Fornecedor atual) {
        if (termo.isBlank()) return atual;
        if (termo.equalsIgnoreCase(NENHUM)) return null;
        return localizarFornecedor(termo);
    }

    private Categoria localizarCategoria(String termo) {
        return sistema.listarCategorias().stream()
                .filter(c -> corresponde(String.valueOf(c.getId()), c.getNome(), termo))
                .findFirst()
                .orElse(null);
    }

    private Fornecedor localizarFornecedor(String termo) {
        return sistema.listarFornecedores().stream()
                .filter(f -> corresponde(String.valueOf(f.getId()), f.getRazaoSocial(), termo))
                .findFirst()
                .orElse(null);
    }

    private boolean corresponde(String id, String nome, String termo) {
        return id.equals(termo) || (nome != null && nome.equalsIgnoreCase(termo));
    }

    private boolean referenciaInvalida(String termo, Object resolvido) {
        return !termo.isBlank() && !termo.equalsIgnoreCase(NENHUM) && resolvido == null;
    }
}