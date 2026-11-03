package apresentacao.fx;

import entidades.Funcionario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MenuPrincipalFuncionarioController extends MenuController {

    @FXML
    private Label lblAvatar;

    @FXML
    private Label lblBoasVindas;

    @FXML
    private StackPane containerTema;

    private Funcionario funcionario;

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        atualizarDadosUsuario();
    }

    private void atualizarDadosUsuario() {
        if (funcionario != null) {
            String nome = funcionario.getNome();
            if (lblBoasVindas != null) {
                lblBoasVindas.setText("Bem-vindo(a), " + nome);
            }
            if (lblAvatar != null) {
                lblAvatar.setText(gerarIniciais(nome));
            }
        }
    }

    private String gerarIniciais(String nome) {
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
    protected String getTitulo() {
        return "Menu Funcionário";
    }

    @Override
    protected void aoExibir() {
        atualizarDadosUsuario();
        garantirToggleTema();
    }

    @FXML
    private void initialize() {
        garantirToggleTema();
    }

    private void garantirToggleTema() {
        if (containerTema != null && containerTema.getChildren().isEmpty()) {
            containerTema.getChildren().add(new ThemeToggleSwitch());
        }
    }

    @FXML
    private void abrirUsuarios() {
        navegador.abrir("MenuUsuariosFuncionario.fxml");
    }

    @FXML
    private void abrirItens() {
        navegador.abrir("MenuItens.fxml");
    }

    @FXML
    private void abrirCategorias() {
        navegador.abrir("MenuCategorias.fxml");
    }

    @FXML
    private void abrirFornecedores() {
        navegador.abrir("MenuFornecedores.fxml");
    }

    @FXML
    private void abrirContratos() {
        navegador.abrir("MenuContratos.fxml");
    }

    @FXML
    private void abrirMultas() {
        navegador.abrir("MenuMultas.fxml");
    }

    @FXML
    private void abrirRelatorios() {
        navegador.abrir("MenuRelatorios.fxml");
    }

    @FXML
    @Override
    public void voltar() {
        navegador.abrir("MenuLogin.fxml");
    }
}