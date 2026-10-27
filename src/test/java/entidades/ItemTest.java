package entidades;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Item (regras de disponibilidade)")
class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria(1, "Ferramentas", "Ferramentas em geral");
        Fornecedor fornecedor = new Fornecedor(1, "Fornecedor XPTO", "00.000.000/0001-00", "contato@xpto.com", "8199999999");

        item = new Item(1, "Furadeira", "Furadeira de impacto", 20.0, "BOM", 500.0, categoria, fornecedor);
    }

    @Test
    @DisplayName("deve nascer disponível, ativo e com status DISPONIVEL")
    void deveNascerDisponivel() {
        assertTrue(item.isAtivo());
        assertEquals("DISPONIVEL", item.getStatus());
        assertTrue(item.estaDisponivel());
        assertFalse(item.estaAlugado());
        assertFalse(item.estaEmManutencao());
    }

    @Test
    @DisplayName("deve ficar indisponível após ser alugado")
    void deveFicarIndisponivelAoAlugar() {
        item.alugar();

        assertTrue(item.estaAlugado());
        assertFalse(item.estaDisponivel(), "Item alugado não pode estar disponível");
    }

    @Test
    @DisplayName("deve voltar a ficar disponível após ser devolvido")
    void deveFicarDisponivelAposDevolucao() {
        item.alugar();
        item.devolver();

        assertTrue(item.estaDisponivel());
        assertFalse(item.estaAlugado());
    }

    @Test
    @DisplayName("deve ficar indisponível quando enviado para manutenção")
    void deveFicarIndisponivelEmManutencao() {
        item.enviarParaManutencao();

        assertTrue(item.estaEmManutencao());
        assertFalse(item.estaDisponivel(), "Item em manutenção não pode estar disponível");
    }

    @Test
    @DisplayName("um item desativado fica INDISPONIVEL e nunca está disponível para aluguel")
    void itemDesativadoNuncaEstaDisponivel() {
        item.desativar();

        assertFalse(item.isAtivo());
        assertEquals("INDISPONIVEL", item.getStatus());
        assertFalse(item.estaDisponivel(), "Item desativado não pode estar disponível para aluguel");
    }

    @Test
    @DisplayName("setStatus deve normalizar o valor para maiúsculas")
    void setStatusDeveNormalizarParaMaiusculas() {
        item.setStatus("disponivel");

        assertEquals("DISPONIVEL", item.getStatus());
    }
    @Test
    @DisplayName("item desativado ignora tentativas de mudar o status")
    void itemDesativadoIgnoraMudancaDeStatus() {
        item.desativar();

        item.setStatus("DISPONIVEL");
        item.alugar();
        item.devolver();
        item.enviarParaManutencao();

        assertEquals("INDISPONIVEL", item.getStatus());
        assertFalse(item.estaDisponivel());
    }

    @Test
    @DisplayName("setAtivo(false) também deixa o item INDISPONIVEL")
    void setAtivoFalseDeixaItemIndisponivel() {
        item.setAtivo(false);

        assertEquals("INDISPONIVEL", item.getStatus());
    }
}
