package apresentacao.fx;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class MenuContratosController extends MenuController {

    private static final String ID_CONTRATO = "Digite o ID do contrato: ";
    private static final String CONTRATO_NAO_ENCONTRADO = "Contrato não encontrado.";

    @FXML private TableView<ContratoAluguel> tabela;

    @Override
    protected String getTitulo() {
        return "Gerenciar Contratos";
    }

    @Override
    protected void aoExibir() {
        configurarTabelaEPesquisa(tabela, this::fazPesquisa);
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
        info("Aluguel registrado com sucesso! ID do Contrato: " + idContrato);
        mostraContrato(idContrato);
    }

    @FXML
    private void buscarContrato() {
        pedirInteiro(ID_CONTRATO).ifPresent(this::mostraContrato);
    }

    private void fazPesquisa(String pesquisado) {
        List<ContratoAluguel> filtrados = sistema.filtrarContratos(pesquisado);
        tabela.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    private void listarContratos() {
        List<ContratoAluguel> contratos = sistema.listarContratos();
        if (!contratos.isEmpty()) {
            tabela.setItems(FXCollections.observableArrayList(contratos));
        }
    }

    @FXML
    private void finalizarContrato() {
        ContratoAluguel contrato = obterContratoAlvo();
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

        if (diasAtraso > 0) {
            double multa = sistema.calcularMulta(diasAtraso, contrato.getItem().getTaxaDiaria());
            contrato.setValorMulta(multa);
            contrato.setMultaPaga(false);
            info(String.format("Contrato finalizado com atraso de %d dia(s). Multa gerada: R$ %.2f", diasAtraso, multa));
        } else {
            info("Devolução realizada dentro do prazo.");
        }

        contrato.getItem().devolver();

        if (sistema.finalizarContrato(contrato.getId())) {
            mostraContrato(contrato.getId());
        } else {
            erro("Erro ao finalizar contrato");
        }
    }

    @FXML
    private void cancelarContrato() {
        ContratoAluguel contrato = obterContratoAlvo();
        if (contrato == null) return;

        if ("FINALIZADO".equalsIgnoreCase(contrato.getStatus())) {
            erro("Não é possível cancelar um contrato já finalizado");
            return;
        }

        if (!confirmar("Tem certeza que deseja cancelar o contrato ID " + contrato.getId() + "?")) {
            return;
        }

        if (sistema.cancelarContrato(contrato.getId())) {
            contrato.getItem().devolver();
            info("Contrato cancelado com sucesso. Item devolvido ao estoque.");
            mostraContrato(contrato.getId());
        } else {
            erro("Erro ao cancelar contrato.");
        }
    }

    private ContratoAluguel obterContratoAlvo() {
        ContratoAluguel selecionado = tabela.getSelectionModel().getSelectedItem();
        tabela.getSelectionModel().clearSelection();

        if (selecionado != null) {
            return selecionado;
        }

        return pedirInteiro(ID_CONTRATO)
                .map(id -> {
                    ContratoAluguel c = sistema.buscarContrato(id);
                    if (c == null) erro(CONTRATO_NAO_ENCONTRADO);
                    return c;
                })
                .orElse(null);
    }

    private void mostraContrato(int id) {
        ContratoAluguel contrato = sistema.buscarContrato(id);
        if (contrato == null) {
            info("Contrato não encontrado.");
            return;
        }
        tabela.getItems().setAll(contrato);
    }
}