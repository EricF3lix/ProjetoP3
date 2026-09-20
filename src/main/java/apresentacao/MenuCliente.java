package apresentacao;

import java.util.List;
import java.util.Scanner;

import entidades.Cliente;
import entidades.Item;
import entidades.ContratoAluguel;
import facade.SistemaFacade;

@SuppressWarnings("java:S106")
public class MenuCliente {

    private SistemaFacade sistema;
    private Cliente cliente;
    private Scanner scanner;


    private static final String PRINT_SESSAO_ENCERRADA = "Sessão encerrada";
    private static final String PRINT_OPCAO_INVALIDA = "Opção inválida";
    private static final String PRINT_NENHUM_ITEM = "Nenhum item disponível no momento";
    private static final String PRINT_NENHUM_ALUGUEL = "Nenhum aluguel ativo";
    private static final String PRINT_NENHUM_CONTRATO = "Nenhum contrato encontrado";
    private static final String PRINT_NENHUMA_MULTA = "Nenhuma multa pendente";
    private static final String PRINT_SEM_MULTAS_PENDENTES = "Você não possui multas pendentes";
    private static final String PRINT_OPERACAO_CANCELADA = "Operação cancelada";
    private static final String PRINT_CONTRATO_NAO_ENCONTRADO = "Contrato não encontrado";
    private static final String PRINT_CONTRATO_SEM_MULTA = "Este contrato não possui multa pendente";
    private static final String PRINT_MULTA_PAGA = "Multa paga com sucesso!";
    private static final String PRINT_ERRO_PAGAMENTO = "Erro ao processar pagamento";
    private static final String PRINT_ID_CONTRATO = "Digite o ID do contrato para quitar a multa (0 para cancelar): ";

    public MenuCliente(SistemaFacade sistema, Cliente cliente, Scanner scanner) {
        this.sistema = sistema;
        this.cliente = cliente;
        this.scanner = scanner;
    }

    public void exibir() {

        int opcao;

        do {

            System.out.println("\n==================================");
            System.out.println("           MENU CLIENTE           ");
            System.out.println("==================================");
            System.out.println("Bem-vindo(a), " + cliente.getNome());
            System.out.println("1 - Ver itens disponíveis");
            System.out.println("2 - Meus aluguéis ativos");
            System.out.println("3 - Histórico de aluguéis");
            System.out.println("4 - Minhas multas pendentes");
            System.out.println("5 - Pagar multa");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");

            opcao = ValidaEntrada.lerInteiro(scanner);

            switch (opcao) {

                case 1:
                    verItensDisponiveis();
                    break;

                case 2:
                    listarAlugueisAtivos();
                    break;

                case 3:
                    listarHistorico();
                    break;

                case 4:
                    consultarMultas();
                    break;

                case 5:
                    pagarMulta();
                    break;

                case 0:
                    System.out.println(PRINT_SESSAO_ENCERRADA);
                    break;

                default:
                    System.out.println(PRINT_OPCAO_INVALIDA);
            }

        } while (opcao != 0);
    }

