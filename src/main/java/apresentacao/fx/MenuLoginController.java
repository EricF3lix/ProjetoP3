package apresentacao.fx;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class MenuLoginController extends MenuController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtSenha;

    @Override
    protected String getTitulo() {
        return "Loja que aluga de um tudo";
    }

    @Override
    protected void aoExibir() {
        
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

        } else if (usuario instanceof Funcionario) {
            info("Tela do funcionário ainda não criada"); 

        } else if (usuario instanceof Cliente) {
            info("Tela do cliente ainda não criada"); 
        }
    }

    @Override
    public void voltar() {
        Platform.exit();
    }
}