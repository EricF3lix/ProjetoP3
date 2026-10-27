package apresentacao.fx;


import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Set;

public class MenuClienteController extends MenuController {
	
	private static final String ITENS = "Itens";
	private static final String DATA_RETIRADA = "Data de Retirada";
	private static final String DATA_DEVOLUCAO = "Data de Devolução";

	@FXML private TableView<Item> tabelaItens;
	@FXML private TableView<ContratoAluguel> tabelaContratos;
	
	@FXML private DatePicker dtPesquisa;
	
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
		    if (novo.equals(ITENS)) { trocaPesquisa(true); filtroCampo.getItems().setAll("ID", "Nome", "Categoria", "Taxa"); listarItensDisponiveis(); }
		    else if (novo.equals("Aluguéis")) { filtroCampo.getItems().setAll("ID", "Item", DATA_RETIRADA, DATA_DEVOLUCAO, "Status", "Valor total", "Valor multa"); historicoAlugueis(); }	    
		    filtroCampo.getSelectionModel().clearSelection();
		});
		
		filtroCampo.valueProperty().addListener((obs, old, novo) -> {
			if (novo == null) {
				return;
			}
			trocaPesquisa(!Set.of(DATA_DEVOLUCAO, DATA_RETIRADA).contains(novo));
		});
		
		pesquisa.textProperty().addListener((obs, old, novo) -> {if (filtroTipo.getValue().equals(ITENS) ) {pesquisaItens();} 
		else if (filtroTipo.getValue().equals("Aluguéis")) pesquisaContratos();
		});
		
		dtPesquisa.valueProperty().addListener((obs, old, novo) -> pesquisaContratos());
		
		dtPesquisa.getEditor().setStyle("-fx-background-color: transparent; -fx-padding: 8px;");
		dtPesquisa.setVisible(false);
		dtPesquisa.setManaged(false);
		
		filtroTipo.setValue(ITENS);
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
	        listarItensDisponiveis();
	        return;
	    }

	    switch (campo) {
	        case "ID" -> tabelaItens.getItems().setAll(sistema.filtraID(pesquisado));
	        case "Nome" -> tabelaItens.getItems().setAll(sistema.filtraNome(pesquisado));
	        case "Categoria" -> tabelaItens.getItems().setAll(sistema.filtraCategoria(pesquisado));
	        case "Taxa" -> tabelaItens.getItems().setAll(sistema.filtraTaxa(pesquisado));
	        default -> erro("Seleção inválida");
	    }
	}
	
	private void pesquisaContratos() {
		mostrarTabelas(true);
	    String pesquisado;
	    
	    String campo = filtroCampo.getValue();
	    
	    
	    if (campo == null) {
	    	historicoAlugueis();
	        return;
	    }
	    
	    else if (campo.equals(DATA_DEVOLUCAO) || campo.equals(DATA_RETIRADA)) {
	    	pesquisado = String.valueOf(dtPesquisa.getValue());
	    }
	    
	    else {
	    	pesquisado = pesquisa.getText();
	    }
	    
	    switch (campo) {
	        case "ID" -> tabelaContratos.getItems().setAll(sistema.filtraIDContrato(pesquisado));
	        case "Item" -> tabelaContratos.getItems().setAll(sistema.filtraItemContrato(pesquisado));
	        case DATA_RETIRADA -> tabelaContratos.getItems().setAll(sistema.filtraDataRetiradaContrato(pesquisado));
	        case DATA_DEVOLUCAO -> tabelaContratos.getItems().setAll(sistema.filtraDataDevolucaoContrato(pesquisado));
	        case "Status" -> tabelaContratos.getItems().setAll(sistema.filtraStatusContrato(pesquisado));
	        case "Valor total" -> tabelaContratos.getItems().setAll(sistema.filtraValorTotalContrato(pesquisado));
	        case "Valor multa" -> tabelaContratos.getItems().setAll(sistema.filtraValorMultaContrato(pesquisado));
	        default -> erro("Seleção inválida");
	    }
	}
	
	private void trocaPesquisa(Boolean tipo) {
		dtPesquisa.setVisible(!tipo);
		dtPesquisa.setManaged(!tipo);
		
		pesquisa.setVisible(tipo);
		pesquisa.setManaged(tipo);
	}
	
	private void exibirContratos(List<ContratoAluguel> lista) {
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

		exibirContratos(contratosAtivos);
	}

	@FXML
	private void historicoAlugueis() {
		List<ContratoAluguel> historico = sistema.listarContratos().stream()
				.filter(contrato -> contrato.getCliente().getId() == cliente.getId())
				.toList();

		exibirContratos(historico);
	}

	@FXML
	private void multasPendentes() {
		exibirContratos(buscarMultasPendentes());
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
		
	    if (pesquisa.isVisible()) {pesquisa.clear();}
	    else if (dtPesquisa.isVisible()) {dtPesquisa.setValue(null);}
	    
	    filtroCampo.getSelectionModel().clearSelection();
	    
	    if (tabelaContratos.isVisible()) {historicoAlugueis();}
	    else {listarItensDisponiveis();}
	}
}