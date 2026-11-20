package business.cruds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import support.FakeContratoRepositorio;

@DisplayName("GerenciamentoContratos (operações que gravam no banco)")
class GerenciamentoContratosOperacoesTest {

    private FakeContratoRepositorio repositorio;
    private GerenciamentoContratos negocio;
    private Cliente cliente;
    private Item item;

    @BeforeEach
    void setUp() {
        repositorio = new FakeContratoRepositorio();
        negocio = new GerenciamentoContratos(repositorio);

        cliente = new Cliente("Ana", "ana@loja.com", "000.000.000-00", "123");
        FakeContratoRepositorio.definirId(cliente, 1);

        item = new Item("Furadeira", "Furadeira de impacto", 20.0, "BOM", 500.0, null, null);
        FakeContratoRepositorio.definirId(item, 1);
    }

    private ContratoAluguel novoContrato() {
        return new ContratoAluguel(cliente, item, "2026-01-05", "2026-01-10", 100.0);
    }

    @Test
    @DisplayName("cadastrarContrato deve gravar o contrato, gerar o id e deixar o item ALUGADO")
    void deveCadastrarEAlugarItem() {
        ContratoAluguel contrato = novoContrato();

        boolean resultado = negocio.cadastrarContrato(contrato);

        assertTrue(resultado);
        assertTrue(contrato.getId() > 0);
        assertEquals("ALUGADO", item.getStatus());
        assertEquals(1, repositorio.getChamadasAtualizar(), "o status do item precisa ser gravado");
    }

    @Test
    @DisplayName("cadastrarContrato deve rejeitar quando o item já tem contrato ativo")
    void deveRejeitarItemJaAlugado() {
        negocio.cadastrarContrato(novoContrato());

        boolean resultado = negocio.cadastrarContrato(novoContrato());

        assertFalse(resultado);
        assertEquals(1, negocio.listarContratos().size());
    }

    @Test
    @DisplayName("finalizarContrato no prazo: finaliza, devolve o item e não gera multa")
    void deveFinalizarNoPrazo() {
        ContratoAluguel contrato = novoContrato();
        negocio.cadastrarContrato(contrato);

        boolean resultado = negocio.finalizarContrato(contrato.getId(), "2026-01-10");

        assertTrue(resultado);
        assertEquals("FINALIZADO", contrato.getStatus());
        assertEquals("2026-01-10", contrato.getDataDevolucaoReal());
        assertEquals("DISPONIVEL", item.getStatus());
        assertFalse(contrato.possuiMulta());
        assertEquals(2, repositorio.getChamadasAtualizar(), "uma gravação no cadastro e outra na devolução");
    }

    @Test
    @DisplayName("finalizarContrato com atraso: calcula a multa e deixa pendente")
    void deveCalcularMultaPorAtraso() {
        ContratoAluguel contrato = novoContrato();
        negocio.cadastrarContrato(contrato);

        negocio.finalizarContrato(contrato.getId(), "2026-01-13");

        assertEquals(13.0, contrato.getValorMulta(), 0.0001);
        assertFalse(contrato.isMultaPaga());
        assertTrue(negocio.clientePossuiMultaPendente(cliente.getId()));
    }

    @Test
    @DisplayName("finalizarContrato deve devolver false para contrato inexistente ou que já não está ativo")
    void deveRecusarFinalizacaoInvalida() {
        assertFalse(negocio.finalizarContrato(999, "2026-01-10"));

        ContratoAluguel contrato = novoContrato();
        negocio.cadastrarContrato(contrato);
        negocio.finalizarContrato(contrato.getId(), "2026-01-10");

        assertFalse(negocio.finalizarContrato(contrato.getId(), "2026-01-10"));
    }

    @Test
    @DisplayName("depois de finalizar, o item pode ser alugado de novo")
    void deveLiberarItemDepoisDeFinalizar() {
        ContratoAluguel contrato = novoContrato();
        negocio.cadastrarContrato(contrato);
        negocio.finalizarContrato(contrato.getId(), "2026-01-10");

        assertTrue(negocio.cadastrarContrato(novoContrato()));
    }

    @Test
    @DisplayName("cancelarContrato deve cancelar, devolver o item e gravar")
    void deveCancelarContratoAtivo() {
        ContratoAluguel contrato = novoContrato();
        negocio.cadastrarContrato(contrato);

        boolean resultado = negocio.cancelarContrato(contrato.getId());

        assertTrue(resultado);
        assertEquals("CANCELADO", contrato.getStatus());
        assertEquals("DISPONIVEL", item.getStatus());
        assertEquals(2, repositorio.getChamadasAtualizar());
    }

    @Test
    @DisplayName("cancelarContrato não deve mexer em contrato que já não está ativo")
    void naoDeveCancelarDuasVezes() {
        ContratoAluguel contrato = novoContrato();
        negocio.cadastrarContrato(contrato);
        negocio.cancelarContrato(contrato.getId());

        assertFalse(negocio.cancelarContrato(contrato.getId()));
        assertFalse(negocio.cancelarContrato(999));
    }

    @Test
    @DisplayName("quitarMultaContrato deve quitar a multa pendente uma única vez")
    void deveQuitarMultaDoContrato() {
        ContratoAluguel contrato = novoContrato();
        negocio.cadastrarContrato(contrato);
        negocio.finalizarContrato(contrato.getId(), "2026-01-13");

        assertTrue(negocio.quitarMultaContrato(contrato.getId()));
        assertTrue(contrato.isMultaPaga());
        assertFalse(negocio.quitarMultaContrato(contrato.getId()));
        assertFalse(negocio.clientePossuiMultaPendente(cliente.getId()));
    }

    @Test
    @DisplayName("quitarMultaContrato deve recusar contrato sem multa")
    void naoDeveQuitarContratoSemMulta() {
        ContratoAluguel contrato = novoContrato();
        negocio.cadastrarContrato(contrato);

        assertFalse(negocio.quitarMultaContrato(contrato.getId()));
    }

    @Test
    @DisplayName("clientePossuiHistorico deve considerar só os contratos do próprio cliente")
    void deveVerificarHistoricoDoCliente() {
        negocio.cadastrarContrato(novoContrato());

        Cliente outro = new Cliente("Beto", "beto@loja.com", "111.111.111-11", "123");
        FakeContratoRepositorio.definirId(outro, 2);

        assertTrue(negocio.clientePossuiHistorico(cliente.getId()));
        assertFalse(negocio.clientePossuiHistorico(outro.getId()));
    }

    @Test
    @DisplayName("calcularMulta: 10 fixos + 5% da taxa por dia de atraso; sem atraso, zero")
    void deveCalcularMulta() {
        assertEquals(0.0, negocio.calcularMulta(0, 20.0), 0.0001);
        assertEquals(13.0, negocio.calcularMulta(3, 20.0), 0.0001);
    }
}