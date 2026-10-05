package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuClienteController extends MenuController {

	private static final String NENHUM_ITEM = "Nenhum item disponível no momento";
	private static final String NENHUM_ALUGUEL = "Nenhum aluguel ativo";
	private static final String NENHUM_CONTRATO = "Nenhum contrato encontrado";
	private static final String NENHUMA_MULTA = "Nenhuma multa pendente";
	private static final String SEM_MULTAS_PENDENTES = "Você não possui multas pendentes";
	private static final String CONTRATO_NAO_ENCONTRADO = "Contrato não encontrado";
	private static final String CONTRATO_SEM_MULTA = "Este contrato não possui multa pendente";
	private static final String MULTA_PAGA = "Multa paga com sucesso!";
	private static final String ERRO_PAGAMENTO = "Erro ao processar pagamento";
	private static final String ID_CONTRATO = "Digite o ID do contrato para quitar a multa: ";

	@FXML private Label bemVindo;
	private Cliente cliente;

	@Override
	protected String getTitulo() {
		return "Menu Cliente";
	}

	@Override
	protected void aoExibir() {
		bemVindo.setText("Bem-vindo(a), " + cliente.getNome());
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@FXML
	private void listarItensDisponiveis() {
		limpar();
		escrever("===== ITENS DISPONÍVEIS =====");
		escreverf("%-5s %-25s %-20s %-10s", "ID", "NOME", "CATEGORIA", "TAXA/DIA");
		escrever("-".repeat(65));

		boolean encontrou = false;

		for (Item item : sistema.listarItens()) {
			if (item.estaDisponivel()) {
				escreverf("%-5d %-25s %-20s R$%.2f",
						item.getId(),
						item.getNome(),
						item.getCategoria() != null ? item.getCategoria().getNome() : "-",
						item.getTaxaDiaria());
				encontrou = true;
			}
		}

		if (!encontrou) {
			escrever(NENHUM_ITEM);
		}
	}

	@FXML
	private void listarAlugueisAtivos() {
		limpar();
		escrever("===== MEUS ALUGUÉIS ATIVOS =====");

		boolean encontrou = false;

		for (ContratoAluguel contrato : sistema.listarContratos()) {
			if (contrato.getCliente().getId() == cliente.getId() && contrato.estaAtivo()) {
				escrever("Contrato ID:        " + contrato.getId());
				escrever("Item:               " + contrato.getItem().getNome());
				escrever("Retirada:           " + contrato.getDataRetirada());
				escrever("Devolução prevista: " + contrato.getDataDevolucaoPrevista());
				escreverf("Valor total:        R$ %.2f", contrato.getValorTotal());
				escrever("-".repeat(40));
				encontrou = true;
			}
		}

		if (!encontrou) {
			escrever(NENHUM_ALUGUEL);
		}
	}

	@FXML
	private void historicoAlugueis() {
		limpar();
		escrever("===== HISTÓRICO DE ALUGUÉIS =====");
		escreverf("%-5s %-22s %-12s %-10s %-10s", "ID", "ITEM", "STATUS", "VALOR", "MULTA");
		escrever("-".repeat(65));

		boolean encontrou = false;

		for (ContratoAluguel contrato : sistema.listarContratos()) {
			if (contrato.getCliente().getId() == cliente.getId()) {
				escreverf("%-5d %-22s %-12s R$%-8.2f R$%.2f",
						contrato.getId(),
						contrato.getItem().getNome(),
						contrato.getStatus(),
						contrato.getValorTotal(),
						contrato.getValorMulta());
				encontrou = true;
			}
		}

		if (!encontrou) {
			escrever(NENHUM_CONTRATO);
		}
	}

	@FXML
	private void multasPendentes() {
		limpar();
		escrever("===== MULTAS PENDENTES =====");

		if (!listarMultasPendentes()) {
			escrever(NENHUMA_MULTA);
		}
	}

	@FXML
	private void pagarMulta() {
		limpar();
		escrever("===== PAGAR MULTA =====");

		if (!listarMultasPendentes()) {
			info(SEM_MULTAS_PENDENTES);
			return;
		}

		Optional<Integer> id = pedirInteiro(ID_CONTRATO);
		if (id.isEmpty()) return;

		ContratoAluguel contrato = sistema.buscarContrato(id.get());

		if (contrato == null || contrato.getCliente().getId() != cliente.getId()) {
			erro(CONTRATO_NAO_ENCONTRADO);
			return;
		}

		if (contrato.getValorMulta() <= 0 || contrato.isMultaPaga()) {
			erro(CONTRATO_SEM_MULTA);
			return;
		}

		if (!confirmar(String.format("Confirma o pagamento de R$ %.2f?", contrato.getValorMulta()))) {
			return;
		}

		if (sistema.quitarMultaContrato(id.get())) {
			info(MULTA_PAGA);
			multasPendentes();
		} else {
			erro(ERRO_PAGAMENTO);
		}
	}

	private boolean listarMultasPendentes() {
		boolean temPendente = false;

		for (ContratoAluguel contrato : sistema.listarContratos()) {
			if (contrato.getCliente().getId() == cliente.getId()
					&& contrato.getValorMulta() > 0
					&& !contrato.isMultaPaga()) {
				escrever("Contrato ID: " + contrato.getId());
				escrever("Item:        " + contrato.getItem().getNome());
				escreverf("Multa:       R$ %.2f", contrato.getValorMulta());
				escrever("-".repeat(35));
				temPendente = true;
			}
		}

		return temPendente;
	}
}