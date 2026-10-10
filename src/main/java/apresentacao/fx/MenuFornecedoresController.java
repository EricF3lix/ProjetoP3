package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Fornecedor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class MenuFornecedoresController extends MenuController{

	private static final String ID = "Digite o ID do fornecedor: ";
	private static final String NAO_ENCONTRADO = "Fornecedor não encontrado.";
	@FXML private TableView<Fornecedor> tabela;
	private boolean listener = false;
	
	@Override
	protected String getTitulo() {
		return("Gerenciar Fornecedores");
	}
	
	@Override
	protected void aoExibir() {
		tabela.setPlaceholder(new Label(""));
		if (!listener) {
			pesquisa.textProperty().addListener((obj, antigo, novo) -> {fazPesquisa(novo);});
			this.listener = true;
		}
	}
	
	@FXML
	private void cadastrarFornecedor() {
		Optional<String> nomeFornecedor = pedirTexto("Razão Social (Nome): ");
		if (nomeFornecedor.isEmpty()) {return;}
		
		Optional<String> cnpjFornecedor = pedirTexto("CNPJ: ");
		if (cnpjFornecedor.isEmpty()) {return;}
		
		Optional<String> emailFornecedor = pedirTexto("Email: ");
		if (emailFornecedor.isEmpty()) {return;}
		
		Optional<String> telefoneFornecedor = pedirTexto("Telefone: ");
		if (telefoneFornecedor.isEmpty()) {return;}
		
		int id = sistema.gerarProximoIdFornecedor();
		Fornecedor fornecedor = new Fornecedor(id, nomeFornecedor.get(), cnpjFornecedor.get(), emailFornecedor.get(), telefoneFornecedor.get());
		
		if(sistema.cadastrarFornecedor(fornecedor)) {
			info("Fornecedor cadastrado com sucesso! ID: " + id);
			mostraFornecedor(id);
		}
		else {
			info("Fornecedor já cadastrado!");
		}
		
	}
	
	@FXML
	private void buscarFornecedor() {
		Optional<Integer> idFornecedor = pedirInteiro(ID);
		if(idFornecedor.isEmpty()) { return;
		}
		
		mostraFornecedor(idFornecedor.get());
		
	}
	
	private void fazPesquisa(String pesquisado) {
		List<Fornecedor> fornecedoresFiltrado = sistema.filtrarFornecedor(pesquisado);
		if (fornecedoresFiltrado.isEmpty()) {
			return;
		}
		tabela.setItems(FXCollections.observableArrayList(fornecedoresFiltrado));
	}
	
	@FXML
	private void listarFornecedores() {
		List<Fornecedor> listaFornecedores = sistema.listarFornecedores();
		
		limpar();
		if (listaFornecedores.isEmpty()) {
			return;
		}
		
		tabela.setItems(FXCollections.observableArrayList(listaFornecedores));
		
	}
	
	@FXML
	private void atualizarFornecedor() {
		Fornecedor atual = tabela.getSelectionModel().getSelectedItem();
		tabela.getSelectionModel().clearSelection();
		Optional<Integer> id;
		
		if (atual == null) {
			id = pedirInteiro(ID);
		    if (id.isEmpty()) return;
		    atual = sistema.buscarFornecedor(id.get());
		    if (atual == null) {
		        erro(NAO_ENCONTRADO);
		        return;
		    }
		}
		
		id = Optional.of(atual.getId());

	    Optional<String> nome = pedirTexto("Razão Social (Nome):", atual.getRazaoSocial());
	    if (nome.isEmpty()) return;
	    
	    Optional<String> cnpj = pedirTexto("CNPJ:", atual.getCnpj());
	    if (cnpj.isEmpty()) return;
	    
	    Optional<String> email = pedirTexto("Email:", atual.getEmail());
	    if (email.isEmpty()) return;
	    
	    Optional<String> telefone = pedirTexto("Telefone:", atual.getTelefone());
	    if (telefone.isEmpty()) return;

	    String novoNome = nome.get().isEmpty() ? atual.getRazaoSocial() : nome.get();
	    String novoCnpj = cnpj.get().isEmpty() ? atual.getCnpj() : cnpj.get();
	    String novoEmail = email.get().isEmpty() ? atual.getEmail() : email.get();
	    String novoTelefone = telefone.get().isEmpty() ? atual.getTelefone() : telefone.get();

	    if (sistema.atualizarFornecedor(new Fornecedor(id.get(), novoNome, novoCnpj, novoEmail, novoTelefone))) {
	        info("Fornecedor atualizado com sucesso.");
	        mostraFornecedor(id.get());
	    } else {
	        erro("Erro ao atualizar fornecedor.");
	    }
	}

	@FXML
	private void desativarFornecedor() {
		Fornecedor fornecedor = tabela.getSelectionModel().getSelectedItem();
		tabela.getSelectionModel().clearSelection();
		Optional<Integer> id;
		
		if (fornecedor != null) {
			id = Optional.of(fornecedor.getId());
		}
		
		else {
			id = pedirInteiro(ID);
			if (id.isEmpty()) return;
		    fornecedor = sistema.buscarFornecedor(id.get());
		    if (fornecedor == null) {
		        erro(NAO_ENCONTRADO);
		        return;
		    }
		}
	    

	    if (!confirmar("Tem certeza que deseja desativar \"" + fornecedor.getRazaoSocial() + "\"?")) {
	        return;
	    }

	    if (sistema.desativarFornecedor(id.get())) {
	        info("Fornecedor desativado com sucesso");
	        mostraFornecedor(id.get());
	    } else {
	        erro("Erro ao desativar fornecedor");
	    }
	}


private void mostraFornecedor(int id) {
	
	Fornecedor fornecedor = sistema.buscarFornecedor(id);
	if (fornecedor == null) {
		info("Fornecedor não cadastrado.");
		return;
	}
	
	tabela.getItems().setAll(fornecedor);
	
}
}