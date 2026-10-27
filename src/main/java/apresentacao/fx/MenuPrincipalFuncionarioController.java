package apresentacao.fx;

import entidades.Funcionario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuPrincipalFuncionarioController extends MenuController {

    private static final String PREFIXO_BEM_VINDO = "Bem-vindo(a), ";

    @FXML
    private Label lblBemVindo;
    
    @FXML
    private Label lblPerfil;

    private Funcionario funcionario;

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
        lblPerfil.setText(funcionario.getNome().substring(0,2).toUpperCase());
        configurarIcones();
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