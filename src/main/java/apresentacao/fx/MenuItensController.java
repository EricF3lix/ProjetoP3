package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class MenuItensController extends MenuController {

    private static final String ID_ITEM = "Digite o ID do item: ";
    private static final String NAO_ENCONTRADO = "Item não encontrado.";
    private static final List<String> ESTADOS = List.of("NOVO", "BOM", "REGULAR", "RUIM");

    @FXML private TableView<Item> tabela;
    private boolean listener = false;

    private record Escolha<T>(T valor) {
    }

    @Override
    protected String getTitulo() {
        return ("Gerenciar Itens");
    }

    @Override
    protected void aoExibir() {
        tabela.setPlaceholder(new Label(""));
        if (!listener) {
            pesquisa.textProperty().addListener((obj, antigo, novo) -> fazPesquisa(novo));
            this.listener = true;
        }
    }

    @FXML
    private void cadastrarItem() {
        Optional<String> nome = pedirTexto("Nome:");
        if (nome.isEmpty()) return;

        Optional<String> descricao = pedirTexto("Descrição:");
        if (descricao.isEmpty()) return;

        Optional<Double> taxaDiaria = pedirDouble("Taxa diária (R$):");
        if (taxaDiaria.isEmpty()) return;

        Optional<Integer> estado = escolherPosicao("Estado de conservação:", ESTADOS, null);
        if (estado.isEmpty()) return;

        Optional<Double> valorReposicao = pedirDouble("Valor de reposição (R$):");
        if (valorReposicao.isEmpty()) return;

        Optional<Escolha<Categoria>> categoria = selecionarCategoria();
        if (categoria.isEmpty()) return;

        Optional<Escolha<Fornecedor>> fornecedor = selecionarFornecedor();
        if (fornecedor.isEmpty()) return;

        int id = sistema.gerarProximoIdItem();

        Item item = new Item(id, nome.get(), descricao.get(), taxaDiaria.get(),
                ESTADOS.get(estado.get()), valorReposicao.get(),
                categoria.get().valor(), fornecedor.get().valor());

        if (sistema.cadastrarItem(item)) {
            info("Item cadastrado com sucesso! ID: " + id);
            mostraItem(id);
        } else {
            erro("Erro ao cadastrar item!");
        }
    }

    @FXML
    private void buscarItem() {
        Optional<Integer> idItem = pedirInteiro(ID_ITEM);
        if (idItem.isEmpty()) {
            return;
        }

        mostraItem(idItem.get());
    }

    private void fazPesquisa(String pesquisado) {
        List<Item> itensFiltrados = sistema.filtrarItem(pesquisado);
        if (itensFiltrados.isEmpty()) {
            tabela.getItems().clear();
            return;
        }
        tabela.setItems(FXCollections.observableArrayList(itensFiltrados));
    }

    @FXML
    private void listarItens() {
        List<Item> listaItens = sistema.listarItens();

        if (listaItens.isEmpty()) {
            return;
        }

        tabela.setItems(FXCollections.observableArrayList(listaItens));
    }

    @FXML
    private void atualizarItem() {
        Item atual = obterItemAlvo();
        if (atual == null) return;

        Optional<Integer> id = Optional.of(atual.getId());

        Optional<String> nome = pedirTexto("Nome:", atual.getNome());
        if (nome.isEmpty()) return;

        Optional<String> descricao = pedirTexto("Descrição:", atual.getDescricao());
        if (descricao.isEmpty()) return;

        Optional<Double> taxaDiaria = pedirDouble("Taxa diária (R$):", atual.getTaxaDiaria());
        if (taxaDiaria.isEmpty()) return;

        Optional<Integer> estado = escolherPosicao("Estado de conservação:", ESTADOS,
                "(manter atual: " + atual.getEstadoConservacao() + ")");
        if (estado.isEmpty()) return;

        Optional<Double> valorReposicao = pedirDouble("Valor de reposição (R$):", atual.getValorReposicao());
        if (valorReposicao.isEmpty()) return;

        String novoNome = pegaNovo(nome.get(), atual.getNome());
        String novaDescricao = pegaNovo(descricao.get(), atual.getDescricao());
        String novoEstado = estado.get() < 0 ? atual.getEstadoConservacao() : ESTADOS.get(estado.get());

        Item itemNovo = new Item(id.get(), novoNome, novaDescricao, taxaDiaria.get(), novoEstado,
                valorReposicao.get(), atual.getCategoria(), atual.getFornecedor());

        if (sistema.atualizarItem(itemNovo)) {
            info("Item atualizado com sucesso.");
            mostraItem(id.get());
        } else {
            erro("Erro ao atualizar item.");
        }
    }

    @FXML
    private void desativarItem() {
        Item item = obterItemAlvo();
        if (item == null) return;

        Optional<Integer> id = Optional.of(item.getId());

        if (!confirmar("Tem certeza que deseja desativar \"" + item.getNome() + "\"?")) {
            return;
        }

        if (sistema.excluirItem(id.get())) {
            info("Item desativado com sucesso");
            mostraItem(id.get());
        } else {
            erro("Erro ao desativar item (não encontrado ou alugado)");
        }
    }

    private Item obterItemAlvo() {
        Item atual = tabela.getSelectionModel().getSelectedItem();
        tabela.getSelectionModel().clearSelection();

        if (atual == null) {
            Optional<Integer> id = pedirInteiro(ID_ITEM);
            if (id.isEmpty()) return null;
            atual = sistema.buscarItem(id.get());
            if (atual == null) {
                erro(NAO_ENCONTRADO);
            }
        }
        return atual;
    }

    private String pegaNovo(String novo, String antigo) {
        return novo.isEmpty() ? antigo : novo;
    }

    private void mostraItem(int id) {
        Item item = sistema.buscarItem(id);
        if (item == null) {
            info("Item não cadastrado.");
            return;
        }

        tabela.getItems().setAll(item);
    }

    private Optional<Escolha<Categoria>> selecionarCategoria() {
        List<Categoria> categorias = sistema.listarCategorias();

        if (categorias.isEmpty()) {
            info("Nenhuma categoria cadastrada. O item será cadastrado sem categoria");
            return Optional.of(new Escolha<Categoria>(null));
        }

        List<String> rotulos = categorias.stream().map(this::rotuloCategoria).toList();

        Optional<Integer> posicao = escolherPosicao("Categoria do item:", rotulos, "(Sem categoria)");
        if (posicao.isEmpty()) {
            return Optional.empty();
        }

        if (posicao.get() < 0) {
            return Optional.of(new Escolha<Categoria>(null));
        }

        return Optional.of(new Escolha<Categoria>(categorias.get(posicao.get())));
    }

    private Optional<Escolha<Fornecedor>> selecionarFornecedor() {
        List<Fornecedor> fornecedores = sistema.listarFornecedores();

        if (fornecedores.isEmpty()) {
            info("Nenhum fornecedor cadastrado. O item será cadastrado sem fornecedor");
            return Optional.of(new Escolha<Fornecedor>(null));
        }

        List<String> rotulos = fornecedores.stream().map(this::rotuloFornecedor).toList();

        Optional<Integer> posicao = escolherPosicao("Fornecedor do item:", rotulos, "(Sem fornecedor)");
        if (posicao.isEmpty()) {
            return Optional.empty();
        }

        if (posicao.get() < 0) {
            return Optional.of(new Escolha<Fornecedor>(null));
        }

        return Optional.of(new Escolha<Fornecedor>(fornecedores.get(posicao.get())));
    }

    private String rotuloCategoria(Categoria categoria) {
        return "[" + categoria.getId() + "] " + categoria.getNome();
    }

    private String rotuloFornecedor(Fornecedor fornecedor) {
        return "[" + fornecedor.getId() + "] " + fornecedor.getRazaoSocial();
    }
}