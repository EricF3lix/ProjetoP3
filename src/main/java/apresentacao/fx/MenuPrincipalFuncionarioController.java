package apresentacao.fx;

import org.kordamp.ikonli.javafx.FontIcon;

import entidades.Funcionario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuPrincipalFuncionarioController extends MenuController {

    private static final String PREFIXO_BEM_VINDO = "Bem-vindo(a), ";

    @FXML private FontIcon iconSair;
    @FXML private FontIcon iconUsuarios;
    @FXML private FontIcon iconItens;
    @FXML private FontIcon iconCategorias;
    @FXML private FontIcon iconFornecedores;
    @FXML private FontIcon iconContratos;
    @FXML private FontIcon iconMultas;
    @FXML private FontIcon iconRelatorios;

    @FXML
    private Label lblBemVindo;

    private Funcionario funcionario;
    private boolean saudacaoExibida = false;

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        if (funcionario != null && lblBemVindo != null) {
            lblBemVindo.setText(PREFIXO_BEM_VINDO + funcionario.getNome());
        }
    }

    @Override
    protected String getTitulo() {
        return "Menu Funcionário";
    }

    @Override
    protected void aoExibir() {
        String nome = (funcionario != null && funcionario.getNome() != null)
                ? funcionario.getNome()
                : "Funcionário";

        if (lblBemVindo != null) {
            lblBemVindo.setText(PREFIXO_BEM_VINDO + nome);
        }

        if (iconSair != null) iconSair.setIconLiteral("fas-sign-out-alt");
        if (iconUsuarios != null) iconUsuarios.setIconLiteral("fas-users");
        if (iconItens != null) iconItens.setIconLiteral("fas-box");
        if (iconCategorias != null) iconCategorias.setIconLiteral("fas-tag");
        if (iconFornecedores != null) iconFornecedores.setIconLiteral("fas-truck");
        if (iconContratos != null) iconContratos.setIconLiteral("fas-file-alt");
        if (iconMultas != null) iconMultas.setIconLiteral("fas-exclamation-triangle");
        if (iconRelatorios != null) iconRelatorios.setIconLiteral("fas-chart-bar");

        if (!saudacaoExibida) {
            saudacaoExibida = true;
            Platform.runLater(() -> {
                notificarPopUp(PREFIXO_BEM_VINDO + nome + "!");
            });
        }
    }

    @FXML
    private void abrirCategorias() {
        navegador.abrir("MenuCategorias.fxml");
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
    private void abrirUsuarios() { 
        navegador.abrir("MenuUsuariosFuncionario.fxml");
    }

    @FXML
    private void abrirItens() {
        navegador.abrir("MenuItens.fxml");
    }

    @FXML
    private void abrirFornecedores() {
        navegador.abrir("MenuFornecedores.fxml");
    }

    @FXML
    private void abrirContratos() {
        navegador.abrir("MenuContratos.fxml");
    }
}