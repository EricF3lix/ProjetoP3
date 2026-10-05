package apresentacao.fx;

import entidades.Administrador;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


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
        lblBemVindo.setText("Bem-vindo(a), " + administrador.getNome());
    }

    @FXML
    private void abrirCategorias() {navegador.abrir("MenuCategorias.fxml");}

    @FXML
    private void abrirMultas() {navegador.abrir("MenuMultas.fxml");}

    @FXML private void abrirUsuarios()     { info("MenuUsuarios.fxml ainda não criado"); }
    @FXML private void abrirItens()        { info("MenuItens.fxml ainda não criado"); }
    
    @FXML private void abrirFornecedores() { 
    	navegador.abrir("MenuFornecedores.fxml");   
    	}
    
    @FXML private void abrirContratos() { 
        navegador.abrir("MenuContratos.fxml"); 
    }
    @FXML private void abrirRelatorios()   { info("MenuRelatorios.fxml ainda não criado"); }
}
