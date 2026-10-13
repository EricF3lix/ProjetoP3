package apresentacao.fx;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

    private record Escolha<T>(T valor) {}

    @Override
    protected String getTitulo() {
        return "Gerenciar Itens";
    }

    @Override
    protected void aoExibir() {
        tabela.setPlaceholder(new Label(""));
        if (!listenerConfigurado) {
            pesquisa.textProperty().addListener((obj, antigo, novo) -> fazPesquisa(novo));
            this.listenerConfigurado = true;
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
        pedirInteiro(ID_ITEM).ifPresent(this::mostraItem);
    }

    private void fazPesquisa(String pesquisado) {
        List<Item> filtrados = sistema.filtrarItem(pesquisado);
        tabela.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    private void listarItens() {
        List<Item> listaItens = sistema.listarItens();
        if (!listaItens.isEmpty()) {
            tabela.setItems(FXCollections.observableArrayList(listaItens));
        }
    }

    @FXML
    private void atualizarItem() {
        Item atual = obterItemAlvo();
        if (atual == null) return;

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

        Item itemNovo = new Item(atual.getId(), novoNome, novaDescricao, taxaDiaria.get(), novoEstado,
                valorReposicao.get(), atual.getCategoria(), atual.getFornecedor());

        if (sistema.atualizarItem(itemNovo)) {
            info("Item atualizado com sucesso.");
            mostraItem(atual.getId());
        } else {
            erro("Erro ao atualizar item.");
        }
    }

    @FXML
    private void desativarItem() {
        Item item = obterItemAlvo();
        if (item == null) return;

        if (!confirmar("Tem certeza que deseja desativar \"" + item.getNome() + "\"?")) {
            return;
        }

        if (sistema.excluirItem(item.getId())) {
            info("Item desativado com sucesso");
            mostraItem(item.getId());
        } else {
            erro("Erro ao desativar item (não encontrado ou alugado)");
        }
    }

    private Item obterItemAlvo() {
        Item selecionado = tabela.getSelectionModel().getSelectedItem();
        tabela.getSelectionModel().clearSelection();

        if (selecionado != null) {
            return selecionado;
        }

        Optional<Integer> id = pedirInteiro(ID_ITEM);
        if (id.isEmpty()) return null;

        Item item = sistema.buscarItem(id.get());
        if (item == null) {
            erro(NAO_ENCONTRADO);
        }
        return item;
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
        return selecionarEntidade(sistema.listarCategorias(),"Nenhuma categoria cadastrada. O item será cadastrado sem categoria", "Categoria do item:", "(Sem categoria)", c -> "[" + c.getId() + "] " + c.getNome());
    }

    private Optional<Escolha<Fornecedor>> selecionarFornecedor() {
        return selecionarEntidade(sistema.listarFornecedores(),"Nenhum fornecedor cadastrado. O item será cadastrado sem fornecedor", "Fornecedor do item:", "(Sem fornecedor)",f -> "[" + f.getId() + "] " + f.getRazaoSocial());
    }

    private <T> Optional<Escolha<T>> selecionarEntidade(
            List<T> lista,
            String mensagemVazio,
            String titulo,
            String opcaoSem,
            Function<T, String> rotuloMapper) {

        if (lista.isEmpty()) {
            info(mensagemVazio);
            return Optional.of(new Escolha<>(null));
        }

        List<String> rotulos = lista.stream().map(rotuloMapper).toList();
        Optional<Integer> posicao = escolherPosicao(titulo, rotulos, opcaoSem);

        if (posicao.isEmpty()) {
            return Optional.empty();
        }

        T selecionado = posicao.get() < 0 ? null : lista.get(posicao.get());
        return Optional.of(new Escolha<>(selecionado));
    }
}