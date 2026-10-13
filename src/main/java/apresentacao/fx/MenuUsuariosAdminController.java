package apresentacao.fx;

import entidades.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MenuUsuariosAdminController extends MenuUsuariosFuncionarioController {

    @FXML
    private VBox painelAtualizar;

    @FXML
    private TextField txtAtualizarId;

    @FXML
    private TextField txtAtualizarNome;

    @FXML
    private TextField txtAtualizarEmail;

    @FXML
    private PasswordField txtAtualizarSenha;

    @FXML
    private Button btnAtualizarSalvar;

    @FXML
    private Label lblAtualizarStatus;

    @FXML
    private VBox painelDesativar;

    @FXML
    private TextField txtDesativarId;

    @FXML
    private Label lblDesativarStatus;

    @FXML
    private Button btnCadastrarFuncionario;

    @FXML
    private Button btnCadastrarAdmin;

    @FXML
    private Button btnAtualizarUsuario;

    @FXML
    private Button btnDesativarUsuario;

    @Override
    public String getTitulo() {
        return "Gerenciar Usuários (Administrador)";
    }

    @Override
    protected void exibirPainel(Node painelDesejado) {
        super.exibirPainel(painelDesejado);
        if (painelAtualizar != null) painelAtualizar.setVisible(painelDesejado == painelAtualizar);
        if (painelDesativar != null) painelDesativar.setVisible(painelDesejado == painelDesativar);
        if (painelDesejado != null) {
            painelDesejado.toFront();
        }
    }

    @Override
    protected void exibirSaudacaoInicial(Pane container) {
        saudacaoExibida = true;
        SaudacaoHelper.exibir(
            container,
            "Bem-vindo, Administrador!",
            "Painel de controlo e administração pronto para utilização.",
            "🛡️"
        );
    }

    @FXML
    public void aoCadastrarFuncionario(ActionEvent event) {
        prepararFormularioCadastro("Funcionário");
    }

    @FXML
    public void aoCadastrarAdmin(ActionEvent event) {
        prepararFormularioCadastro("Administrador");
    }

    @FXML
    public void aoAtualizarUsuario(ActionEvent event) {
        txtAtualizarId.clear();
        txtAtualizarNome.clear();
        txtAtualizarEmail.clear();
        txtAtualizarSenha.clear();
        txtAtualizarNome.setDisable(true);
        txtAtualizarEmail.setDisable(true);
        txtAtualizarSenha.setDisable(true);
        btnAtualizarSalvar.setDisable(true);
        lblAtualizarStatus.setText("");
        exibirPainel(painelAtualizar);
    }

    @FXML
    public void aoCarregarUsuarioParaAtualizar(ActionEvent event) {
        if (sistema == null) return;
        try {
            int id = Integer.parseInt(txtAtualizarId.getText().trim());
            Usuario u = sistema.buscarUsuario(id);
            if (u != null) {
                txtAtualizarNome.setText(u.getNome());
                txtAtualizarEmail.setText(u.getEmail());
                txtAtualizarSenha.setText("");
                txtAtualizarNome.setDisable(false);
                txtAtualizarEmail.setDisable(false);
                txtAtualizarSenha.setDisable(false);
                btnAtualizarSalvar.setDisable(false);
                lblAtualizarStatus.setText("Usuário carregado. Edite e salve.");
                lblAtualizarStatus.setStyle("-fx-text-fill: black;");
            } else {
                lblAtualizarStatus.setText("Usuário não encontrado.");
                lblAtualizarStatus.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException ex) {
            lblAtualizarStatus.setText("ID inválido.");
            lblAtualizarStatus.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void aoConfirmarAtualizar(ActionEvent event) {
        if (sistema == null) return;
        int id = Integer.parseInt(txtAtualizarId.getText().trim());
        Usuario atual = sistema.buscarUsuario(id);

        String novoNome = txtAtualizarNome.getText().trim().isEmpty() ? atual.getNome() : txtAtualizarNome.getText().trim();
        String novoEmail = txtAtualizarEmail.getText().trim().isEmpty() ? atual.getEmail() : txtAtualizarEmail.getText().trim();
        String novaSenha = txtAtualizarSenha.getText().trim().isEmpty() ? atual.getSenha() : txtAtualizarSenha.getText().trim();

        if (sistema.atualizarUsuario(id, novoNome, novoEmail, novaSenha)) {
            lblAtualizarStatus.setText("Usuário atualizado com sucesso!");
            lblAtualizarStatus.setStyle("-fx-text-fill: green;");
            carregarTabela();
        } else {
            lblAtualizarStatus.setText("Erro ao atualizar usuário.");
            lblAtualizarStatus.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void aoDesativarUsuario(ActionEvent event) {
        txtDesativarId.clear();
        lblDesativarStatus.setText("");
        exibirPainel(painelDesativar);
    }

    @FXML
    public void aoConfirmarDesativar(ActionEvent event) {
        if (sistema == null) return;
        try {
            int id = Integer.parseInt(txtDesativarId.getText().trim());
            if (sistema.desativaUsuario(id)) {
                lblDesativarStatus.setText("Usuário desativado com sucesso!");
                lblDesativarStatus.setStyle("-fx-text-fill: green;");
                txtDesativarId.clear();
                carregarTabela();
            } else {
                lblDesativarStatus.setText("Não foi possível desativar (verifique pendências/multas).");
                lblDesativarStatus.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException ex) {
            lblDesativarStatus.setText("Digite um ID numérico válido.");
            lblDesativarStatus.setStyle("-fx-text-fill: red;");
        }
    }
}