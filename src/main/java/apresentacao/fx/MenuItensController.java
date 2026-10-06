package apresentacao.fx;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import javafx.fxml.FXML;
 

public class MenuItensController extends MenuController {
 
    private static final String ID_ITEM = "ID do item:";
    private static final String NAO_ENCONTRADO = "Item não encontrado";
    private static final List<String> ESTADOS = List.of("NOVO", "BOM", "REGULAR", "RUIM");
 
   
    private record Escolha<T>(T valor) {
    }
 
    @Override
    protected String getTitulo() {
        return "Gerenciar Itens";
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
            info("Item cadastrado com sucesso. ID: " + id);
        } else {
            erro("Erro ao cadastrar item");
        }
    }

    @FXML
    private void buscarItem() {
        Optional<Integer> id = pedirInteiro(ID_ITEM);
        if (id.isEmpty()) return;
 
        Item item = sistema.buscarItem(id.get());
        if (item == null) {
            erro(NAO_ENCONTRADO);
            return;
        }
 
        limpar();
        escrever("===== DADOS DO ITEM =====");
        escrever("ID:           " + item.getId());
        escrever("Nome:         " + item.getNome());
        escrever("Descrição:    " + item.getDescricao());
        escreverf("Taxa diária:  R$ %.2f", item.getTaxaDiaria());
        escrever("Estado:       " + item.getEstadoConservacao());
        escreverf("Reposição:    R$ %.2f", item.getValorReposicao());
        escrever("Categoria:    " + (item.getCategoria() != null ? item.getCategoria().getNome() : "Sem categoria"));
        escrever("Fornecedor:   " + (item.getFornecedor() != null ? item.getFornecedor().getRazaoSocial() : "Sem fornecedor"));
        escrever("Status:       " + item.getStatus());
        escrever("Ativo:        " + item.isAtivo());
    }
     @FXML
    private void listarItens() {
        List<Item> itens = sistema.listarItens();
 
        limpar();
        escrever("===== ITENS CADASTRADOS =====");
 
        if (itens.isEmpty()) {
            escrever("Nenhum item cadastrado");
            return;
        }
 
        escreverf("%-5s %-25s %-20s %-10s %-12s %-6s", "ID", "NOME", "CATEGORIA", "TAXA/DIA", "STATUS", "ATIVO");
        escrever("-".repeat(85));
 
        for (Item item : itens) {
            escreverf("%-5d %-25s %-20s R$%-8.2f %-12s %-6s",
                    item.getId(),
                    item.getNome(),
                    item.getCategoria() != null ? item.getCategoria().getNome() : "-",
                    item.getTaxaDiaria(),
                    item.getStatus(),
                    item.isAtivo());
        }
    }
    @FXML
    private void desativarItem() {
        Optional<Integer> id = pedirInteiro(ID_ITEM);
        if (id.isEmpty()) return;
 
        if (sistema.excluirItem(id.get())) {
            info("Item desativado com sucesso");
        } else {
            erro("Item não encontrado ou alugado");
        }
    }
    private Optional<Escolha<Categoria>> selecionarCategoria() {
        List<Categoria> categorias = sistema.listarCategorias();

        if (categorias.isEmpty()) {
            info("Nenhuma categoria cadastrada. O item será cadastrado sem categoria");
            return Optional.of(new Escolha<Categoria>(null));
        }

        List<String> rotulos = new ArrayList<>();
        for (Categoria c : categorias) {
            rotulos.add("[" + c.getId() + "] " + c.getNome());
        }

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

        List<String> rotulos = new ArrayList<>();
        for (Fornecedor f : fornecedores) {
            rotulos.add("[" + f.getId() + "] " + f.getRazaoSocial());
        }

        Optional<Integer> posicao = escolherPosicao("Fornecedor do item:", rotulos, "(Sem fornecedor)");
        if (posicao.isEmpty()) {
            return Optional.empty(); 
        }

        if (posicao.get() < 0) {
            return Optional.of(new Escolha<Fornecedor>(null));
        }

        return Optional.of(new Escolha<Fornecedor>(fornecedores.get(posicao.get())));
    }
}