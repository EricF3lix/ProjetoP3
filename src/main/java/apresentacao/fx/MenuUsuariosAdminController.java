package apresentacao.fx;

import entidades.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MenuUsuariosAdminController extends MenuUsuariosFuncionarioController {

    @FXML
    private Button btnCadastrarFuncionario;

    @FXML
    private Button btnCadastrarAdmin;

    @FXML
    private Button btnAtualizarUsuario;

    @FXML
    private Button btnDesativarUsuario;

    @FXML
    public void aoCadastrarFuncionario(ActionEvent event) {
        exibirFormularioCadastro("Funcionário");
    }

    @FXML
    public void aoCadastrarAdmin(ActionEvent event) {
        exibirFormularioCadastro("Administrador");
    }

    @FXML
    public void aoAtualizarUsuario(ActionEvent event) {
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
    public void aoDesativarUsuario(ActionEvent event) {
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
}