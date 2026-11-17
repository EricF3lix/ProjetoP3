package apresentacao.fx;

import entidades.Administrador;
import javafx.fxml.FXML;

public class MenuPrincipalAdministradorController extends MenuPrincipalBaseController<Administrador> {

    private Administrador administrador;

    public void setAdministrador(Administrador administrador) {
        this.administrador = administrador;
        atualizarDadosUsuario(administrador != null ? administrador.getNome() : null);
    }

    @Override
    protected String getTitulo() {
        return "Menu Administrador";
    }

    @FXML
    private void abrirUsuarios() {
        navegador.abrir("MenuUsuariosAdmin.fxml");
    }
}