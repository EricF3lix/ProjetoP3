package apresentacao.fx;

import entidades.Funcionario;
import javafx.fxml.FXML;

public class MenuPrincipalFuncionarioController extends MenuPrincipalBaseController<Funcionario> {

    private Funcionario funcionario;

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        atualizarDadosUsuario(funcionario != null ? funcionario.getNome() : null);
    }

    @Override
    protected String getTitulo() {
        return "Menu Funcionário";
    }

    @FXML
    private void abrirUsuarios() {
        navegador.abrir("MenuUsuariosFuncionario.fxml");
    }
}