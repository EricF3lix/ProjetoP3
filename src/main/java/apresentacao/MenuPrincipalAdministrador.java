package apresentacao;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import entidades.Administrador;
import facade.SistemaFacade;

public class MenuPrincipalAdministrador {

    private SistemaFacade sistema;
    private Administrador administrador;
    private Scanner scanner;
    private PrintStream out;

    public MenuPrincipalAdministrador(SistemaFacade sistema, Administrador administrador, Scanner scanner, PrintStream out) {
        this.sistema = sistema;
        this.administrador = administrador;
        this.scanner = scanner;
        this.out = out;
    }

    public MenuPrincipalAdministrador(SistemaFacade sistema, Administrador administrador, Scanner scanner) {
        this(sistema, administrador, scanner, new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    public void exibir() {

        int opcao;

        do {

            out.println("\n===================================");
            out.println("       MENU ADMINISTRADOR         ");
            out.println("===================================");
            out.println("Bem vindo, " + administrador.getNome());
            out.println("1 - Usuários");
            out.println("2 - Itens");
            out.println("3 - Categorias");
            out.println("4 - Fornecedores");
            out.println("5 - Contratos / Aluguéis");
            out.println("6 - Multas");
            out.println("7 - Relatórios");
            out.println("0 - Sair");
            out.print("Opção: ");

            opcao = ValidaEntrada.lerInteiro(scanner);

            switch (opcao) {

                case 1:
                    new MenuUsuarios(sistema, scanner, true).exibir();
                    break;

                case 2:
                    new MenuItens(sistema, scanner).exibir();
                    break;

                case 3:
                    new MenuCategorias(sistema, scanner).exibir();
                    break;

                case 4:
                    new MenuFornecedores(sistema, scanner).exibir();
                    break;

                case 5:
                    new MenuContratos(sistema, scanner).exibir();
                    break;

                case 6:
                    new MenuMultas(sistema, scanner).exibir();
                    break;

                case 7:
                    new MenuRelatorios(sistema, scanner).exibir();
                    break;

                case 0:
                    out.println("Sessão encerrada.");
                    break;

                default:
                    out.println("Opção inválida.");
            }

        } while (opcao != 0);
    }
}