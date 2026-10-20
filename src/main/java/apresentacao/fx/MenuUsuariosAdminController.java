package apresentacao.fx;

import entidades.Usuario;
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

        // Pega a linha selecionada na TableView herdada
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
        try {
            int id = Integer.parseInt(txtAtualizarId.getText().trim());
            Usuario atual = sistema.buscarUsuario(id);

            if (atual == null) {
                lblAtualizarStatus.setText("Usuário não encontrado.");
                lblAtualizarStatus.setStyle("-fx-text-fill: red;");
                return;
            }

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
        } catch (NumberFormatException ex) {
            lblAtualizarStatus.setText("ID inválido.");
            lblAtualizarStatus.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void aoDesativarUsuario(ActionEvent event) {
        txtDesativarId.clear();
        lblDesativarStatus.setText("");

        // Pega a linha selecionada na TableView herdada
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
                lblDesativarStatus.setText("Usuário não encontrado.");
                lblDesativarStatus.setStyle("-fx-text-fill: red;");
                return;
            }

            // Se estiver ativo, desativa. Se já estiver inativo, tenta reativar (se existir método no backend) ou avisa
            if (usuario.isAtivo()) {
                if (sistema.desativaUsuario(id)) {
                    lblDesativarStatus.setText("Usuário desativado com sucesso!");
                    lblDesativarStatus.setStyle("-fx-text-fill: green;");
                    txtDesativarId.clear();
                    carregarTabela();
                } else {
                    lblDesativarStatus.setText("Não foi possível desativar (verifique pendências/multas).");
                    lblDesativarStatus.setStyle("-fx-text-fill: red;");
                }
            } else {
                // Tenta ativar se houver método no facade, senão avisa o status
                boolean reativado = false;
                try {
                    // Verifica se existe método ativaUsuario no SistemaFacade por reflexão segura
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
                    lblDesativarStatus.setStyle("-fx-text-fill: green;");
                    txtDesativarId.clear();
                    carregarTabela();
                } else {
                    lblDesativarStatus.setText("Este usuário já se encontra inativo.");
                    lblDesativarStatus.setStyle("-fx-text-fill: orange;");
                }
            }
        } catch (NumberFormatException ex) {
            lblDesativarStatus.setText("Digite um ID numérico válido.");
            lblDesativarStatus.setStyle("-fx-text-fill: red;");
        }
    }
}