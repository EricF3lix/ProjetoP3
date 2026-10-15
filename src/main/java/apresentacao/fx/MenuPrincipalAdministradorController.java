package apresentacao.fx;

import entidades.Administrador;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class MenuPrincipalAdministradorController extends MenuController {

    @FXML
    private Label lblBemVindo;

    private Administrador administrador;

    public void setAdministrador(Administrador administrador) {
        this.administrador = administrador;
    }

    @Override
    protected String getTitulo() {
        return "Menu Administrador";
    }

    @Override
    protected void aoExibir() {
        String nome = (administrador != null && administrador.getNome() != null)
                ? administrador.getNome()
                : "Administrador";

        if (lblBemVindo != null) {
            lblBemVindo.setText("Bem-vindo(a), " + nome);

            // Dispara a saudação uma única vez na sessão
            Platform.runLater(() -> {
                Pane container = encontrarContainerRaiz();
                if (container != null) {
                    SaudacaoHelper.exibir(
                        container,
                        "Bem-vindo, Administrador!",
                        "Painel de controle e administração pronto para utilização.",
                        "🛡️"
                    );
                }
            });
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
        navegador.abrir("MenuUsuariosAdmin.fxml");
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