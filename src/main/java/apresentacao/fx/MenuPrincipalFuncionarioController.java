package apresentacao.fx;

import entidades.Funcionario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.javafx.FontIcon;

public class MenuPrincipalFuncionarioController extends MenuController {

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

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        if (funcionario != null && lblBemVindo != null) {
            lblBemVindo.setText("Bem-vindo(a), " + funcionario.getNome());
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
            lblBemVindo.setText("Bem-vindo(a), " + nome);
        }

        iconSair.setIconLiteral("fas-sign-out-alt");
        iconUsuarios.setIconLiteral("fas-users");
        iconItens.setIconLiteral("fas-box");
        iconCategorias.setIconLiteral("fas-tag");
        iconFornecedores.setIconLiteral("fas-truck");
        iconContratos.setIconLiteral("fas-file-alt");
        iconMultas.setIconLiteral("fas-exclamation-triangle");
        iconRelatorios.setIconLiteral("fas-chart-bar");
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