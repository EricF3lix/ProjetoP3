package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class MenuUsuariosAdminController extends MenuController {

	private static final String ID = "Digite o ID do usuário: ";
	private static final String NAO_ENCONTRADO = "Usuário não encontrado.";
	private static final String PERFIL_CLIENTE = "Cliente";
	private static final String PERFIL_FUNCIONARIO = "Funcionário";
	private static final String PERFIL_ADMIN = "Administrador";

	@FXML private TableView<Usuario> tabela;
	@FXML private ComboBox<String> filtro;
	@FXML private TableColumn<Usuario, Integer> colId;
	@FXML private TableColumn<Usuario, String> colNome;
	@FXML private TableColumn<Usuario, String> colEmail;
	@FXML private TableColumn<Usuario, String> colCpf;
	@FXML private TableColumn<Usuario, String> colPerfil;
	@FXML private TableColumn<Usuario, Boolean> colunaStatus;

	@FXML private VBox dadosUsuario;
	@FXML private VBox tabelaUsuario;

	@FXML private Label tituloUsuario;

	@FXML private ComboBox<String> cbPerfil;
	@FXML private TextField txtNome;
	@FXML private TextField txtEmail;
	@FXML private TextField txtCpf;
	@FXML private PasswordField txtSenha;

	@FXML private Button btnCadastrar;
	@FXML private Button btnAtualizar;

	private boolean listener = false;

	@Override
	protected String getTitulo() {
		return "Gerenciar Usuários (Administrador)";
	}

	@Override
	protected void aoExibir() {
		tabela.setPlaceholder(new Label("Nenhum usuário cadastrado"));
		if (!listener) {
			configurarColunas();
			configurarCombos();
			pesquisa.textProperty().addListener((obj, antigo, novo) -> fazPesquisa(novo));
			this.listener = true;
		}
		filtro.setValue("Todos");
		listarUsuarios();
	}

	private void configurarColunas() {
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
		colPerfil.setCellValueFactory(new PropertyValueFactory<>("tipo"));
		colunaStatus.setCellValueFactory(new PropertyValueFactory<>("ativo"));

		colunaStatus.setCellFactory(c -> new TableCell<>() {
			private final Label badge = new Label();

			@Override
			protected void updateItem(Boolean ativo, boolean vazio) {
				super.updateItem(ativo, vazio);
				if (vazio || ativo == null) {
					setGraphic(null);
					setText(null);
					return;
				}
				configuraBadge(badge, ativo);
				setGraphic(badge);
				setText(null);
			}
		});
	}

	private void configurarCombos() {
		filtro.setItems(FXCollections.observableArrayList(
				"Todos", "ID", "Nome", "E-mail", "CPF", "Perfil", "Status"));
		cbPerfil.setItems(FXCollections.observableArrayList(
				PERFIL_CLIENTE, PERFIL_FUNCIONARIO, PERFIL_ADMIN));
		cbPerfil.setValue(PERFIL_CLIENTE);
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
		tabelaUsuario.setVisible(false);
		tabelaUsuario.setManaged(false);

		dadosUsuario.setVisible(true);
		dadosUsuario.setManaged(true);
	}

	private void colocaTabela() {
		tabelaUsuario.setVisible(true);
		tabelaUsuario.setManaged(true);

		dadosUsuario.setVisible(false);
		dadosUsuario.setManaged(false);
	}

	private void limparFormulario() {
		cbPerfil.setValue(PERFIL_CLIENTE);
		cbPerfil.setDisable(false);
		txtNome.clear();
		txtEmail.clear();
		txtCpf.clear();
		txtCpf.setDisable(false);
		txtSenha.clear();
		txtSenha.setPromptText("Senha");
	}

	@FXML
	private void trocaCadastro() {
		tiraTabela();
		tituloUsuario.setText("Cadastrar Usuário");

		btnCadastrar.setVisible(true);
		btnCadastrar.setManaged(true);

		btnAtualizar.setVisible(false);
		btnAtualizar.setManaged(false);

		limparFormulario();
	}

	@FXML
	private void trocaAtualizar() {
		Usuario alvo = obterUsuarioAlvo();
		if (alvo == null) return;

		tiraTabela();
		tituloUsuario.setText("Atualizar Usuário");

		btnCadastrar.setVisible(false);
		btnCadastrar.setManaged(false);
		btnAtualizar.setVisible(true);
		btnAtualizar.setManaged(true);
		btnAtualizar.setUserData(alvo);

		cbPerfil.setValue(formatarPerfil(alvo.getTipo()));
		cbPerfil.setDisable(true);
		txtNome.setText(alvo.getNome());
		txtEmail.setText(alvo.getEmail());
		txtCpf.setText(alvo.getCpf());
		txtCpf.setDisable(true);
		txtSenha.clear();
		txtSenha.setPromptText("Nova senha (opcional)");
	}

	@FXML
	private void cadastrarUsuario() {
		String perfil = cbPerfil.getValue();
		String nome = txtNome.getText().trim();
		String email = txtEmail.getText().trim();
		String cpf = txtCpf.getText().trim();
		String senha = txtSenha.getText().trim();

		if (perfil == null || nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || senha.isEmpty()) {
			notificarPopUpErro("Preencha todos os campos!");
			return;
		}

		Usuario usuario = criarUsuarioPorPerfil(perfil, nome, email, cpf, senha);

		if (sistema.cadastrarUsuario(usuario)) {
			notificarPopUp("Usuário cadastrado com sucesso!");
			limparFormulario();
			listarUsuarios();
		} else {
			notificarPopUpErro("Usuário já cadastrado (CPF ou e-mail já em uso)!");
		}
	}

	private Usuario criarUsuarioPorPerfil(String perfil, String nome, String email, String cpf, String senha) {
		return switch (perfil) {
			case PERFIL_FUNCIONARIO -> new Funcionario(nome, email, cpf, senha);
			case PERFIL_ADMIN -> new Administrador(nome, email, cpf, senha);
			default -> new Cliente(nome, email, cpf, senha);
		};
	}

	private String formatarPerfil(String tipo) {
		if (tipo == null) return PERFIL_CLIENTE;
		return switch (tipo.toUpperCase()) {
			case "FUNCIONARIO" -> PERFIL_FUNCIONARIO;
			case "ADMINISTRADOR" -> PERFIL_ADMIN;
			default -> PERFIL_CLIENTE;
		};
	}

	private void fazPesquisa(String pesquisado) {
		colocaTabela();

		if (filtro.getValue() == null) {
			filtro.getSelectionModel().selectFirst();
		}

		String termo = (pesquisado == null) ? "" : pesquisado.toLowerCase().trim();
		List<Usuario> todos = sistema.listarUsuarios();

		if (termo.isEmpty()) {
			tabela.setItems(FXCollections.observableArrayList(todos));
			return;
		}

		String criterio = filtro.getValue();
		List<Usuario> filtrados = todos.stream()
				.filter(u -> correspondeFiltro(u, criterio, termo))
				.toList();

		tabela.setItems(FXCollections.observableArrayList(filtrados));
	}

	private boolean correspondeFiltro(Usuario u, String criterio, String termo) {
		String statusTexto = u.isAtivo() ? "ativo" : "inativo";
		return switch (criterio) {
			case "ID" -> String.valueOf(u.getId()).startsWith(termo);
			case "Nome" -> u.getNome() != null && u.getNome().toLowerCase().contains(termo);
			case "E-mail" -> u.getEmail() != null && u.getEmail().toLowerCase().contains(termo);
			case "CPF" -> u.getCpf() != null && u.getCpf().toLowerCase().contains(termo);
			case "Perfil" -> u.getTipo() != null && u.getTipo().toLowerCase().contains(termo);
			case "Status" -> statusTexto.startsWith(termo);
			default -> String.valueOf(u.getId()).contains(termo)
					|| (u.getNome() != null && u.getNome().toLowerCase().contains(termo))
					|| (u.getEmail() != null && u.getEmail().toLowerCase().contains(termo))
					|| (u.getCpf() != null && u.getCpf().toLowerCase().contains(termo))
					|| (u.getTipo() != null && u.getTipo().toLowerCase().contains(termo))
					|| statusTexto.contains(termo);
		};
	}

	@FXML
	private void listarUsuarios() {
		colocaTabela();
		tabela.setItems(FXCollections.observableArrayList(sistema.listarUsuarios()));
	}

	@FXML
	private void atualizarUsuario() {
		Usuario atual = (Usuario) btnAtualizar.getUserData();
		if (atual == null) return;

		String nome = txtNome.getText().trim();
		String email = txtEmail.getText().trim();
		String senhaDigitada = txtSenha.getText().trim();
		String senha = senhaDigitada.isEmpty() ? atual.getSenha() : senhaDigitada;

		if (nome.isEmpty() || email.isEmpty()) {
			notificarPopUpErro("Preencha nome e e-mail!");
			return;
		}

		int id = atual.getId();

		if (sistema.atualizarUsuario(id, nome, email, senha)) {
			notificarPopUp("Usuário atualizado com sucesso.");
			btnAtualizar.setUserData(null);
			limparFormulario();
			mostraUsuario(id);
		} else {
			notificarPopUpErro("Erro ao atualizar usuário.");
		}
	}

	@FXML
	private void desativarUsuario() {
		Usuario usuario = obterUsuarioAlvo();
		if (usuario == null) return;

		if (!usuario.isAtivo()) {
			notificarPopUpErro("Este usuário já está desativado.");
			return;
		}

		int id = usuario.getId();

		if (!confirmar("Tem certeza que deseja desativar \"" + usuario.getNome() + "\"?")) {
			return;
		}

		if (sistema.desativaUsuario(id)) {
			notificarPopUp("Usuário desativado com sucesso");
			mostraUsuario(id);
		} else {
			notificarPopUpErro("Erro ao desativar usuário (verifique pendências/multas).");
		}
	}

	private Usuario obterUsuarioAlvo() {
		Usuario atual = tabela.getSelectionModel().getSelectedItem();
		tabela.getSelectionModel().clearSelection();

		if (atual == null) {
			Optional<Integer> id = pedirInteiro(ID);
			if (id.isEmpty()) return null;
			atual = sistema.buscarUsuario(id.get());
			if (atual == null) {
				notificarPopUpErro(NAO_ENCONTRADO);
			}
		}
		return atual;
	}

	private void mostraUsuario(int id) {
		Usuario usuario = sistema.buscarUsuario(id);
		if (usuario == null) {
			info("Usuário não cadastrado.");
			return;
		}

		colocaTabela();
		tabela.getItems().setAll(usuario);
	}

	@FXML
	private void aoLimparPesquisa() {
		colocaTabela();
		pesquisa.clear();
		filtro.setValue("Todos");
		listarUsuarios();
	}
}