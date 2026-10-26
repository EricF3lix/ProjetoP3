package apresentacao.fx;

import entidades.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.lang.reflect.Method;

public class MenuUsuariosAdminController extends MenuUsuariosFuncionarioController {

    private static final String STYLE_TEXT_RED = "-fx-text-fill: red;";
    private static final String STYLE_TEXT_GREEN = "-fx-text-fill: green;";
    private static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";

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
    public void initialize() {
        super.initialize();
        removerBotoesCadastroRedundantes();
    }

    private void removerBotoesCadastroRedundantes() {
        if (btnCadastrarFuncionario != null) {
            btnCadastrarFuncionario.setVisible(false);
            btnCadastrarFuncionario.setManaged(false);
        }
        if (btnCadastrarAdmin != null) {
            btnCadastrarAdmin.setVisible(false);
            btnCadastrarAdmin.setManaged(false);
        }
    }

    @Override
    protected ObservableList<String> obterOpcoesCadastro() {
        return FXCollections.observableArrayList("Cliente", "Funcionário", "Administrador");
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
        lblAtualizarStatus.setText("");

        Usuario selecionado = (tabelaUsuarios != null) ? tabelaUsuarios.getSelectionModel().getSelectedItem() : null;

        if (selecionado != null) {
            txtAtualizarId.setText(String.valueOf(selecionado.getId()));
            txtAtualizarNome.setText(selecionado.getNome());
            txtAtualizarEmail.setText(selecionado.getEmail());
            txtAtualizarSenha.setText("");
            txtAtualizarNome.setDisable(false);
            txtAtualizarEmail.setDisable(false);
            txtAtualizarSenha.setDisable(false);
            btnAtualizarSalvar.setDisable(false);
            lblAtualizarStatus.setText("Editando dados de: " + selecionado.getNome());
            lblAtualizarStatus.setStyle("-fx-text-fill: #007AFF;");
        } else {
            txtAtualizarNome.setDisable(true);
            txtAtualizarEmail.setDisable(true);
            txtAtualizarSenha.setDisable(true);
            btnAtualizarSalvar.setDisable(true);
            lblAtualizarStatus.setText("Selecione um usuário na tabela ou informe o ID.");
            lblAtualizarStatus.setStyle("-fx-text-fill: #6e6e73;");
        }

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
                lblAtualizarStatus.setText(MSG_USUARIO_NAO_ENCONTRADO);
                lblAtualizarStatus.setStyle(STYLE_TEXT_RED);
            }
        } catch (NumberFormatException ex) {
            lblAtualizarStatus.setText("ID inválido.");
            lblAtualizarStatus.setStyle(STYLE_TEXT_RED);
        }
    }

    @FXML
    public void aoConfirmarAtualizar(ActionEvent event) {
        if (sistema == null) return;
        try {
            int id = Integer.parseInt(txtAtualizarId.getText().trim());
            Usuario atual = sistema.buscarUsuario(id);

            if (atual == null) {
                lblAtualizarStatus.setText(MSG_USUARIO_NAO_ENCONTRADO);
                lblAtualizarStatus.setStyle(STYLE_TEXT_RED);
                return;
            }

            String novoNome = txtAtualizarNome.getText().trim().isEmpty() ? atual.getNome() : txtAtualizarNome.getText().trim();
            String novoEmail = txtAtualizarEmail.getText().trim().isEmpty() ? atual.getEmail() : txtAtualizarEmail.getText().trim();
            String novaSenha = txtAtualizarSenha.getText().trim().isEmpty() ? atual.getSenha() : txtAtualizarSenha.getText().trim();

            if (sistema.atualizarUsuario(id, novoNome, novoEmail, novaSenha)) {
                lblAtualizarStatus.setText("Usuário atualizado com sucesso!");
                lblAtualizarStatus.setStyle(STYLE_TEXT_GREEN);
                carregarTabela();
            } else {
                lblAtualizarStatus.setText("Erro ao atualizar usuário.");
                lblAtualizarStatus.setStyle(STYLE_TEXT_RED);
            }
        } catch (NumberFormatException ex) {
            lblAtualizarStatus.setText("ID inválido.");
            lblAtualizarStatus.setStyle(STYLE_TEXT_RED);
        }
    }

    @FXML
    public void aoDesativarUsuario(ActionEvent event) {
        txtDesativarId.clear();
        lblDesativarStatus.setText("");

        Usuario selecionado = (tabelaUsuarios != null) ? tabelaUsuarios.getSelectionModel().getSelectedItem() : null;

        if (selecionado != null) {
            txtDesativarId.setText(String.valueOf(selecionado.getId()));
            lblDesativarStatus.setText("Selecionado: " + selecionado.getNome() + (selecionado.isAtivo() ? " (Ativo)" : " (Inativo)"));
            lblDesativarStatus.setStyle("-fx-text-fill: #007AFF;");
        }

        exibirPainel(painelDesativar);
    }

    @FXML
    public void aoConfirmarDesativar(ActionEvent event) {
        if (sistema == null) return;
        
        try {
            int id = Integer.parseInt(txtDesativarId.getText().trim());
            Usuario usuario = sistema.buscarUsuario(id);

            if (usuario == null) {
                lblDesativarStatus.setText(MSG_USUARIO_NAO_ENCONTRADO);
                lblDesativarStatus.setStyle(STYLE_TEXT_RED);
                return;
            }

            if (usuario.isAtivo()) {
                executarDesativacao(id);
            } else {
                executarReativacao(id);
            }
        } catch (NumberFormatException ex) {
            lblDesativarStatus.setText("Digite um ID numérico válido.");
            lblDesativarStatus.setStyle(STYLE_TEXT_RED);
        }
    }

    private void executarDesativacao(int id) {
        if (sistema.desativaUsuario(id)) {
            lblDesativarStatus.setText("Usuário desativado com sucesso!");
            lblDesativarStatus.setStyle(STYLE_TEXT_GREEN);
            txtDesativarId.clear();
            carregarTabela();
        } else {
            lblDesativarStatus.setText("Não foi possível desativar (verifique pendências/multas).");
            lblDesativarStatus.setStyle(STYLE_TEXT_RED);
        }
    }

    private void executarReativacao(int id) {
        if (tentarReativarUsuario(id)) {
            lblDesativarStatus.setText("Usuário reativado com sucesso!");
            lblDesativarStatus.setStyle(STYLE_TEXT_GREEN);
            txtDesativarId.clear();
            carregarTabela();
        } else {
            lblDesativarStatus.setText("Este usuário já se encontra inativo.");
            lblDesativarStatus.setStyle("-fx-text-fill: orange;");
        }
    }

    private boolean tentarReativarUsuario(int id) {
        try {
            Method metodoAtivar = sistema.getClass().getMethod("ativaUsuario", int.class);
            return (boolean) metodoAtivar.invoke(sistema, id);
        } catch (Exception e) {
            return invocarMetodoReativacaoAlternativo(id);
        }
    }

    private boolean invocarMetodoReativacaoAlternativo(int id) {
        try {
            Method metodoAtivar2 = sistema.getClass().getMethod("ativarUsuario", int.class);
            return (boolean) metodoAtivar2.invoke(sistema, id);
        } catch (Exception ex) {
            return false;
        }
    }
}