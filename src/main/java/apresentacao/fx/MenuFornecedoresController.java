package apresentacao.fx;

import java.util.Optional;

import entidades.Fornecedor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;

public class MenuFornecedoresController extends MenuController{

	private static final String ID = "Digite o ID do fornecedor: ";
	private static final String NAO_ENCONTRADO = "Fornecedor não encontrado.";
	
	@FXML private TableView<Fornecedor> tabela;
	@FXML private ComboBox<String> filtro;
	@FXML private TableColumn<Fornecedor, String> colunaStatus;
	
	@FXML private VBox dadosFornecedor;
	@FXML private VBox tabelaFornecedor;
	
	@FXML private Label tituloFornecedor;
	@FXML private Label tituloTabela;
	
	@FXML private TextField txtNome;
	@FXML private TextField txtCNPJ;
	@FXML private TextField txtEmail;
	@FXML private TextField txtTelefone;
	
	@FXML private Button btnCadastrar;
	@FXML private Button btnAtualizar;
	
	private boolean listener = false;
	
	@Override
	protected String getTitulo() {
		return("Gerenciar Fornecedores");
	}
	
	@Override
	protected void aoExibir() {
		tabela.setPlaceholder(new Label("Nenhum fornecedor cadastrado"));
		if (!listener) {
			pesquisa.textProperty().addListener((obj, antigo, novo) -> fazPesquisa(novo));
			this.listener = true;
			
			colunaStatus.setCellFactory(c -> new TableCell<Fornecedor, String>() {
			    private final Label badge = new Label();

			    @Override
			    protected void updateItem(String status, boolean vazio) {
			        super.updateItem(status, vazio);
			        if (vazio || status == null) {
			            setGraphic(null);
			            setText(null);
			            return;
			        }
			        boolean ativo = status.equals("ATIVO"); 
			        configuraBadge(badge, ativo);
			        setGraphic(badge);
			        setText(null);
			    }
			});
			
		}
		filtro.setValue("Todos");
		listarFornecedores();
	}
	
	private void configuraBadge(Label badge, boolean ativo) {
		badge.setText(ativo ? "● Ativo" : "● Inativo");
        badge.setStyle(
            "-fx-background-color: " + (ativo ? "#d4edda" : "#f8d7da") + "; " +
            "-fx-text-fill: " + (ativo ? "#155724" : "#721c24") + "; " +
            "-fx-font-weight: bold; -fx-padding: 3 8 3 8; " +
            "-fx-background-radius: 10; -fx-font-size: 11px;");
	}
	
	private void tiraTabela() {
		
		tabelaFornecedor.setVisible(false);
		tabelaFornecedor.setManaged(false);
		
		dadosFornecedor.setVisible(true);
		dadosFornecedor.setManaged(true);
	}
	
	private void colocaTabela() {
		tabelaFornecedor.setVisible(true);
		tabelaFornecedor.setManaged(true);
		
		dadosFornecedor.setVisible(false);
		dadosFornecedor.setManaged(false);
	}
	
	@FXML private void trocaCadastro() {
		tiraTabela();
		
		tituloFornecedor.setText("Cadastrar Fornecedor");
		
		btnCadastrar.setVisible(true);
		btnCadastrar.setManaged(true);
		
		btnAtualizar.setVisible(false);
		btnAtualizar.setManaged(false);
		
		txtNome.clear();
		txtCNPJ.clear();
		txtEmail.clear();
		txtTelefone.clear();
	}
	
	@FXML private void trocaAtualizar() {
		Fornecedor alvo = obterFornecedorAlvo(); 
		if (alvo == null) return;    

		tiraTabela();
		tituloFornecedor.setText("Atualizar Fornecedor");

		btnCadastrar.setVisible(false);
		btnCadastrar.setManaged(false);
		btnAtualizar.setVisible(true);
		btnAtualizar.setManaged(true);
		btnAtualizar.setUserData(alvo);

		txtNome.setText(alvo.getRazaoSocial());
		txtCNPJ.setText(alvo.getCnpj());
		txtEmail.setText(alvo.getEmail());
		txtTelefone.setText(alvo.getTelefone());
	}
	
