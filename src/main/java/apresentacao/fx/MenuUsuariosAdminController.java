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
            lblAtualizarStatus.setStyle("-fx-text-fill: #007AFF; -fx-font-weight: bold;");
        } else {
            txtAtualizarNome.setDisable(true);
            txtAtualizarEmail.setDisable(true);
            txtAtualizarSenha.setDisable(true);
            btnAtualizarSalvar.setDisable(true);
            lblAtualizarStatus.setText("Selecione um usuário na tabela ou informe o ID.");
            lblAtualizarStatus.setStyle("-fx-text-fill: #8e8e93;");
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
                lblAtualizarStatus.setStyle("-fx-text-fill: #1c1c1e;");
                notificarPopUp("Dados do usuário #" + id + " carregados.", true);
            } else {
                animarErroShake(painelAtualizar);
                lblAtualizarStatus.setText("Usuário não encontrado.");
                lblAtualizarStatus.setStyle("-fx-text-fill: #ff3b30; -fx-font-weight: bold;");
                notificarPopUp("Nenhum usuário encontrado com o ID " + id, false);
            }
        } catch (NumberFormatException ex) {
            animarErroShake(painelAtualizar);
            lblAtualizarStatus.setText("ID inválido. Digite apenas números.");
            lblAtualizarStatus.setStyle("-fx-text-fill: #ff3b30; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void aoConfirmarAtualizar(ActionEvent event) {
        if (sistema == null) return;
        try {
            int id = Integer.parseInt(txtAtualizarId.getText().trim());
            Usuario atual = sistema.buscarUsuario(id);

            if (atual == null) {
                animarErroShake(painelAtualizar);
                lblAtualizarStatus.setText("Usuário não encontrado.");
                lblAtualizarStatus.setStyle("-fx-text-fill: #ff3b30; -fx-font-weight: bold;");
                return;
            }

            String novoNome = txtAtualizarNome.getText().trim().isEmpty() ? atual.getNome() : txtAtualizarNome.getText().trim();
            String novoEmail = txtAtualizarEmail.getText().trim().isEmpty() ? atual.getEmail() : txtAtualizarEmail.getText().trim();
            String novaSenha = txtAtualizarSenha.getText().trim().isEmpty() ? atual.getSenha() : txtAtualizarSenha.getText().trim();

            if (sistema.atualizarUsuario(id, novoNome, novoEmail, novaSenha)) {
                lblAtualizarStatus.setText("Usuário atualizado com sucesso!");
                lblAtualizarStatus.setStyle("-fx-text-fill: #34c759; -fx-font-weight: bold;");
                notificarPopUp("Usuário #" + id + " atualizado com sucesso!", true);
                carregarTabela();
            } else {
                animarErroShake(painelAtualizar);
                lblAtualizarStatus.setText("Erro ao atualizar usuário no banco de dados.");
                lblAtualizarStatus.setStyle("-fx-text-fill: #ff3b30; -fx-font-weight: bold;");
                notificarPopUp("Falha ao atualizar dados do usuário.", false);
            }
        } catch (NumberFormatException ex) {
            animarErroShake(painelAtualizar);
            lblAtualizarStatus.setText("ID inválido.");
            lblAtualizarStatus.setStyle("-fx-text-fill: #ff3b30; -fx-font-weight: bold;");
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
            lblDesativarStatus.setStyle("-fx-text-fill: #007AFF; -fx-font-weight: bold;");
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
                animarErroShake(painelDesativar);
                lblDesativarStatus.setText("Usuário não encontrado.");
                lblDesativarStatus.setStyle("-fx-text-fill: #ff3b30; -fx-font-weight: bold;");
                notificarPopUp("Usuário não encontrado.", false);
                return;
            }

            if (usuario.isAtivo()) {
                if (sistema.desativaUsuario(id)) {
                    lblDesativarStatus.setText("Usuário desativado com sucesso!");
                    lblDesativarStatus.setStyle("-fx-text-fill: #34c759; -fx-font-weight: bold;");
                    txtDesativarId.clear();
                    notificarPopUp("Usuário #" + id + " desativado.", true);
                    carregarTabela();
                } else {
                    animarErroShake(painelDesativar);
                    lblDesativarStatus.setText("Não foi possível desativar (verifique pendências/multas).");
                    lblDesativarStatus.setStyle("-fx-text-fill: #ff3b30; -fx-font-weight: bold;");
                    notificarPopUp("Impossível desativar: pendências ativas.", false);
                }
            } else {
                boolean reativado = false;
                try {
                    var metodoAtivar = sistema.getClass().getMethod("ativaUsuario", int.class);
                    reativado = (boolean) metodoAtivar.invoke(sistema, id);
                } catch (NoSuchMethodException ignored) {
                    try {
                        var metodoAtivar2 = sistema.getClass().getMethod("ativarUsuario", int.class);
                        reativado = (boolean) metodoAtivar2.invoke(sistema, id);
                    } catch (Exception ex2) {
                        reativado = false;
                    }
                } catch (Exception e) {
                    reativado = false;
                }

                if (reativado) {
                    lblDesativarStatus.setText("Usuário reativado com sucesso!");
                    lblDesativarStatus.setStyle("-fx-text-fill: #34c759; -fx-font-weight: bold;");
                    txtDesativarId.clear();
                    notificarPopUp("Usuário #" + id + " reativado com sucesso!", true);
                    carregarTabela();
                } else {
                    animarErroShake(painelDesativar);
                    lblDesativarStatus.setText("Este usuário já se encontra inativo.");
                    lblDesativarStatus.setStyle("-fx-text-fill: #ff9500; -fx-font-weight: bold;");
                }
            }
        } catch (NumberFormatException ex) {
            animarErroShake(painelDesativar);
            lblDesativarStatus.setText("Digite um ID numérico válido.");
            lblDesativarStatus.setStyle("-fx-text-fill: #ff3b30; -fx-font-weight: bold;");
        }
    }
}