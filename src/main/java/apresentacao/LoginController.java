package apresentacao; // Ajuste para o nome do seu pacote

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtNome;

    @FXML
    private Button btnEnviar;

    @FXML
    private Label lblMensagem;

    @FXML
    private void onBtnEnviarClick() {
        String nome = txtNome.getText();
        if (nome.trim().isEmpty()) {
            lblMensagem.setText("Por favor, digite um nome!");
        } else {
            lblMensagem.setText("Funciona!! Olá, " + nome + " 🎉");
        }
    }
}