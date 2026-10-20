package apresentacao.fx;

import entidades.Funcionario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class MenuPrincipalFuncionarioController extends MenuController {

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
    }

    private Pane encontrarContainerRaiz() {
        if (lblBemVindo == null || lblBemVindo.getScene() == null) {
            return null;
        }
        if (lblBemVindo.getScene().getRoot() instanceof Pane raiz) {
            return raiz;
        }
        return (lblBemVindo.getParent() instanceof Pane p) ? p : null;
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