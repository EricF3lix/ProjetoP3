package apresentacao.fx;

import entidades.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuClienteController extends MenuController {
	
	@FXML private Label bemVindo;
	private Cliente cliente;

	@Override
	protected String getTitulo() {
		return("Menu Cliente");
	}
	
	@Override
    protected void aoExibir() {
        bemVindo.setText("Bem-vindo(a), " + cliente.getNome());
    }
	
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	@FXML
	private void listarItensDisponiveis() {
		
	}
	
	@FXML
	private void listarAlugueisAtivos() {
		
	}
	
	@FXML
	private void historicoAlugueis() {
		
	}
	
	@FXML
	private void multasPendentes() {
		
	}
	
	@FXML
	private void pagarMulta() {
		
	}
	
}
