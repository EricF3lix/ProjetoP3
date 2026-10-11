package apresentacao.fx;

import entidades.Funcionario;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

import java.util.function.Function;

public class MenuPrincipalFuncionarioController extends MenuController{

    @FXML
    private Menu lblBemVindo;

    private Funcionario funcionario;

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    @Override
    protected String getTitulo() {return "Menu Funcionario";}

    @Override
    protected void aoExibir() {
        lblBemVindo.setText("Bem-vindo(a), " + funcionario.getNome());
    }

    @FXML
    private void abrirCategorias() {
        navegador.abrir("MenuCategorias.fxml");
    }

    @FXML
    private void abrirMultas() {navegador.abrir("MenuMultas.fxml");}


}
