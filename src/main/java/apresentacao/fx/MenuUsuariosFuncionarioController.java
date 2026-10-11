package apresentacao.fx;

import java.util.List;

import entidades.Cliente;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuUsuariosFuncionarioController {

    private SistemaFacade sistema;

    @FXML
    private AnchorPane painelConteudo;

    @FXML
    private Button btnCadastrarCliente;

    @FXML
    private Button btnBuscarUsuario;

    @FXML
    private Button btnListarUsuarios;

    @FXML
    private Button btnVoltar;

    public void setSistema(SistemaFacade sistema) {
        this.sistema = sistema;
    }

    @FXML
    void aoCadastrarCliente(ActionEvent event) {
        painelConteudo.getChildren().clear();

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));

        Label titulo = new Label("Cadastrar Novo Cliente");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome completo");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("E-mail");

        TextField txtCpf = new TextField();
        txtCpf.setPromptText("CPF");

        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Senha");

        Button btnSalvar = new Button("Salvar Cliente");
        Label lblMensagem = new Label();

        btnSalvar.setOnAction(e -> {
            if (sistema == null) {
                lblMensagem.setText("Erro: Instância do sistema não carregada.");
                return;
            }

            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String cpf = txtCpf.getText().trim();
            String senha = txtSenha.getText().trim();

            if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || senha.isEmpty()) {
                lblMensagem.setText("Por favor, preencha todos os campos.");
                lblMensagem.setStyle("-fx-text-fill: red;");
                return;
            }

            int id = sistema.gerarProximoIdUsuario();
            Cliente cliente = new Cliente(id, nome, email, cpf, senha);

            if (sistema.cadastrarUsuario(cliente)) {
                lblMensagem.setText("Cliente cadastrado com sucesso! ID: " + id);
                lblMensagem.setStyle("-fx-text-fill: green;");
                txtNome.clear();
                txtEmail.clear();
                txtCpf.clear();
                txtSenha.clear();
            } else {
                lblMensagem.setText("Erro: CPF ou e-mail já cadastrado");
                lblMensagem.setStyle("-fx-text-fill: red;");
            }
        });

        container.getChildren().addAll(
            titulo,
            new Label("Nome:"), txtNome,
            new Label("E-mail:"), txtEmail,
            new Label("CPF:"), txtCpf,
            new Label("Senha:"), txtSenha,
            btnSalvar,
            lblMensagem
        );

        ancorarNoPainel(container);
    }

    @FXML
    void aoBuscarUsuario(ActionEvent event) {
        painelConteudo.getChildren().clear();

        VBox container = new VBox(12);
        container.setPadding(new Insets(20));

        Label titulo = new Label("Buscar Usuário por ID");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        HBox linhaBusca = new HBox(8);
        TextField txtId = new TextField();
        txtId.setPromptText("ID do usuário");
        Button btnConfirmarBusca = new Button("Buscar");
        linhaBusca.getChildren().addAll(txtId, btnConfirmarBusca);

        Label lblResultado = new Label();
        lblResultado.setStyle("-fx-font-size: 13px;");

        btnConfirmarBusca.setOnAction(e -> {
            if (sistema == null) return;

            try {
                int id = Integer.parseInt(txtId.getText().trim());
                Usuario usuario = sistema.buscarUsuario(id);

                if (usuario != null) {
                    lblResultado.setText(
                        "ID: " + usuario.getId() + "\n" +
                        "Nome: " + usuario.getNome() + "\n" +
                        "E-mail: " + usuario.getEmail() + "\n" +
                        "CPF: " + usuario.getCpf() + "\n" +
                        "Perfil: " + usuario.getClass().getSimpleName() + "\n" +
                        "Ativo: " + (usuario.isAtivo() ? "Sim" : "Não")
                    );
                    lblResultado.setStyle("-fx-text-fill: black;");
                } else {
                    lblResultado.setText("Usuário não encontrado");
                    lblResultado.setStyle("-fx-text-fill: red;");
                }
            } catch (NumberFormatException ex) {
                lblResultado.setText("Digite um número inteiro válido para o ID.");
                lblResultado.setStyle("-fx-text-fill: red;");
            }
        });

        container.getChildren().addAll(titulo, linhaBusca, lblResultado);
        ancorarNoPainel(container);
    }

    @FXML
    void aoListarUsuarios(ActionEvent event) {
        painelConteudo.getChildren().clear();

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));

        Label titulo = new Label("Todos os Usuários Cadastrados");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        TableView<Usuario> tabela = new TableView<>();
        tabela.setPrefHeight(400.0);

        TableColumn<Usuario, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Usuario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setPrefWidth(120);

        TableColumn<Usuario, String> colEmail = new TableColumn<>("E-mail");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(140);

        TableColumn<Usuario, String> colPerfil = new TableColumn<>("Perfil");
        colPerfil.setCellValueFactory(dado -> 
            new SimpleStringProperty(dado.getValue().getClass().getSimpleName()));
        colPerfil.setPrefWidth(90);

        TableColumn<Usuario, String> colAtivo = new TableColumn<>("Ativo");
        colAtivo.setCellValueFactory(dado -> 
            new SimpleStringProperty(dado.getValue().isAtivo() ? "Sim" : "Não"));
        colAtivo.setPrefWidth(60);

        tabela.getColumns().addAll(colId, colNome, colEmail, colPerfil, colAtivo);

        if (sistema != null) {
            List<Usuario> usuarios = sistema.listarUsuarios();
            tabela.setItems(FXCollections.observableArrayList(usuarios));
        }

        container.getChildren().addAll(titulo, tabela);
        ancorarNoPainel(container);
    }

    @FXML
    void aoVoltar(ActionEvent event) {
        Stage stage = (Stage) btnVoltar.getScene().getWindow();
        stage.close();
    }

    private void ancorarNoPainel(javafx.scene.Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        painelConteudo.getChildren().add(node);
    }
}