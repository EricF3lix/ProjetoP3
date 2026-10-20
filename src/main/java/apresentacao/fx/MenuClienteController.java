package apresentacao.fx;


import java.util.List;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class MenuClienteController extends MenuController {

	private static final String NENHUM_ALUGUEL = "Nenhum aluguel ativo";
	private static final String NENHUM_CONTRATO = "Nenhum contrato encontrado";
	private static final String NENHUMA_MULTA = "Nenhuma multa pendente";


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
		    if (novo.equals("Itens")) { filtroCampo.getItems().setAll("ID", "Nome", "Categoria", "Taxa"); listarItensDisponiveis(); }
		    else if (novo.equals("Aluguéis")) { filtroCampo.getItems().setAll("ID", "Item", "Data de Retirada", "Data de Devolução", "Status", "Valor total", "Valor multa"); historicoAlugueis(); }	    
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
		mostrarTabelas(false);
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
	
	private void pesquisaContratos() {
		mostrarTabelas(true);
	    String pesquisado = pesquisa.getText();
	    String campo = filtroCampo.getValue();

	    if (campo == null) {
	        tabelaContratos.getItems().setAll(sistema.listarContratos());
	        return;
	    }

	    switch (campo) {
	        case "ID" -> tabelaContratos.getItems().setAll(sistema.filtraIDContrato(pesquisado));
	        case "Item" -> tabelaContratos.getItems().setAll(sistema.filtraItemContrato(pesquisado));
	        case "Data de Retirada" -> tabelaContratos.getItems().setAll(sistema.filtraDataRetiradaContrato(pesquisado));
	        case "Data de Devolução" -> tabelaContratos.getItems().setAll(sistema.filtraDataDevolucaoContrato(pesquisado));
	        case "Status" -> tabelaContratos.getItems().setAll(sistema.filtraStatusContrato(pesquisado));
	        case "Valor total" -> tabelaContratos.getItems().setAll(sistema.filtraValorTotalContrato(pesquisado));
	        case "Valor multa" -> tabelaContratos.getItems().setAll(sistema.filtraValorMultaContrato(pesquisado));
	    }
	}
	
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
	    pesquisa.clear();
	    filtroCampo.getSelectionModel().clearSelection();
	    tabelaItens.getItems().clear();
	    tabelaContratos.getItems().clear();
	}
}