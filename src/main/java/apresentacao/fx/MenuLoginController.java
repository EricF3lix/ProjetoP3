package apresentacao.fx;

import java.net.URL;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MenuLoginController extends MenuController {

    @FXML
    private VBox cardLogin;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private StackPane areaLogo;

    @FXML
    private Label lblLogoPadrao;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblMensagemStatus;

    @Override
    protected String getTitulo() {
        return "Loja que aluga de um tudo";
    }

    @FXML
    private void initialize() {
        carregarLogo();
    }

    @Override
    protected void aoExibir() {
        if (sistema != null) {
            sistema.reiniciaTipoUsuarioLogado();
        }
        
        txtEmail.clear();
        txtSenha.clear();
        removerEstiloInvalido(txtEmail);
        removerEstiloInvalido(txtSenha);
        restaurarStatusPadrao();

        // Animação suave de entrada para o card de login
        if (cardLogin != null) {
            cardLogin.setOpacity(0.0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), cardLogin);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }

        Platform.runLater(txtEmail::requestFocus);
    }

    @FXML
    private void entrar() {
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            exibirErroStatus("Preencha o e-mail e a senha.");
            destacarCampoInvalido(txtEmail, email.isEmpty());
            destacarCampoInvalido(txtSenha, senha.isEmpty());
            animarErroShake(cardLogin);
            return;
        }

        Usuario usuario = (sistema != null) ? sistema.realizarLogin(email, senha) : null;

        if (usuario == null) {
            exibirErroStatus("Credenciais inválidas. Verifique os dados.");
            txtSenha.clear();
            destacarCampoInvalido(txtEmail, true);
            destacarCampoInvalido(txtSenha, true);
            animarErroShake(cardLogin);
            return;
        }

        // Sucesso: redireciona conforme o perfil
        if (usuario instanceof Administrador administrador) {
            navegador.abrir("MenuPrincipalAdministrador.fxml",
                    (MenuPrincipalAdministradorController c) -> c.setAdministrador(administrador));

        } else if (usuario instanceof Funcionario funcionario) {
            navegador.abrir("MenuPrincipalFuncionario.fxml",
                    (MenuPrincipalFuncionarioController c) -> c.setFuncionario(funcionario));

        } else if (usuario instanceof Cliente cliente) {
            navegador.abrir("MenuCliente.fxml", (MenuClienteController c) -> c.setCliente(cliente));
        }
    }

    private void exibirErroStatus(String texto) {
        if (lblMensagemStatus != null) {
            lblMensagemStatus.setText(texto);
            lblMensagemStatus.setStyle("-fx-font-size: 13px; -fx-text-fill: #ff3b30; -fx-font-weight: bold;");
        }
    }

    private void restaurarStatusPadrao() {
        if (lblMensagemStatus != null) {
            lblMensagemStatus.setText("Acesse para gerenciar o sistema");
            lblMensagemStatus.setStyle("-fx-font-size: 13px; -fx-text-fill: #636366;");
        }
    }

    private void destacarCampoInvalido(TextField campo, boolean invalido) {
        if (campo == null) return;
        if (invalido) {
            if (!campo.getStyleClass().contains("campo-invalido")) {
                campo.getStyleClass().add("campo-invalido");
            }
            PauseTransition pausa = new PauseTransition(Duration.seconds(3.0));
            pausa.setOnFinished(e -> removerEstiloInvalido(campo));
            pausa.play();
        } else {
            removerEstiloInvalido(campo);
        }
    }

    private void removerEstiloInvalido(TextField campo) {
        if (campo != null) {
            campo.getStyleClass().remove("campo-invalido");
        }
    }

    @Override
    public void voltar() {
        Platform.exit();
    }

    private void carregarLogo() {
        URL url = MenuLoginController.class.getResource("/imagens/logo.jpg");
        if (url == null) {
            return; 
        }

        imgLogo.setImage(new Image(url.toExternalForm()));
        lblLogoPadrao.setVisible(false);
        areaLogo.setStyle("-fx-border-color: transparent;");
    }
}