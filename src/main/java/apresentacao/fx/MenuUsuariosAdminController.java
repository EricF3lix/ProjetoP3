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

public class MenuUsuariosAdminController {

    private SistemaFacade sistema;

    @FXML
    private AnchorPane painelConteudo;

    @FXML
    private Button btnCadastrarCliente;

    @FXML
    private Button btnCadastrarFuncionario;

    @FXML
    private Button btnCadastrarAdmin;

    @FXML
    private Button btnBuscarUsuario;

    @FXML
    private Button btnListarUsuarios;

    @FXML
    private Button btnAtualizarUsuario;

    @FXML
    private Button btnDesativarUsuario;

    @FXML
    private Button btnVoltar;

    public void setSistema(SistemaFacade sistema) {
        this.sistema = sistema;
    }

    @FXML
    void aoCadastrarCliente(ActionEvent event) {
        exibirFormularioCadastro("Cliente");
    }

    @FXML
    void aoCadastrarFuncionario(ActionEvent event) {
        exibirFormularioCadastro("Funcionário");
    }

    @FXML
    void aoCadastrarAdmin(ActionEvent event) {
        exibirFormularioCadastro("Administrador");
    }

    private void exibirFormularioCadastro(String tipo) {
        painelConteudo.getChildren().clear();

        VBox container = new VBox(8);
        container.setPadding(new Insets(20));

        Label titulo = new Label("Cadastrar " + tipo);
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome completo");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("E-mail");

        TextField txtCpf = new TextField();
        txtCpf.setPromptText("CPF");

        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Senha");

        Button btnSalvar = new Button("Salvar " + tipo);
        Label lblMensagem = new Label();

        btnSalvar.setOnAction(e -> {
            if (sistema == null) return;

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
            Usuario novoUsuario;

            switch (tipo) {
                case "Funcionário" -> novoUsuario = new Funcionario(id, nome, email, cpf, senha);
                case "Administrador" -> novoUsuario = new Administrador(id, nome, email, cpf, senha);
                default -> novoUsuario = new Cliente(id, nome, email, cpf, senha);
            }

            if (sistema.cadastrarUsuario(novoUsuario)) {
                lblMensagem.setText(tipo + " cadastrado com sucesso! ID: " + id);
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
                lblResultado.setText("Digite um ID numérico válido.");
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
        tabela.setPrefHeight(450.0);

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
    void aoAtualizarUsuario(ActionEvent event) {
        painelConteudo.getChildren().clear();

        VBox container = new VBox(8);
        container.setPadding(new Insets(20));

        Label titulo = new Label("Atualizar Usuário");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        HBox linhaBusca = new HBox(8);
        TextField txtId = new TextField();
        txtId.setPromptText("ID do usuário a atualizar");
        Button btnCarregar = new Button("Carregar");
        linhaBusca.getChildren().addAll(txtId, btnCarregar);

        TextField txtNome = new TextField();
        TextField txtEmail = new TextField();
        PasswordField txtSenha = new PasswordField();
        Button btnSalvar = new Button("Salvar Alterações");
        Label lblStatus = new Label();

        txtNome.setDisable(true);
        txtEmail.setDisable(true);
        txtSenha.setDisable(true);
        btnSalvar.setDisable(true);

        btnCarregar.setOnAction(e -> {
            if (sistema == null) return;
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                Usuario u = sistema.buscarUsuario(id);
                if (u != null) {
                    txtNome.setText(u.getNome());
                    txtEmail.setText(u.getEmail());
                    txtSenha.setText("");
                    txtSenha.setPromptText("Deixe em branco para manter");

                    txtNome.setDisable(false);
                    txtEmail.setDisable(false);
                    txtSenha.setDisable(false);
                    btnSalvar.setDisable(false);
                    lblStatus.setText("Usuário carregado. Edite os campos e clique em salvar.");
                    lblStatus.setStyle("-fx-text-fill: black;");
                } else {
                    lblStatus.setText("Usuário não encontrado.");
                    lblStatus.setStyle("-fx-text-fill: red;");
                }
            } catch (NumberFormatException ex) {
                lblStatus.setText("ID inválido.");
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        });

        btnSalvar.setOnAction(e -> {
            int id = Integer.parseInt(txtId.getText().trim());
            Usuario atual = sistema.buscarUsuario(id);

            String novoNome = txtNome.getText().trim().isEmpty() ? atual.getNome() : txtNome.getText().trim();
            String novoEmail = txtEmail.getText().trim().isEmpty() ? atual.getEmail() : txtEmail.getText().trim();
            String novaSenha = txtSenha.getText().trim().isEmpty() ? atual.getSenha() : txtSenha.getText().trim();

            if (sistema.atualizarUsuario(id, novoNome, novoEmail, novaSenha)) {
                lblStatus.setText("Usuário atualizado com sucesso!");
                lblStatus.setStyle("-fx-text-fill: green;");
            } else {
                lblStatus.setText("Erro ao atualizar usuário.");
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        });

        container.getChildren().addAll(
            titulo,
            new Label("ID do Usuário:"), linhaBusca,
            new Label("Nome:"), txtNome,
            new Label("E-mail:"), txtEmail,
            new Label("Senha:"), txtSenha,
            btnSalvar,
            lblStatus
        );

        ancorarNoPainel(container);
    }

    @FXML
    void aoDesativarUsuario(ActionEvent event) {
        painelConteudo.getChildren().clear();

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));

        Label titulo = new Label("Desativar Usuário");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        TextField txtId = new TextField();
        txtId.setPromptText("Digite o ID do usuário");

        Button btnDesativar = new Button("Confirmar Desativação");
        Label lblMsg = new Label();

        btnDesativar.setOnAction(e -> {
            if (sistema == null) return;
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                if (sistema.desativaUsuario(id)) {
                    lblMsg.setText("Usuário desativado com sucesso!");
                    lblMsg.setStyle("-fx-text-fill: green;");
                    txtId.clear();
                } else {
                    lblMsg.setText("Não foi possível desativar. Verifique se o usuário existe ou possui multas pendentes.");
                    lblMsg.setStyle("-fx-text-fill: red;");
                }
            } catch (NumberFormatException ex) {
                lblMsg.setText("Digite um ID numérico válido.");
                lblMsg.setStyle("-fx-text-fill: red;");
            }
        });

        container.getChildren().addAll(titulo, new Label("ID:"), txtId, btnDesativar, lblMsg);
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