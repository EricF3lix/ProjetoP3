package apresentacao.fx;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

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

	@FXML private TableView<Item> tabelaItens;
	@FXML private TableView<ContratoAluguel> tabelaContratos;
	
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

	private void mostrarTabelas(boolean tipo) {
		tabelaItens.setVisible(!tipo);
		tabelaItens.setManaged(!tipo);
		
		tabelaContratos.setVisible(tipo);
		tabelaContratos.setManaged(tipo);
	}
	
	private void exibirContratos(List<ContratoAluguel> lista, String msgVazio) {
		mostrarTabelas(true);
		if (lista.isEmpty()) {
			info(msgVazio);
		}
		else {
			tabelaContratos.setItems(FXCollections.observableArrayList(lista));
		}
	}
	
	@FXML
	private void listarItensDisponiveis() {

		mostrarTabelas(false);

		List<Item> disponiveis = sistema.listarItens().stream()
				.filter(item -> item.estaDisponivel())
				.toList();

		if (disponiveis.isEmpty()) {
			info(NENHUM_ITEM);
		}
		else {
			tabelaItens.setItems(FXCollections.observableArrayList(disponiveis));
		}
	}
	
	@FXML
	private void listarAlugueisAtivos() {
		List<ContratoAluguel> contratosAtivos = sistema.listarContratos().stream()
				.filter(contrato -> contrato.getCliente().getId() == cliente.getId() && contrato.estaAtivo())
				.toList();

		exibirContratos(contratosAtivos, NENHUM_ALUGUEL);
	}

	@FXML
	private void historicoAlugueis() {
		List<ContratoAluguel> historico = sistema.listarContratos().stream()
				.filter(contrato -> contrato.getCliente().getId() == cliente.getId())
				.toList();

		exibirContratos(historico, NENHUM_CONTRATO);
	}

	@FXML
	private void multasPendentes() {
		exibirContratos(buscarMultasPendentes(), NENHUMA_MULTA);
	}

	@FXML
	private void pagarMulta() {

		mostrarTabelas(true);

		List<ContratoAluguel> pendentes = buscarMultasPendentes();

		if (pendentes.isEmpty()) {
			info(SEM_MULTAS_PENDENTES);
			return;
		}

		tabelaContratos.setItems(FXCollections.observableArrayList(pendentes));

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

	private List<ContratoAluguel> buscarMultasPendentes() {
		return sistema.listarContratos().stream()
				.filter(contrato -> contrato.getCliente().getId() == cliente.getId()
						&& contrato.getValorMulta() > 0
						&& !contrato.isMultaPaga())
				.toList();
	}
}