    public void verItensDisponiveis() {

        System.out.println("\n===== ITENS DISPONÍVEIS =====");

        List<Item> itens = sistema.listarItens();

        boolean encontrou = false;

        System.out.printf("%-5s %-25s %-20s %-10s%n", "ID", "NOME", "CATEGORIA", "TAXA/DIA");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (Item item : itens) {

            if (item.estaDisponivel()) {

                System.out.printf("%-5d %-25s %-20s R$%.2f%n",
                        item.getId(),
                        item.getNome(),
                        item.getCategoria() != null ? item.getCategoria().getNome() : "-",
                        item.getTaxaDiaria());

                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println(PRINT_NENHUM_ITEM);
        }
    }

    public void listarAlugueisAtivos() {

        List<ContratoAluguel> contratos = sistema.listarContratos();

        System.out.println("\n===== MEUS ALUGUÉIS ATIVOS =====");

        boolean encontrou = false;

        for (ContratoAluguel contrato : contratos) {

            if (contrato.getCliente().getId() == cliente.getId() && contrato.estaAtivo()) {

                System.out.println("Contrato ID:        " + contrato.getId());
                System.out.println("Item:               " + contrato.getItem().getNome());
                System.out.println("Retirada:           " + contrato.getDataRetirada());
                System.out.println("Devolução prevista: " + contrato.getDataDevolucaoPrevista());
                System.out.printf("Valor total:        R$ %.2f%n", contrato.getValorTotal());
                System.out.println("-".repeat(40));

                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println(PRINT_NENHUM_ALUGUEL);
        }
    }

    public void listarHistorico() {

        List<ContratoAluguel> contratos = sistema.listarContratos();

        System.out.println("\n===== HISTÓRICO DE ALUGUÉIS =====");

        boolean encontrou = false;

        System.out.printf("%-5s %-22s %-12s %-10s %-10s%n", "ID", "ITEM", "STATUS", "VALOR", "MULTA");
        System.out.println("-------------------------------------------------------------------------------------");

        for (ContratoAluguel contrato : contratos) {

            if (contrato.getCliente().getId() == cliente.getId()) {

                System.out.printf("%-5d %-22s %-12s R$%-8.2f R$%.2f%n",
                        contrato.getId(),
                        contrato.getItem().getNome(),
                        contrato.getStatus(),
                        contrato.getValorTotal(),
                        contrato.getValorMulta());

                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println(PRINT_NENHUM_CONTRATO);
        }
    }

    public void consultarMultas() {

        List<ContratoAluguel> contratos = sistema.listarContratos();

        System.out.println("\n===== MULTAS PENDENTES =====");

        boolean encontrou = false;

        for (ContratoAluguel contrato : contratos) {

            if (contrato.getCliente().getId() == cliente.getId() && contrato.getValorMulta() > 0 && !contrato.isMultaPaga()) {
                System.out.println("Contrato ID: " + contrato.getId());
                System.out.println("Item:        " + contrato.getItem().getNome());
                System.out.printf("Multa:       R$ %.2f%n", contrato.getValorMulta());
                System.out.println("------------------------------------------------------------");

                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println(PRINT_NENHUMA_MULTA);
        }
    }

    public void pagarMulta() {

        List<ContratoAluguel> contratos = sistema.listarContratos();

        System.out.println("\n===== PAGAR MULTA =====");

        boolean temPendente = false;

        for (ContratoAluguel contrato : contratos) {

            if (contrato.getCliente().getId() == cliente.getId() && contrato.getValorMulta() > 0 && !contrato.isMultaPaga()) {

                System.out.println("Contrato ID: " + contrato.getId());
                System.out.println("Item:        " + contrato.getItem().getNome());
                System.out.printf("Multa:       R$ %.2f%n", contrato.getValorMulta());
                System.out.println("-".repeat(35));

                temPendente = true;
            }
        }

        if (!temPendente) {
            System.out.println(PRINT_SEM_MULTAS_PENDENTES);
        } else {

            System.out.print(PRINT_ID_CONTRATO);
            int idContrato = ValidaEntrada.lerInteiro(scanner);

            if (idContrato == 0) {
                System.out.println(PRINT_OPERACAO_CANCELADA);
            } else {

                ContratoAluguel contrato = sistema.buscarContrato(idContrato);

                if (contrato == null || contrato.getCliente().getId() != cliente.getId()) {
                    System.out.println(PRINT_CONTRATO_NAO_ENCONTRADO);
                } else {

                    if (contrato.getValorMulta() <= 0 || contrato.isMultaPaga()) {
                        System.out.println(PRINT_CONTRATO_SEM_MULTA);
                    } else {

                        System.out.printf("Confirma o pagamento de R$ %.2f? (1 - Sim / 0 - Não): ", contrato.getValorMulta());
                        int confirmacao = ValidaEntrada.lerInteiro(scanner);

                        if (confirmacao != 1) {
                            System.out.println(PRINT_OPERACAO_CANCELADA);
                        } else {

                            if (sistema.quitarMultaContrato(idContrato)) {
                                System.out.println(PRINT_MULTA_PAGA);
                            } else {
                                System.out.println(PRINT_ERRO_PAGAMENTO);
                            }
                        }
                    }
                }
            }
        }
    }
}