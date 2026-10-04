package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Categoria;
import entidades.Fornecedor;
import javafx.fxml.FXML;

public class MenuFornecedoresController extends MenuController{

	private static final String ID = "Digite o ID do fornecedor: ";
	private static final String NAO_ENCONTRADO = "Fornecedor não encontrado.";
	
	@Override
	protected String getTitulo() {
		return("Gerenciar Fornecedores");
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
		
		Fornecedor fornecedor = sistema.buscarFornecedor(idFornecedor.get());
		if (fornecedor == null) {
			info("Fornecedor não cadastrado.");
			return;
		}
		
		limpar();
		escrever("===== DADOS DO FORNECEDOR =====");
        escrever("ID:        " + fornecedor.getId());
        escrever("Nome:      " + fornecedor.getRazaoSocial());
        escrever("CNPJ:      " + fornecedor.getCnpj());
        escrever("Email:     " + fornecedor.getEmail());
        escrever("Telefone:  " + fornecedor.getTelefone());
        escrever("Ativo:     " + fornecedor.isAtivo());
		
		
	}
	
	@FXML
	private void listarFornecedores() {
		List<Fornecedor> listaFornecedores = sistema.listarFornecedores();
		
		limpar();
		if (listaFornecedores.isEmpty()) {
			return;
		}
		
		escreverf("%-5s %-25s %-18s %-30s %-15s %-6s", "ID", "NOME", "CNPJ", "EMAIL", "TELEFONE", "ATIVO");
		escrever("-".repeat(104));
		
		for (Fornecedor c : listaFornecedores) {
			escreverf("%-5s %-25s %-18s %-30s %-15s %-6s", c.getId(), c.getRazaoSocial(), c.getCnpj(), c.getEmail(), c.getTelefone(), c.isAtivo());
        }
		
	}
	
	@FXML
	private void atualizarFornecedor() {
	    Optional<Integer> id = pedirInteiro(ID);
	    if (id.isEmpty()) return;

	    Fornecedor atual = sistema.buscarFornecedor(id.get());
	    if (atual == null) {
	        erro(NAO_ENCONTRADO);
	        return;
	    }

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
	    } else {
	        erro("Erro ao atualizar fornecedor.");
	    }
	}

	@FXML
	private void desativarFornecedor() {
	    Optional<Integer> id = pedirInteiro(ID);
	    if (id.isEmpty()) return;

	    Fornecedor fornecedor = sistema.buscarFornecedor(id.get());
	    if (fornecedor == null) {
	        erro(NAO_ENCONTRADO);
	        return;
	    }

	    if (confirmar("Tem certeza que deseja desativar \"" + fornecedor.getRazaoSocial() + "\"?") == false) {
	        return;
	    }

	    if (sistema.desativarFornecedor(id.get())) {
	        info("Fornecedor desativado com sucesso");
	    } else {
	        erro("Erro ao desativar fornecedor");
	    }
	}
}
