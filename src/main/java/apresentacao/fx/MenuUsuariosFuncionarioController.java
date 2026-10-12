package apresentacao.fx;

import java.util.List;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MenuUsuariosFuncionarioController extends MenuController {

    @FXML
    protected StackPane painelConteudo;

    @FXML
    protected VBox painelTabela;

    @FXML
    protected TableView<Usuario> tabelaUsuarios;

    @FXML
    protected TableColumn<Usuario, Integer> colId;

    @FXML
    protected TableColumn<Usuario, String> colNome;

    @FXML
    protected TableColumn<Usuario, String> colEmail;

    @FXML
    protected TableColumn<Usuario, String> colPerfil;

    @FXML
    protected TableColumn<Usuario, String> colAtivo;

    @FXML
    protected VBox painelCadastro;

    @FXML
    protected Label lblCadTitulo;

    @FXML
    protected TextField txtCadNome;

    @FXML
    protected TextField txtCadEmail;

    @FXML
    protected TextField txtCadCpf;

    @FXML
    protected PasswordField txtCadSenha;

    @FXML
    protected Button btnCadSalvar;

    @FXML
    protected Label lblCadMensagem;

    @FXML
    protected VBox painelBusca;

    @FXML
    protected TextField txtBuscaId;

    @FXML
    protected Label lblBuscaResultado;

    @FXML
    protected Button btnCadastrarCliente;

    @FXML
    protected Button btnBuscarUsuario;

    @FXML
    protected Button btnListarUsuarios;

    @FXML
    protected Button btnVoltar;

    protected String tipoCadastroAtual = "Cliente";

    @Override
    public String getTitulo() {
        return "Gerenciar Usuários";
    }

    @FXML
    public void initialize() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colNome != null) colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        if (colEmail != null) colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        if (colPerfil != null) {
            colPerfil.setCellValueFactory(dado ->
                new SimpleStringProperty(dado.getValue().getClass().getSimpleName()));
        }
        if (colAtivo != null) {
            colAtivo.setCellValueFactory(dado ->
                new SimpleStringProperty(dado.getValue().isAtivo() ? "Sim" : "Não"));
        }
    }

    public void setSistema(SistemaFacade sistema) {
        this.sistema = sistema;
        carregarTabela();
    }

    protected void carregarTabela() {
        if (sistema != null && tabelaUsuarios != null) {
            List<Usuario> usuarios = sistema.listarUsuarios();
            tabelaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
        }
    }

    protected void exibirPainel(Node painelDesejado) {
        painelTabela.setVisible(painelDesejado == painelTabela);
        painelCadastro.setVisible(painelDesejado == painelCadastro);
        painelBusca.setVisible(painelDesejado == painelBusca);
        if (painelDesejado != null) {
            painelDesejado.toFront();
        }
    }

    @FXML
    public void aoListarUsuarios(ActionEvent event) {
        exibirPainel(painelTabela);
        carregarTabela();
    }

    @FXML
    public void aoCadastrarCliente(ActionEvent event) {
        prepararFormularioCadastro("Cliente");
    }

    protected void prepararFormularioCadastro(String tipo) {
        this.tipoCadastroAtual = tipo;
        lblCadTitulo.setText("Cadastrar " + tipo);
        btnCadSalvar.setText("Salvar " + tipo);
        txtCadNome.clear();
        txtCadEmail.clear();
        txtCadCpf.clear();
        txtCadSenha.clear();
        lblCadMensagem.setText("");
        exibirPainel(painelCadastro);
    }

    @FXML
    public void aoSalvarCadastro(ActionEvent event) {
        if (sistema == null) return;

        String nome = txtCadNome.getText().trim();
        String email = txtCadEmail.getText().trim();
        String cpf = txtCadCpf.getText().trim();
        String senha = txtCadSenha.getText().trim();

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || senha.isEmpty()) {
            lblCadMensagem.setText("Por favor, preencha todos os campos.");
            lblCadMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        int id = sistema.gerarProximoIdUsuario();
        Usuario novoUsuario = switch (tipoCadastroAtual) {
            case "Funcionário" -> new Funcionario(id, nome, email, cpf, senha);
            case "Administrador" -> new Administrador(id, nome, email, cpf, senha);
            default -> new Cliente(id, nome, email, cpf, senha);
        };

        if (sistema.cadastrarUsuario(novoUsuario)) {
            lblCadMensagem.setText(tipoCadastroAtual + " cadastrado com sucesso! ID: " + id);
            lblCadMensagem.setStyle("-fx-text-fill: green;");
            txtCadNome.clear();
            txtCadEmail.clear();
            txtCadCpf.clear();
            txtCadSenha.clear();
        } else {
            lblCadMensagem.setText("Erro: CPF ou e-mail já cadastrado");
            lblCadMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void aoBuscarUsuario(ActionEvent event) {
        txtBuscaId.clear();
        lblBuscaResultado.setText("");
        exibirPainel(painelBusca);
    }

    @FXML
    public void aoConfirmarBusca(ActionEvent event) {
        if (sistema == null) return;

        try {
            int id = Integer.parseInt(txtBuscaId.getText().trim());
            Usuario usuario = sistema.buscarUsuario(id);

            if (usuario != null) {
                lblBuscaResultado.setText(
                    "ID: " + usuario.getId() + "\n" +
                    "Nome: " + usuario.getNome() + "\n" +
                    "E-mail: " + usuario.getEmail() + "\n" +
                    "CPF: " + usuario.getCpf() + "\n" +
                    "Perfil: " + usuario.getClass().getSimpleName() + "\n" +
                    "Ativo: " + (usuario.isAtivo() ? "Sim" : "Não")
                );
                lblBuscaResultado.setStyle("-fx-text-fill: black;");
            } else {
                lblBuscaResultado.setText("Usuário não encontrado");
                lblBuscaResultado.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException ex) {
            lblBuscaResultado.setText("Digite um ID numérico válido.");
            lblBuscaResultado.setStyle("-fx-text-fill: red;");
        }
    }
}