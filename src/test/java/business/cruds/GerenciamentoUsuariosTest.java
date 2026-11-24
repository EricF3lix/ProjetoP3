package business.cruds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entidades.Administrador;
import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Funcionario;
import entidades.Item;
import support.FakeContratoRepositorio;
import support.FakeUsuarioRepositorio;

@DisplayName("GerenciamentoUsuarios (operações que gravam no banco)")
class GerenciamentoUsuariosOperacoesTest {

    private FakeUsuarioRepositorio repositorio;
    private GerenciamentoContratos contratos;
    private GerenciamentoUsuarios negocio;

    @BeforeEach
    void setUp() {
        repositorio = new FakeUsuarioRepositorio();
        contratos = new GerenciamentoContratos(new FakeContratoRepositorio());
        negocio = new GerenciamentoUsuarios(repositorio, contratos);
    }

    private Cliente novoCliente() {
        return new Cliente("Ana", "ana@loja.com", "000.000.000-01", "senha1");
    }

    @Test
    @DisplayName("cadastrarUsuario deve gravar o usuário e deixar o id gerado pelo banco")
    void deveCadastrarEGerarId() {
        Cliente cliente = novoCliente();
        assertEquals(0, cliente.getId(), "antes de gravar, o usuário ainda não tem id");

        boolean resultado = negocio.cadastrarUsuario(cliente);

        assertTrue(resultado);
        assertTrue(cliente.getId() > 0);
        assertEquals(1, negocio.listarUsuarios().size());
    }

    @Test
    @DisplayName("cadastrarUsuario deve rejeitar CPF já cadastrado")
    void deveRejeitarCpfDuplicado() {
        negocio.cadastrarUsuario(novoCliente());

        boolean resultado = negocio.cadastrarUsuario(
                new Cliente("Outra Ana", "outra@loja.com", "000.000.000-01", "x"));

        assertFalse(resultado);
        assertEquals(1, negocio.listarUsuarios().size());
    }

    @Test
    @DisplayName("cadastrarUsuario deve rejeitar e-mail já cadastrado, sem diferenciar maiúsculas")
    void deveRejeitarEmailDuplicado() {
        negocio.cadastrarUsuario(novoCliente());

        boolean resultado = negocio.cadastrarUsuario(
                new Cliente("Outra Ana", "ANA@LOJA.COM", "111.111.111-11", "x"));

        assertFalse(resultado);
        assertEquals(1, negocio.listarUsuarios().size());
    }

    @Test
    @DisplayName("buscarUsuario deve achar pelo id gerado e devolver null quando não existe")
    void deveBuscarPorId() {
        Cliente cliente = novoCliente();
        negocio.cadastrarUsuario(cliente);

        assertSame(cliente, negocio.buscarUsuario(cliente.getId()));
        assertNull(negocio.buscarUsuario(999));
    }

    @Test
    @DisplayName("atualizarUsuario deve trocar nome, e-mail e senha e gravar")
    void deveAtualizarDados() {
        Cliente cliente = novoCliente();
        negocio.cadastrarUsuario(cliente);

        boolean resultado = negocio.atualizarUsuario(cliente.getId(), "Ana Maria", "ana.maria@loja.com", "nova");

        assertTrue(resultado);
        assertEquals("Ana Maria", cliente.getNome());
        assertEquals("ana.maria@loja.com", cliente.getEmail());
        assertEquals("nova", cliente.getSenha());
        assertEquals(3, repositorio.getChamadasAtualizar(), "a alteração precisa ser gravada");
    }

    @Test
    @DisplayName("atualizarUsuario deve devolver false e não gravar nada para usuário inexistente")
    void naoDeveAtualizarInexistente() {
        assertFalse(negocio.atualizarUsuario(999, "x", "x@loja.com", "x"));
        assertEquals(0, repositorio.getChamadasAtualizar());
    }

    @Test
    @DisplayName("desativaUsuario deve desativar e gravar")
    void deveDesativarUsuario() {
        Cliente cliente = novoCliente();
        negocio.cadastrarUsuario(cliente);

        boolean resultado = negocio.desativaUsuario(cliente.getId());

        assertTrue(resultado);
        assertFalse(cliente.isAtivo());
        assertEquals(1, repositorio.getChamadasDesativar());
        assertFalse(negocio.desativaUsuario(999));
    }

    @Test
    @DisplayName("desativaUsuario deve recusar quando o cliente tem multa pendente")
    void naoDeveDesativarComMultaPendente() {
        Cliente cliente = novoCliente();
        negocio.cadastrarUsuario(cliente);

        Item item = new Item("Furadeira", "Furadeira de impacto", 20.0, "BOM", 500.0, null, null);
        item.setId(1);

        ContratoAluguel contrato = new ContratoAluguel(cliente, item, "2026-01-05", "2026-01-10", 100.0);
        contratos.cadastrarContrato(contrato);
        contratos.finalizarContrato(contrato.getId(), "2026-01-13");

        boolean resultado = negocio.desativaUsuario(cliente.getId());

        assertFalse(resultado);
        assertTrue(cliente.isAtivo());
        assertEquals(0, repositorio.getChamadasDesativar());
    }

    @Test
    @DisplayName("realizarLogin deve devolver o usuário com e-mail e senha corretos (e a classe certa de cada perfil)")
    void deveFazerLogin() {
        negocio.cadastrarUsuario(novoCliente());
        negocio.cadastrarUsuario(new Funcionario("Bia", "bia@loja.com", "222.222.222-22", "f123"));
        negocio.cadastrarUsuario(new Administrador("Caio", "caio@loja.com", "333.333.333-33", "a123"));

        assertInstanceOf(Cliente.class, negocio.realizarLogin("ana@loja.com", "senha1"));
        assertInstanceOf(Funcionario.class, negocio.realizarLogin("bia@loja.com", "f123"));
        assertInstanceOf(Administrador.class, negocio.realizarLogin("CAIO@loja.com", "a123"));
    }

    @Test
    @DisplayName("realizarLogin deve devolver null com senha errada, e-mail desconhecido ou usuário inativo")
    void deveRecusarLoginInvalido() {
        Cliente cliente = novoCliente();
        negocio.cadastrarUsuario(cliente);

        assertNull(negocio.realizarLogin("ana@loja.com", "errada"));
        assertNull(negocio.realizarLogin("naoexiste@loja.com", "senha1"));

        negocio.desativaUsuario(cliente.getId());
        assertNull(negocio.realizarLogin("ana@loja.com", "senha1"));
        assertNotNull(negocio.buscarUsuario(cliente.getId()), "desativar não apaga o usuário");
    }
}