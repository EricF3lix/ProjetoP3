package apresentacao.fx;

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
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class MenuItensController extends MenuController {

    private static final String PEDIR_ID_ITEM = "Digite o ID do item:";
    private static final String ESTILO_BADGE_COMUM =
            "-fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 10; -fx-font-size: 11px;";
    private static final String ESTILO_BADGE_DISPONIVEL =
            "-fx-background-color: #d4edda; -fx-text-fill: #155724; " + ESTILO_BADGE_COMUM;
    private static final String ESTILO_BADGE_INDISPONIVEL =
            "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; " + ESTILO_BADGE_COMUM;
    private static final String FILTRO_TODOS = "Todos";

    @FXML
    private ComboBox<String> cbFiltro;

    @FXML
    private TableView<Item> tabela;

    @FXML
    private TableColumn<Item, Boolean> colAtivo;

    @FXML
    private TextField nomeItem;
    @FXML
    private TextField descricaoItem;
    @FXML
    private TextField taxaDiaria;
    @FXML
    private TextField valorReposicao;
    @FXML
    private ComboBox<String> estadoItem;

    @FXML
    private ComboBox<String> categoriaItem;
    @FXML
    private ComboBox<String> fornecedoresItem;


    @FXML
    private TextField nomeItem1;
    @FXML
    private TextField descricaoItem1;
    @FXML
    private TextField taxaDiaria1;
    @FXML
    private TextField valorReposicao1;
    @FXML
    private ComboBox<String> estadoItem1;

    @FXML
    private ComboBox<String> categoriaItem1;
    @FXML
    private ComboBox fornecedoresItem1;

    @FXML
    private VBox dadosItens;
    @FXML
    private VBox tabelaItens;
    @FXML
    private VBox dadosItens1;

    private final ObservableList<Item> listaItens = FXCollections.observableArrayList();
    private FilteredList<Item> listaFiltrada;
    private Item itemEmEdicao;

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
                badge.setText(disponivel ? "● Ativo" : "● Inativo");
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
        mostrarTabela();
    }

    @FXML
    private void mostrarTabela() {
        tabelaItens.setVisible(true);
        dadosItens.setVisible(false);
        dadosItens1.setVisible(false);
    }

    @FXML
    private void esconderTabela() {
        tabelaItens.setVisible(false);
        dadosItens.setVisible(true);
        dadosItens1.setVisible(false);

        ObservableList<String> nomes = FXCollections.observableArrayList();
        List<Categoria> categorias = sistema.listarCategorias();
        for (Categoria c : categorias) {
            nomes.add(c.getNome());
        }
        categoriaItem.setItems(nomes);

        ObservableList<String> listanomes = FXCollections.observableArrayList();
        List<Fornecedor> fornecedores = sistema.listarFornecedores();
        for (Fornecedor f : fornecedores) {
            listanomes.add(f.getRazaoSocial());
        }
        fornecedoresItem.setItems(listanomes);

        ObservableList<String> estados = FXCollections.observableArrayList("Novo","Seminovo","Usado","Danificado");
        estadoItem.setItems(estados);

    }

    @FXML
    private void esconderTabela1() {
        tabelaItens.setVisible(false);
        dadosItens1.setVisible(true);
        dadosItens.setVisible(false);

        ObservableList<String> nomes = FXCollections.observableArrayList();
        for (Categoria c : sistema.listarCategorias()) {
            nomes.add(c.getNome());
        }
        categoriaItem1.setItems(nomes);

        ObservableList<String> listanomes1 = FXCollections.observableArrayList();
        List<Fornecedor> fornecedores1 = sistema.listarFornecedores();
        for (Fornecedor f : fornecedores1) {
            listanomes1.add(f.getRazaoSocial());
        }
        fornecedoresItem1.setItems(listanomes1);

        ObservableList<String> estados = FXCollections.observableArrayList("Novo","Seminovo","Usado","Danificado");
        estadoItem1.setItems(estados);
    }


    @FXML
    public void cadastrarItem(ActionEvent event) {

        String nomeitem = nomeItem.getText().trim();
        String descricaoitem = descricaoItem.getText().trim();
        String taxadiaria = (taxaDiaria.getText().trim());
        String valorreposicao = (valorReposicao.getText().trim());
        String estado = estadoItem.getValue();
        String categoria = categoriaItem.getValue();
        String fornecedortxt = fornecedoresItem.getValue();

        if (nomeitem.isEmpty() || descricaoitem.isEmpty() || taxadiaria.isEmpty() || valorreposicao.isEmpty() || estado.isEmpty() || categoria == null || categoria.isEmpty() || fornecedortxt.isEmpty()) {
            erro("Preencha todos os campos");
            return;
        }

        double taxa = Double.parseDouble(taxadiaria.replace(',', '.'));
        double valor = Double.parseDouble(valorreposicao.replace(',', '.'));
        int id = sistema.gerarProximoIdItem();

        List<Categoria> categorias = sistema.buscarCategoriaPorNome(categoria);
        Categoria catgoriatxt = categorias.getFirst();

        List<Fornecedor> fornecedores = sistema.buscarPorNome(fornecedortxt);
        Fornecedor fornecedor = fornecedores.getFirst();

        Item item = new Item(id, nomeitem, descricaoitem, taxa, estado, valor, catgoriatxt, fornecedor);

        sistema.cadastrarItem(item);
        info("Item cadastrado com sucesso");

    }

    @FXML
    public void atualizarItem(ActionEvent event) {
        Item selecionado = obterItemParaAtualizar();
        if (selecionado == null) return;

        itemEmEdicao = selecionado;
        esconderTabela1();
        preencherCamposAtualizacao(selecionado);
    }

    private void preencherCamposAtualizacao(Item item) {
        nomeItem1.setText(item.getNome());
        descricaoItem1.setText(item.getDescricao());
        taxaDiaria1.setText(String.format("%.2f", item.getTaxaDiaria()).replace('.', ','));
        valorReposicao1.setText(String.format("%.2f", item.getValorReposicao()).replace('.', ','));
        estadoItem1.setValue(item.getEstadoConservacao());
        fornecedoresItem1.setValue(item.getFornecedor() != null ? String.valueOf(item.getFornecedor().getRazaoSocial()) : "");
        categoriaItem1.setValue(item.getCategoria() != null ? item.getCategoria().getNome() : null);
    }

    @FXML
    public void salvarAtualizacao(ActionEvent event) {
        if (itemEmEdicao == null) {
            erro("Nenhum item selecionado para atualização.");
            return;
        }

        String nome = nomeItem1.getText().trim();
        String descricao = descricaoItem1.getText().trim();
        String taxaTxt = taxaDiaria1.getText().trim();
        String valorTxt = valorReposicao1.getText().trim();
        String estado = estadoItem1.getValue();
        String categoriaNome = categoriaItem1.getValue();
        String fornecedorTxt = fornecedoresItem1.getValue().toString();

        if (nome.isEmpty() || descricao.isEmpty() || taxaTxt.isEmpty() || valorTxt.isEmpty()
                || estado.isEmpty() || categoriaNome == null || fornecedorTxt.isEmpty()) {
            erro("Preencha todos os campos");
            return;
        }

        double taxa;
        double valor;
        int idFornecedor;

        try {
            taxa = Double.parseDouble(taxaTxt.replace(',', '.'));
            valor = Double.parseDouble(valorTxt.replace(',', '.'));
        } catch (NumberFormatException e) {
            erro("Informe valores numéricos válidos para taxa, valor de reposição e fornecedor.");
            return;
        }

        List<Categoria> categorias = sistema.buscarCategoriaPorNome(categoriaNome);
        if (categorias.isEmpty()) {
            erro("Categoria não encontrada.");
            return;
        }

        Fornecedor fornecedor = sistema.buscarPorNome(fornecedorTxt).getFirst();
        if (fornecedor == null) {
            erro("Fornecedor não encontrado.");
            return;
        }

        Item atualizado = new Item(itemEmEdicao.getId(), nome, descricao, taxa, estado, valor, categorias.getFirst(), fornecedor);

        sistema.atualizarItem(atualizado);
        info("Item atualizado com sucesso");
        listarItens(event);
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

    @FXML
    public void desativarItem(ActionEvent event) {
        mostrarTabela();
        Item item = tabela.getSelectionModel().getSelectedItem();
        if (item == null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Desativar Item");
            dialog.setHeaderText("Desativar Item");
            dialog.setContentText(PEDIR_ID_ITEM);
            Optional<String> resultado = dialog.showAndWait();


            if (resultado.isPresent()) {
                try {
                    int id = Integer.parseInt(resultado.get().trim());

                    if (sistema.excluirItem(id)) {
                        mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Item desativado com sucesso!");
                        carregarTabela();
                    } else {
                        mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível desativar o item.");
                    }

                } catch (NumberFormatException e) {
                    mostrarAlerta(AlertType.ERROR, "Erro", "Digite um ID numérico válido.");
                } finally {
                    return;
                }
            }
        }

        if (sistema.excluirItem(item.getId())) {
            mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Item desativado com sucesso!");
            carregarTabela();
        } else {
            mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível desativar o item.");
        }

    }
    private void mostrarAlerta(AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}