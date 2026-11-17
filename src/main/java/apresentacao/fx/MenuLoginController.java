package apresentacao.fx;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;

public class MenuLoginController extends MenuController {

    @FXML
    private VBox cardLogin;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Label lblErro;

    @FXML
    private StackPane areaLogo;

    @FXML
    private Label lblLogoPadrao;

    @FXML
    private ImageView imgLogo;

    @FXML
    private StackPane containerTema;

    @Override
    protected String getTitulo() {
        return "Loja que aluga de um tudo";
    }

    @Override
    protected void aoExibir() {
        sistema.reiniciaTipoUsuarioLogado();
        txtEmail.clear();
        txtSenha.clear();
        esconderErro();
        Platform.runLater(txtEmail::requestFocus);

        garantirToggleTema();
        carregarLogo();
    }

    @FXML
    private void entrar() {
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarErro("dados inválidos");
            tremerCard();
            if (email.isEmpty()) {
                txtEmail.requestFocus();
            } else {
                txtSenha.requestFocus();
            }
            return;
        }

        Usuario usuario = sistema.realizarLogin(email, senha);

        if (usuario == null) {
            mostrarErro("dados inválidos");
            tremerCard();
            txtSenha.clear();
            txtSenha.requestFocus();
            return;
        }

        esconderErro();

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

    private void mostrarErro(String mensagem) {
        if (lblErro != null) {
            lblErro.setText(mensagem);
            lblErro.setVisible(true);
            lblErro.setManaged(true);
        }
    }

    private void esconderErro() {
        if (lblErro != null) {
            lblErro.setText("");
            lblErro.setVisible(false);
            lblErro.setManaged(false);
        }
    }

    private void tremerCard() {
        if (cardLogin == null) return;

        TranslateTransition tt = new TranslateTransition(Duration.millis(45), cardLogin);
        tt.setFromX(0);
        tt.setByX(12);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> cardLogin.setTranslateX(0));
        tt.play();
    }

    @Override
    public void voltar() {
        Platform.exit();
    }

    @FXML
    private void initialize() {
        carregarLogo();
        garantirToggleTema();

        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> esconderErro());
        txtSenha.textProperty().addListener((obs, oldVal, newVal) -> esconderErro());
    }

    private void garantirToggleTema() {
        if (containerTema != null && containerTema.getChildren().isEmpty()) {
            containerTema.getChildren().add(new ThemeToggleSwitch());
        }
    }

    private void carregarLogo() {
        URL url = null;
        String[] nomes = {"logo.jpg", "logo.png", "logo.jpeg", "logo.webp"};

        for (String nome : nomes) {
            url = MenuLoginController.class.getResource("/imagens/" + nome);
            if (url == null) {
                url = MenuLoginController.class.getClassLoader().getResource("imagens/" + nome);
            }
            if (url != null) break;
        }

        if (url == null) {
            File pasta = new File("src/main/resources/imagens");
            if (pasta.exists() && pasta.isDirectory()) {
                File[] arquivos = pasta.listFiles((dir, name) -> {
                    String n = name.toLowerCase();
                    return n.startsWith("logo") || n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg");
                });
                if (arquivos != null && arquivos.length > 0) {
                    try {
                        url = arquivos[0].toURI().toURL();
                    } catch (Exception ignored) {}
                }
            }
        }

        if (url != null) {
            try {
                Image original = new Image(url.toExternalForm(), false);
                Image renderizada = tornarFundoTransparente(original);

                imgLogo.setImage(renderizada != null ? renderizada : original);
                imgLogo.setPreserveRatio(true);
                imgLogo.setSmooth(true);
                imgLogo.setVisible(true);
                lblLogoPadrao.setVisible(false);
            } catch (Exception e) {
                lblLogoPadrao.setVisible(true);
                imgLogo.setVisible(false);
            }
        } else {
            lblLogoPadrao.setVisible(true);
            imgLogo.setVisible(false);
        }
    }

    private Image tornarFundoTransparente(Image img) {
        if (img == null) return null;
        int largura = (int) img.getWidth();
        int altura = (int) img.getHeight();
        if (largura <= 0 || altura <= 0) return img;

        PixelReader reader = img.getPixelReader();
        if (reader == null) return img;

        WritableImage resultado = new WritableImage(largura, altura);
        PixelWriter writer = resultado.getPixelWriter();

        Color fundo = reader.getColor(4, 4);

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                Color c = reader.getColor(x, y);

                double dist = Math.sqrt(
                    Math.pow(c.getRed() - fundo.getRed(), 2) +
                    Math.pow(c.getGreen() - fundo.getGreen(), 2) +
                    Math.pow(c.getBlue() - fundo.getBlue(), 2)
                );

                double lum = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();

                if (dist < 0.20 || lum < 0.19) {
                    writer.setColor(x, y, Color.TRANSPARENT);
                } else if (dist < 0.35) {
                    double alfa = (dist - 0.20) / (0.35 - 0.20);

                    double r = Math.min(1.0, Math.max(0.0, (c.getRed() - fundo.getRed() * (1.0 - alfa)) / alfa));
                    double g = Math.min(1.0, Math.max(0.0, (c.getGreen() - fundo.getGreen() * (1.0 - alfa)) / alfa));
                    double b = Math.min(1.0, Math.max(0.0, (c.getBlue() - fundo.getBlue() * (1.0 - alfa)) / alfa));

                    writer.setColor(x, y, new Color(r, g, b, Math.pow(alfa, 1.2)));
                } else {
                    writer.setColor(x, y, c);
                }
            }
        }
        return resultado;
    }
}