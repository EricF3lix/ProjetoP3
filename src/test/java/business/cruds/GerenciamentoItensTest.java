package business.cruds;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.FakeItemRepositorio;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GerenciamentoItens")
class GerenciamentoItensTest {

    private FakeItemRepositorio repositorio;
    private GerenciamentoItens gerenciamentoItens;

    private Categoria categoria;
    private Fornecedor fornecedor;

    @BeforeEach
    void setUp() {
        repositorio = new FakeItemRepositorio();
        gerenciamentoItens = new GerenciamentoItens(repositorio);

        categoria = new Categoria("Ferramentas", "Ferramentas em geral");
        fornecedor = new Fornecedor("Fornecedor XPTO", "00.000.000/0001-00", "contato@xpto.com", "8199999999");
    }

    private Item novoItem(int id) {
        return new Item("Furadeira", "Furadeira de impacto", 20.0, "BOM", 500.0, categoria, fornecedor);
    }

    

    @Test
    @DisplayName("cadastrarItem deve cadastrar um item novo e deixá-lo disponível")
    void deveCadastrarItemNovo() {
        Item item = novoItem(1);

        boolean resultado = gerenciamentoItens.cadastrarItem(item);

        assertTrue(resultado);
        assertTrue(item.estaDisponivel());
    }

    @Test
    @DisplayName("cadastrarItem deve rejeitar quando já existe item com o mesmo id")
    void deveRejeitarItemComIdDuplicado() {
        gerenciamentoItens.cadastrarItem(novoItem(1));

        boolean resultado = gerenciamentoItens.cadastrarItem(novoItem(1));

        assertFalse(resultado);
        assertEquals(1, gerenciamentoItens.listarItens().size());
    }

    
    @Test
    @DisplayName("buscarItem deve retornar o item quando existir")
    void deveEncontrarItemExistente() {
        gerenciamentoItens.cadastrarItem(novoItem(1));

        assertNotNull(gerenciamentoItens.buscarItem(1));
    }

    @Test
    @DisplayName("buscarItem deve retornar null quando não existir")
    void deveRetornarNullAoBuscarItemInexistente() {
        assertNull(gerenciamentoItens.buscarItem(999));
    }

    

    @Test
    @DisplayName("atualizarItem deve retornar false quando o item não existe")
    void deveRetornarFalseAoAtualizarItemInexistente() {
        boolean resultado = gerenciamentoItens.atualizarItem("nome","novo nome",999);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("atualizarItem deve atualizar todos os campos do item existente")
    void deveAtualizarCamposDoItem() {
        gerenciamentoItens.cadastrarItem(novoItem(1));

        boolean resultado = false;
        if (gerenciamentoItens.atualizarItem("nome","Serra Elétrica",1)){
        if (gerenciamentoItens.atualizarItem("descricao","Serra circular",1)){
        if (gerenciamentoItens.atualizarItem("taxaDiaria","35.0",1)){
        if (gerenciamentoItens.atualizarItem("estadoConservacao","OTIMO",1)){
            if (gerenciamentoItens.atualizarItem("valorReposicao","800.0",1)){
            resultado = true;}}}}}

        Item atualizado = gerenciamentoItens.buscarItem(1);

        assertTrue(resultado);
        assertEquals("Serra Elétrica", atualizado.getNome());
        assertEquals("Serra circular", atualizado.getDescricao());
        assertEquals(35.0, atualizado.getTaxaDiaria(), 0.0001);
        assertEquals("OTIMO", atualizado.getEstadoConservacao());
        assertEquals(800.0, atualizado.getValorReposicao(), 0.0001);
    }

    @Test
    @DisplayName("excluirItem deve desativar o item, tornando-o indisponível")
    void deveDesativarItem() {
        gerenciamentoItens.cadastrarItem(novoItem(1));

        gerenciamentoItens.desativarItem(1);
        Item item = gerenciamentoItens.buscarItem(1);

        assertFalse(item.isAtivo());
        assertFalse(item.estaDisponivel(), "Item desativado não pode estar disponível para aluguel");
    }

}