	@FXML
	private void cadastrarFornecedor() {
		String nomeFornecedor = txtNome.getText().trim();
		String cnpjFornecedor = txtCNPJ.getText().trim();
		String emailFornecedor = txtEmail.getText().trim();
		String telefoneFornecedor = txtTelefone.getText().trim();

		if (nomeFornecedor.isEmpty() || cnpjFornecedor.isEmpty() || emailFornecedor.isEmpty() || telefoneFornecedor.isEmpty()) {
			erro("Preencha todos os campos!");
			return;
		}
		
		int id = sistema.gerarProximoIdFornecedor();
		Fornecedor fornecedor = new Fornecedor(id, nomeFornecedor, cnpjFornecedor, emailFornecedor, telefoneFornecedor);
		
		if(sistema.cadastrarFornecedor(fornecedor)) {
			info("Fornecedor cadastrado com sucesso! ID: " + id);
			
			txtNome.clear();
			txtCNPJ.clear();
			txtEmail.clear();
			txtTelefone.clear();
			
			mostraFornecedor(id);
		}
		else {
			erro("Fornecedor já cadastrado!");
		}
		
	}
	
	
	private void fazPesquisa(String pesquisado) {
		colocaTabela();
		
		if (filtro.getValue() == null) {
			filtro.getSelectionModel().selectFirst();
		}
		
		switch(filtro.getValue()) {
		case "Todos" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarPorTodos(pesquisado)));
		case "ID" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarPorId(pesquisado)));
		case "Razão Social" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarPorNome(pesquisado)));
		case "CNPJ" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarPorCnpj(pesquisado)));
		case "E-mail" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarPorEmail(pesquisado)));
		case "Telefone" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarPorTelefone(pesquisado)));
		}
	}
	
	@FXML
	private void listarFornecedores() {
		colocaTabela();
		tabela.setItems(FXCollections.observableArrayList(sistema.listarFornecedores()));
		
	}
	
	@FXML
	private void atualizarFornecedor() {
		Fornecedor atual = (Fornecedor) btnAtualizar.getUserData();
		if (atual == null) return;

		String nome = txtNome.getText().trim();
		String cnpj = txtCNPJ.getText().trim();
		String email = txtEmail.getText().trim();
		String telefone = txtTelefone.getText().trim();

		if (nome.isEmpty() || cnpj.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
			erro("Preencha todos os campos!");
			return;
		}

		int id = atual.getId();

		if (sistema.atualizarFornecedor(new Fornecedor(id, nome, cnpj, email, telefone))) {
			info("Fornecedor atualizado com sucesso.");
			btnAtualizar.setUserData(null);
			txtNome.clear();
			txtCNPJ.clear();
			txtEmail.clear();
			txtTelefone.clear();
			mostraFornecedor(id);
		} else {
			erro("Erro ao atualizar fornecedor.");
		}
	}
	
	@FXML
	private void desativarFornecedor() {
		Fornecedor fornecedor = obterFornecedorAlvo();
		if (fornecedor == null) return;
		
		Optional<Integer> id = Optional.of(fornecedor.getId());

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
	
	private Fornecedor obterFornecedorAlvo() {
		Fornecedor atual = tabela.getSelectionModel().getSelectedItem();
		tabela.getSelectionModel().clearSelection();
		
		if (atual == null) {
			Optional<Integer> id = pedirInteiro(ID);
			if (id.isEmpty()) return null;
			atual = sistema.buscarFornecedor(id.get());
			if (atual == null) {
				erro(NAO_ENCONTRADO);
			}
		}
		return atual;
	}
	
	private void mostraFornecedor(int id) {
		Fornecedor fornecedor = sistema.buscarFornecedor(id);
		if (fornecedor == null) {
			info("Fornecedor não cadastrado.");
			return;
		}
		
		colocaTabela();
		tabela.getItems().setAll(fornecedor);
		
	}
	
	@FXML
	private void aoLimparPesquisa() {
		colocaTabela();
		pesquisa.clear();
		filtro.getSelectionModel().clearSelection();
	}
}