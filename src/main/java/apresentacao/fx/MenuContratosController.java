package apresentacao.fx;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import javafx.fxml.FXML;

public class MenuContratosController extends MenuController {

    private static final String ID_CONTRATO = "ID do contrato:";
    private static final String CONTRATO_NAO_ENCONTRADO = "Contrato não encontrado";

    @Override
    protected String getTitulo() {
        return "Gerenciar Contratos";
    }

    @FXML
    private void registrarAluguel() {

        Optional<Integer> idCliente = pedirInteiro("ID do cliente:");
        if (idCliente.isEmpty()) return;

        Usuario usuario = sistema.buscarUsuario(idCliente.get());
        if (!(usuario instanceof Cliente cliente)) {
            erro("Cliente não encontrado");
            return;
        }

        if (sistema.clientePossuiMultaPendente(cliente.getId())) {
            erro("Cliente possui multa pendente e não pode realizar novos aluguéis");
            return;
        }

        Optional<Integer> idItem = pedirInteiro("ID do item:");
        if (idItem.isEmpty()) return;

        Item item = sistema.buscarItem(idItem.get());
        if (item == null) {
            erro("Item não encontrado");
            return;
        }

        if (!item.estaDisponivel()) {
            erro("Item indisponível para aluguel. Status atual: " + item.getStatus());
            return;
        }

        Optional<String> dataRetirada = pedirData("Data de retirada");
        if (dataRetirada.isEmpty()) return;

        Optional<String> dataDevolucao = pedirData("Data de devolução prevista");
        if (dataDevolucao.isEmpty()) return;

        long quantidadeDias = ChronoUnit.DAYS.between(
                LocalDate.parse(dataRetirada.get()),
                LocalDate.parse(dataDevolucao.get()));

        if (quantidadeDias <= 0) {
            erro("A data de devolução deve ser posterior à data de retirada");
            return;
        }

        double valorTotal = quantidadeDias * item.getTaxaDiaria();
        int idContrato = sistema.gerarProximoIdContrato();

        ContratoAluguel contrato = new ContratoAluguel(
                idContrato, cliente, item, dataRetirada.get(), dataDevolucao.get(), valorTotal);

        if (!sistema.cadastrarContrato(contrato)) {
            erro("Erro ao registrar aluguel");
            return;
        }

        item.alugar();

        limpar();
        escrever("=== ALUGUEL REGISTRADO COM SUCESSO ===");
        escrever("Contrato ID:   " + idContrato);
        escrever("Cliente:       " + cliente.getNome());
        escrever("Item:          " + item.getNome());
        escreverf("Período:       %d dia(s)", quantidadeDias);
        escreverf("Taxa diária:   R$ %.2f", item.getTaxaDiaria());
        escreverf("Valor total:   R$ %.2f", valorTotal);
    }

    @FXML
    private void buscarContrato() {
        ContratoAluguel contrato = pedirContrato();
        if (contrato == null) return;

        limpar();
        exibirDetalhesContrato(contrato);
    }

    @FXML
    private void listarContratos() {
        List<ContratoAluguel> contratos = sistema.listarContratos();

        limpar();
        escrever("===== CONTRATOS CADASTRADOS =====");

        if (contratos.isEmpty()) {
            escrever("Nenhum contrato cadastrado");
            return;
        }

        escreverf("%-5s %-22s %-22s %-12s %-10s", "ID", "CLIENTE", "ITEM", "STATUS", "VALOR");
        escrever("-".repeat(80));

        for (ContratoAluguel c : contratos) {
            escreverf("%-5d %-22s %-22s %-12s R$%.2f",
                    c.getId(), c.getCliente().getNome(), c.getItem().getNome(),
                    c.getStatus(), c.getValorTotal());
        }
    }

    @FXML
    private void finalizarContrato() {
        ContratoAluguel contrato = pedirContrato();
        if (contrato == null) return;

        if (!contrato.estaAtivo() || contrato.estaFinalizado()) {
            erro("Este contrato não está ativo. Status: " + contrato.getStatus());
            return;
        }

        Optional<String> dataReal = pedirData("Data real da devolução");
        if (dataReal.isEmpty()) return;

        LocalDate prevista = LocalDate.parse(contrato.getDataDevolucaoPrevista());
        LocalDate real = LocalDate.parse(dataReal.get());

        contrato.setDataDevolucaoReal(dataReal.get());

        long diasAtraso = ChronoUnit.DAYS.between(prevista, real);

        limpar();

        if (diasAtraso > 0) {
            double multa = sistema.calcularMulta(diasAtraso, contrato.getItem().getTaxaDiaria());

            contrato.setValorMulta(multa);
            contrato.setMultaPaga(false);

            escrever("=== MULTA POR ATRASO ===");
            escrever("Dias de atraso: " + diasAtraso);
            escreverf("Multa total:    R$ %.2f", multa);
        } else {
            escrever("Devolução dentro do prazo");
        }

        contrato.getItem().devolver();

        if (sistema.finalizarContrato(contrato.getId())) {
            info("Contrato finalizado. Item devolvido ao estoque");
        } else {
            erro("Erro ao finalizar contrato");
        }
    }

    @FXML
    private void cancelarContrato() {
        ContratoAluguel contrato = pedirContrato();
        if (contrato == null) return;

        if (contrato.getStatus().equals("FINALIZADO")) {
            erro("Não é possível cancelar um contrato já finalizado");
           
        } else {

        sistema.cancelarContrato(contrato.getId());
        contrato.getItem().devolver();
        info("Contrato cancelado. Item devolvido ao estoque");
        }
    }

    
    private ContratoAluguel pedirContrato() {
        Optional<Integer> id = pedirInteiro(ID_CONTRATO);
        if (id.isEmpty()) return null;

        ContratoAluguel contrato = sistema.buscarContrato(id.get());
        if (contrato == null) {
            erro(CONTRATO_NAO_ENCONTRADO);
        }
        return contrato;
    }

    private void exibirDetalhesContrato(ContratoAluguel contrato) {
        String devolucaoReal = contrato.getDataDevolucaoReal();
        boolean semDevolucao = devolucaoReal == null || devolucaoReal.isBlank();

        escrever("===== DETALHES DO CONTRATO =====");
        escrever("ID:                 " + contrato.getId());
        escrever("Cliente:            " + contrato.getCliente().getNome());
        escrever("Item:               " + contrato.getItem().getNome());
        escrever("Data de retirada:   " + contrato.getDataRetirada());
        escrever("Devolução prevista: " + contrato.getDataDevolucaoPrevista());
        escrever("Devolução real:     " + (semDevolucao ? "-" : devolucaoReal));
        escrever("Status:             " + contrato.getStatus());
        escreverf("Valor total:        R$ %.2f", contrato.getValorTotal());
        escreverf("Valor multa:        R$ %.2f", contrato.getValorMulta());
        escrever("Multa paga:         " + contrato.isMultaPaga());
    }
}