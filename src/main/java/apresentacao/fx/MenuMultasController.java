package apresentacao.fx;

import entidades.Categoria;
import entidades.Multa;
import javafx.fxml.FXML;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public class MenuMultasController extends MenuController {

    @Override
    protected String getTitulo() {return "Gerenciar Multas: ";}

    @FXML
    private void cadastrarMulta() {
        Optional<Integer> idcontrato = pedirInteiro("ID do contrato:");
        if (idcontrato.isEmpty()) return;

        Optional<String> descricao = pedirTexto("Descrição:");
        if (descricao.isEmpty()) return;

        Optional<String> tipo = pedirTexto("Tipo:");
        if (tipo.isEmpty()) return;

        Optional<Double> valor = pedirDouble("valor: ");
        if (valor.isEmpty()) return;

        String data = java.time.LocalDate.now(ZoneId.of("America/Recife")).toString();

        int id = sistema.gerarProximoIdCategoria();
        Multa multa = new Multa(id, idcontrato.get(), tipo.get(), descricao.get(), valor.get(), data);

        if (sistema.registrarMulta(multa)) {
            info("Multa cadastrada com sucesso. ID: " + id);
        } else {
            erro("Erro ao cadastrar multa");
        }
    }

    @FXML
    private void listarMultas(){
        List<Multa> multas = sistema.listarMultas();
        limpar();
        escrever("============================================= MULTAS =============================================");

        if (multas.isEmpty()) {
            escrever("Nenhuma Multa Registrada");
            return;
        }

        escreverf("%-3s %-30s %-30s %-15s %-12s %-10s", "ID", "DATA-CRIAÇÃO","DESCRIÇÃO" , "TIPO", "VALOR", "ATIVA");
        escrever("-".repeat(200));
        for (Multa m : multas) {
            escreverf("%-3d %-30s %-30s %-15s %-12.2f %-10b", m.getId(), m.getDataCriacao(), m.getDescricao(), m.getTipo(), m.getValor(), m.isPaga());
        }
    }
}
