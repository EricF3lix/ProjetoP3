package business.cruds;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entidades.Multa;
import support.FakeMultaRepositorio;

@DisplayName("GerenciamentoMultas")
class GerenciamentoMultasTest {

    private FakeMultaRepositorio repositorio;
    private GerenciamentoMultas gerenciamentoMultas;

    @BeforeEach
    void setUp() {
        repositorio = new FakeMultaRepositorio();
        gerenciamentoMultas = new GerenciamentoMultas(repositorio);
    }

    private Multa novaMulta(int idContrato, double valor) {
        return new Multa(idContrato, "ATRASO", "Devolução em atraso", valor, "2026-01-15");
    }

    @Test
    @DisplayName("registrarMulta deve registrar a multa")
    void deveRegistrarMultaNova() {
        boolean resultado = gerenciamentoMultas.registrarMulta(novaMulta(10, 50.0));

        assertTrue(resultado);
        assertEquals(1, gerenciamentoMultas.listarMultas().size());
    }

    @Test
    @DisplayName("registrarMulta deve deixar a multa com o id gerado pelo banco")
    void deveGerarIdAoRegistrar() {
        Multa multa = novaMulta(10, 50.0);
        assertEquals(0, multa.getId(), "antes de gravar, a multa ainda não tem id");

        gerenciamentoMultas.registrarMulta(multa);

        assertTrue(multa.getId() > 0);
    }

    @Test
    @DisplayName("registrarMulta deve dar ids diferentes para multas diferentes")
    void deveGerarIdsDiferentes() {
        Multa primeira = novaMulta(10, 50.0);
        Multa segunda = novaMulta(10, 30.0);

        gerenciamentoMultas.registrarMulta(primeira);
        gerenciamentoMultas.registrarMulta(segunda);

        assertNotEquals(primeira.getId(), segunda.getId());
        assertEquals(2, gerenciamentoMultas.listarMultas().size());
    }

    @Test
    @DisplayName("buscarMulta deve retornar a multa quando existir")
    void deveEncontrarMultaExistente() {
        Multa multa = novaMulta(10, 50.0);
        gerenciamentoMultas.registrarMulta(multa);

        assertNotNull(gerenciamentoMultas.buscarMulta(multa.getId()));
    }

    @Test
    @DisplayName("buscarMulta deve retornar null quando não existir")
    void deveRetornarNullAoBuscarMultaInexistente() {
        assertNull(gerenciamentoMultas.buscarMulta(999));
    }

    @Test
    @DisplayName("listarMultasPorContrato deve retornar apenas as multas do contrato informado")
    void deveFiltrarMultasPorContrato() {
        gerenciamentoMultas.registrarMulta(novaMulta(10, 50.0));
        gerenciamentoMultas.registrarMulta(novaMulta(10, 30.0));
        gerenciamentoMultas.registrarMulta(novaMulta(20, 40.0));

        List<Multa> resultado = gerenciamentoMultas.listarMultasPorContrato(10);

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("listarMultasPendentes deve retornar somente as multas ainda não pagas")
    void deveListarSomenteMultasNaoPagas() {
        Multa paga = novaMulta(10, 50.0);
        Multa pendente = novaMulta(11, 30.0);
        gerenciamentoMultas.registrarMulta(paga);
        gerenciamentoMultas.registrarMulta(pendente);
        gerenciamentoMultas.quitarMulta(paga.getId());

        List<Multa> pendentes = gerenciamentoMultas.listarMultasPendentes();

        assertEquals(1, pendentes.size());
        assertEquals(pendente.getId(), pendentes.get(0).getId());
    }

    @Test
    @DisplayName("quitarMulta deve retornar false quando a multa não existe")
    void deveRetornarFalseAoQuitarMultaInexistente() {
        assertFalse(gerenciamentoMultas.quitarMulta(999));
        assertEquals(0, repositorio.getChamadasAtualizar(), "não deve tentar atualizar nada");
    }

    @Test
    @DisplayName("quitarMulta deve marcar a multa como paga quando ela está pendente")
    void deveQuitarMultaPendente() {
        Multa multa = novaMulta(10, 50.0);
        gerenciamentoMultas.registrarMulta(multa);

        boolean resultado = gerenciamentoMultas.quitarMulta(multa.getId());

        assertTrue(resultado);
        assertTrue(gerenciamentoMultas.buscarMulta(multa.getId()).isPaga());
    }

    @Test
    @DisplayName("quitarMulta deve pedir ao repositório para atualizar a coluna paga")
    void deveAtualizarColunaPaga() {
        Multa multa = novaMulta(10, 50.0);
        gerenciamentoMultas.registrarMulta(multa);

        gerenciamentoMultas.quitarMulta(multa.getId());

        assertEquals(1, repositorio.getChamadasAtualizar());
        assertEquals("paga", repositorio.getUltimaColuna());
        assertEquals(Boolean.TRUE, repositorio.getUltimoValor());
        assertEquals(multa.getId(), repositorio.getUltimoId());
    }

    @Test
    @DisplayName("quitarMulta deve retornar false quando a multa já está paga")
    void deveRetornarFalseQuandoMultaJaPaga() {
        Multa multa = novaMulta(10, 50.0);
        gerenciamentoMultas.registrarMulta(multa);
        gerenciamentoMultas.quitarMulta(multa.getId());

        boolean resultado = gerenciamentoMultas.quitarMulta(multa.getId());

        assertFalse(resultado);
        assertEquals(1, repositorio.getChamadasAtualizar(), "só a primeira quitação atualiza");
    }
} 