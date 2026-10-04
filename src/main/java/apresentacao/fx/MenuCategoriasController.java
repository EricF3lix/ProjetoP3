package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Categoria;
import javafx.fxml.FXML;

public class MenuCategoriasController extends MenuController {

    private static final String ID_CATEGORIA = "ID da categoria:";
    private static final String NAO_ENCONTRADA = "Categoria não encontrada";

    @Override
    protected String getTitulo() {
        return "Gerenciar Categorias";
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
            info("Categoria cadastrada com sucesso. ID: " + id);
        } else {
            erro("Já existe uma categoria com esse nome");
        }
    }

    @FXML
    private void buscarCategoria() {
        Optional<Integer> id = pedirInteiro(ID_CATEGORIA);
        if (id.isEmpty()) return;

        Categoria categoria = sistema.buscarCategoria(id.get());
        if (categoria == null) {
            erro(NAO_ENCONTRADA);
            return;
        }

        limpar();
        escrever("===== DADOS DA CATEGORIA =====");
        escrever("ID:        " + categoria.getId());
        escrever("Nome:      " + categoria.getNome());
        escrever("Descrição: " + categoria.getDescricao());
        escrever("Ativa:     " + categoria.isAtiva());
    }

    @FXML
    private void listarCategorias() {
        List<Categoria> categorias = sistema.listarCategorias();

        limpar();
        escrever("===== CATEGORIAS CADASTRADAS =====");

        if (categorias.isEmpty()) {
            escrever("Nenhuma categoria cadastrada");
            return;
        }

        escreverf("%-5s %-25s %-40s %-6s", "ID", "NOME", "DESCRIÇÃO", "ATIVA");
        escrever("-".repeat(80));
        for (Categoria c : categorias) {
            escreverf("%-5d %-25s %-40s %-6s", c.getId(), c.getNome(), c.getDescricao(), c.isAtiva());
        }
    }

    @FXML
    private void atualizarCategoria() {
        Optional<Integer> id = pedirInteiro(ID_CATEGORIA);
        if (id.isEmpty()) return;

        Categoria atual = sistema.buscarCategoria(id.get());
        if (atual == null) {
            erro(NAO_ENCONTRADA);
            return;
        }

        Optional<String> nome = pedirTexto("Nome:", atual.getNome());
        if (nome.isEmpty()) return;
        Optional<String> descricao = pedirTexto("Descrição:", atual.getDescricao());
        if (descricao.isEmpty()) return;

        String novoNome = nome.get().isEmpty() ? atual.getNome() : nome.get();
        String novaDescricao = descricao.get().isEmpty() ? atual.getDescricao() : descricao.get();

        if (sistema.atualizarCategoria(new Categoria(id.get(), novoNome, novaDescricao))) {
            info("Categoria atualizada com sucesso");
        } else {
            erro("Erro ao atualizar categoria");
        }
    }

    @FXML
    private void desativarCategoria() {
        Optional<Integer> id = pedirInteiro(ID_CATEGORIA);
        if (id.isEmpty()) return;

        Categoria categoria = sistema.buscarCategoria(id.get());
        if (categoria == null) {
            erro(NAO_ENCONTRADA);
            return;
        }

        if (!confirmar("Tem certeza que deseja desativar \"" + categoria.getNome() + "\"?")) {
            return;
        }

        if (sistema.desativarCategoria(id.get())) {
            info("Categoria desativada com sucesso");
        } else {
            erro("Erro ao desativar categoria");
        }
    }
}