package apresentacao;

import java.util.List;
import java.util.Scanner;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;

@SuppressWarnings("java:S106")
public class MenuUsuarios {

    private SistemaFacade sistema;
    private Scanner scanner;
    private boolean acessoTotal;

    private static final String PRINT_SENHA = "Senha: ";
    private static final String PRINT_EMAIL = "Email: ";
    private static final String PRINT_CPF = "CPF: ";
    private static final String PRINT_NOME = "Nome: ";
    private static final String PRINT_ID_USUARIO = "ID do usuário: ";

    private static final String PRINT_USUARIO_INVALIDO = "Erro: CPF ou e-mail já cadastrado";

    public MenuUsuarios(SistemaFacade sistema, Scanner scanner, boolean acessoTotal) {
        this.sistema = sistema;
        this.scanner = scanner;
        this.acessoTotal = acessoTotal;
    }

    public void exibir() {
        int opcao;
        do {
            exibirMenu();
            opcao = ValidaEntrada.lerInteiro(scanner);
            executarOpcao(opcao);
        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println("\n===== GERENCIAR USUÁRIOS =====");
        System.out.println("1 - Cadastrar Cliente");
        System.out.println("2 - Buscar Usuário por ID");
        System.out.println("3 - Listar todos os Usuários");

        if (acessoTotal) {
            System.out.println("4 - Cadastrar Funcionário");
            System.out.println("5 - Cadastrar Administrador");
            System.out.println("6 - Atualizar Usuário");
            System.out.println("7 - Desativar Usuário");
        }

        System.out.println("0 - Voltar");
        System.out.print("Opção: ");
    }

    private void executarOpcao(int opcao) {
        switch (opcao) {

            case 1 -> cadastrarCliente();

            case 2 -> buscarUsuario();

            case 3 -> listarUsuarios();

            case 4 -> executarComAcessoTotal(this::cadastrarFuncionario);

            case 5 -> executarComAcessoTotal(this::cadastrarAdministrador);

            case 6 -> executarComAcessoTotal(this::atualizarUsuario);

            case 7 -> executarComAcessoTotal(this::desativarUsuario);

            case 0 -> System.out.println("Retornando...");

            default -> System.out.println("Opção inválida");
        }
    }

    private void executarComAcessoTotal(Runnable acao) {
        if (acessoTotal) {
            acao.run();
        }
    }

    private String[] lerDados() {
        System.out.print(PRINT_NOME);
        String nome = scanner.nextLine().trim();

        System.out.print(PRINT_EMAIL);
        String email = ValidaEntrada.lerEmail(scanner);

        System.out.print(PRINT_CPF);
        String cpf = ValidaEntrada.lerCpf(scanner);

        System.out.print(PRINT_SENHA);
        String senha = scanner.nextLine().trim();

        return new String[]{nome, email, cpf, senha};
    }

    private void verificaCadastro(Usuario usuario, String tipo) {
        if (sistema.cadastrarUsuario(usuario)) {
            System.out.println(tipo + " cadastrado com sucesso. ID: " + usuario.getId());
        } else {
            System.out.println(PRINT_USUARIO_INVALIDO);
        }
    }

    public void cadastrarCliente() {
        System.out.println("\n=== CADASTRAR CLIENTE ===");
        String[] dados = lerDados();
        int id = sistema.gerarProximoIdUsuario();
        Cliente cliente = new Cliente(id, dados[0], dados[1], dados[2], dados[3]);
        verificaCadastro(cliente, "Cliente");
    }

    public void cadastrarFuncionario() {
        System.out.println("\n=== CADASTRAR FUNCIONÁRIO ===");
        String[] dados = lerDados();
        int id = sistema.gerarProximoIdUsuario();
        Funcionario funcionario = new Funcionario(id, dados[0], dados[1], dados[2], dados[3]);
        verificaCadastro(funcionario, "Funcionário");
    }

    public void cadastrarAdministrador() {
        System.out.println("\n=== CADASTRAR ADMINISTRADOR ===");
        String[] dados = lerDados();
        int id = sistema.gerarProximoIdUsuario();
        Administrador administrador = new Administrador(id, dados[0], dados[1], dados[2], dados[3]);
        verificaCadastro(administrador, "Administrador");
    }

    public void buscarUsuario() {
        System.out.print(PRINT_ID_USUARIO);
        int id = ValidaEntrada.lerInteiro(scanner);

        Usuario usuario = sistema.buscarUsuario(id);

        if (usuario == null) {
            System.out.println("Usuário não encontrado");
        } else {
            System.out.println("\n===== DADOS DO USUÁRIO =====");
            System.out.println("ID:     " + usuario.getId());
            System.out.println("Nome:   " + usuario.getNome());
            System.out.println("Email:  " + usuario.getEmail());
            System.out.println("CPF:    " + usuario.getCpf());
            System.out.println("Perfil: " + usuario.getClass().getSimpleName());
            System.out.println("Ativo:  " + usuario.isAtivo());
        }
    }

    public void listarUsuarios() {
        List<Usuario> usuarios = sistema.listarUsuarios();

        System.out.println("\n===== USUÁRIOS CADASTRADOS =====");

        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado");
        } else {
            System.out.printf("%-5s %-25s %-30s %-15s %-6s%n","ID", "NOME", "EMAIL", "PERFIL", "ATIVO");
            System.out.println("-".repeat(85));

            for (Usuario usuario : usuarios) {
                System.out.printf("%-5d %-25s %-30s %-15s %-6s%n",usuario.getId(),usuario.getNome(),usuario.getEmail(),usuario.getClass().getSimpleName(),usuario.isAtivo());
            }
        }
    }

    public void atualizarUsuario() {
        System.out.print(PRINT_ID_USUARIO);
        int id = ValidaEntrada.lerInteiro(scanner);

        Usuario usuario = sistema.buscarUsuario(id);

        if (usuario == null) {
            System.out.println("Usuário não encontrado");
        } else{
            System.out.println("Deixe em branco para manter o valor atual");

            System.out.print("Novo nome [" + usuario.getNome() + "]: ");
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()) nome = usuario.getNome();

            System.out.print("Novo email [" + usuario.getEmail() + "]: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) email = usuario.getEmail();

            System.out.print("Nova senha: ");
            String senha = scanner.nextLine().trim();
            if (senha.isEmpty()) senha = usuario.getSenha();

            if (sistema.atualizarUsuario(id, nome, email, senha)) {
                System.out.println("Usuário atualizado com sucesso");
            } else {
                System.out.println("Erro ao atualizar usuário");
            }
        }
    }

    public void desativarUsuario() {
        System.out.print(PRINT_ID_USUARIO);
        int id = ValidaEntrada.lerInteiro(scanner);

        if (sistema.desativaUsuario(id)) {
            System.out.println("Usuário desativado com sucesso");
        } else {
            System.out.println("Não foi possível desativar. Verifique se o usuário existe ou possui multas pendentes");
        }
    }
}