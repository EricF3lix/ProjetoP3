package apresentacao.fx;

import java.net.URL;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class MenuLoginController extends MenuController {

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
    @Override
    protected String getTitulo() {
        return "Loja que aluga de um tudo";
    }

    @Override
    protected void aoExibir() {
        sistema.reiniciaTipoUsuarioLogado();
        txtEmail.clear();
        txtSenha.clear();
        Platform.runLater(txtEmail::requestFocus);
    }

    @FXML
    private void entrar() {
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            erro("Informe email e senha");
            return;
        }

        Usuario usuario = sistema.realizarLogin(email, senha);

        if (usuario == null) {
            erro("Email ou senha inválidos. Tente novamente");
            txtSenha.clear();
            return;
        }

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

    @Override
    public void voltar() {
        Platform.exit();
    }
    @FXML
    private void initialize() {
        carregarLogo();
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