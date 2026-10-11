package apresentacao;

import java.util.Scanner;

import entidades.Funcionario;
import facade.SistemaFacade;

@SuppressWarnings("java:S106")
public class MenuPrincipalFuncionario {

    private SistemaFacade sistema;
    private Funcionario funcionario;
    private Scanner scanner;

    public MenuPrincipalFuncionario(SistemaFacade sistema, Funcionario funcionario, Scanner scanner) {
        this.sistema = sistema;
        this.funcionario = funcionario;
        this.scanner = scanner;
    }

}