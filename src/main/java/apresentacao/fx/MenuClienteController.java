package apresentacao.fx;


import java.util.List;
import java.util.Optional;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
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
	
	@FXML private ComboBox<String> filtroTipo; 
	@FXML private ComboBox<String> filtroCampo;
	
	@FXML private Label bemVindo;
	private Cliente cliente;

	@Override
	protected String getTitulo() {
		return "Menu Cliente";
	}

	@Override
	protected void aoExibir() {
		bemVindo.setText("Bem-vindo(a), " + cliente.getNome());
		tabelaItens.setPlaceholder(new Label("Nenhum item disponível"));
		tabelaContratos.setPlaceholder(new Label ("Você não tem aluguéis ativos"));
		
		filtroTipo.valueProperty().addListener((obs, old, novo) -> {
		    switch (novo) {
		        case "Itens"      -> filtroCampo.getItems().setAll("ID", "Nome", "Categoria", "Taxa");
		        case "Aluguéis"-> filtroCampo.getItems().setAll("ID", "Item", "Data de Retirada", "Data de Devolução", "Status", "Valor total", "Valor multa");
		    }
		    filtroCampo.getSelectionModel().clearSelection();
		});
		
		pesquisa.textProperty().addListener((obs, old, novo) -> {if (filtroTipo.getValue().equals("Itens") ) {pesquisaItens();} 
		else if (filtroTipo.getValue().equals("Aluguéis")) {pesquisaContratos();}
		});
		
		listarItensDisponiveis();
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
	
	private void pesquisaItens() {
	    String pesquisado = pesquisa.getText();
	    String campo = filtroCampo.getValue();

	    if (campo == null) {
	        tabelaItens.getItems().setAll(sistema.listarItens());
	        return;
	    }

	    switch (campo) {
	        case "ID" -> tabelaItens.getItems().setAll(sistema.filtraID(pesquisado));
	        case "Nome" -> tabelaItens.getItems().setAll(sistema.filtraNome(pesquisado));
	        case "Categoria" -> tabelaItens.getItems().setAll(sistema.filtraCategoria(pesquisado));
	        case "Taxa" -> tabelaItens.getItems().setAll(sistema.filtraTaxa(pesquisado));
	    }
	}
	
	private void pesquisaContratos() {}
	
	private void exibirContratos(List<ContratoAluguel> lista, String msgVazio) {
		mostrarTabelas(true);
		tabelaContratos.setItems(FXCollections.observableArrayList(lista));
		}
	
	@FXML
	private void listarItensDisponiveis() {

		mostrarTabelas(false);

		List<Item> disponiveis = sistema.listarItens().stream()
				.filter(Item::estaDisponivel)
				.toList();
			tabelaItens.setItems(FXCollections.observableArrayList(disponiveis));
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


	private List<ContratoAluguel> buscarMultasPendentes() {
		return sistema.listarContratos().stream()
				.filter(contrato -> contrato.getCliente().getId() == cliente.getId()
						&& contrato.getValorMulta() > 0
						&& !contrato.isMultaPaga())
				.toList();
	}
	
	@FXML
	private void aoLimparPesquisa() {
		
	}
}