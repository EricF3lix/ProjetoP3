package apresentacao.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public abstract class MenuPrincipalBaseController<T> extends MenuController {

    @FXML
    protected Label lblAvatar;

    @FXML
    protected Label lblBoasVindas;

    @FXML
    protected StackPane containerTema;

    protected void atualizarDadosUsuario(String nome) {
        if (nome != null) {
            if (lblBoasVindas != null) {
                lblBoasVindas.setText("Bem-vindo(a), " + nome);
            }
            if (lblAvatar != null) {
                lblAvatar.setText(gerarIniciais(nome));
            }
        }
    }

    protected String gerarIniciais(String nome) {
        if (nome == null || nome.isBlank()) return "U";
        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 1) {
            String p = partes[0].toUpperCase();
            return p.length() >= 2 ? p.substring(0, 2) : p;
        }
        String primeira = partes[0].substring(0, 1).toUpperCase();
        String ultima = partes[partes.length - 1].substring(0, 1).toUpperCase();
        return primeira + ultima;
    }

    @Override
    protected void aoExibir() {
        garantirToggleTema();
    }

    @FXML
    protected void initialize() {
        garantirToggleTema();
    }

    protected void garantirToggleTema() {
        if (containerTema != null && containerTema.getChildren().isEmpty()) {
            containerTema.getChildren().add(new ThemeToggleSwitch());
        }
    }

    @FXML
    protected void abrirItens() {
        navegador.abrir("MenuItens.fxml");
    }

    @FXML
    protected void abrirCategorias() {
        navegador.abrir("MenuCategorias.fxml");
    }

    @FXML
    protected void abrirFornecedores() {
        navegador.abrir("MenuFornecedores.fxml");
    }

    @FXML
    protected void abrirContratos() {
        navegador.abrir("MenuContratos.fxml");
    }

    @FXML
    protected void abrirMultas() {
        navegador.abrir("MenuMultas.fxml");
    }

    @FXML
    protected void abrirRelatorios() {
        navegador.abrir("MenuRelatorios.fxml");
    }

    @FXML
    @Override
    public void voltar() {
        navegador.abrir("MenuLogin.fxml");
    }
}