package apresentacao.fx;

import apresentacao.ValidaEntrada;
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
        if (idcontrato.isEmpty()) {
            erro("O ID nao pode ser vazio");
            return;
        }

        Optional<String> descricao = pedirTexto("Descrição:");
        if (descricao.isEmpty()) {
            erro("A Descrição nao pode ser vazia");
            return;
        }

        Optional<String> tipo = pedirTexto("Tipo:");
        if (tipo.isEmpty()) {
            erro("O Tipo nao pode ser vazio");
            return;
        }

        Optional<Double> valor = pedirDouble("valor: ");
        if (valor.isEmpty()) {
            erro("O Valor nao pode ser vazio");
            return;
        }

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
    private void buscarMultas(){
        Optional<Integer> id = pedirInteiro("ID da Multa:");
        if (id.isEmpty()) {
            erro("O ID nao pode ser vazio");
            return;
        }

        Multa multa = sistema.buscarMulta(id.get());
        if (multa == null) {
            erro("Multa não encontrada");
            return;
        }

        limpar();
        escrever("===== DADOS DA CATEGORIA =====");
        escrever("ID:        " + multa.getId());
        escrever("Data de Criação::        " + multa.getDataCriacao());
        escrever("Tipo:        " + multa.getTipo());
        escrever("Descrição:        " + multa.getDescricao());
        escrever("ID do Contrato:        " + multa.getIdContrato());
        escrever("Valor:        " + multa.getValor());
        escrever("Paga:        " + multa.isPaga());

    }

    @FXML
    private void listarMultas(){
        List<Multa> multas = sistema.listarMultas();
        limpar();
        escrever("============================================= MULTAS =============================================");

        if (multas.isEmpty()) {
            erro("Nenhuma Multa Registrada");
            return;
        }

        escreverf("%-3s %-15s %-20s %-15s %-12s %-10s", "ID", "DATA-CRIAÇÃO","DESCRIÇÃO" , "TIPO", "VALOR", "PAGA");
        escrever("-".repeat(200));
        for (Multa m : multas) {
            escreverf("%-3d %-22s %-30s %-15s %-12.2f %-10b", m.getId(), m.getDataCriacao(), m.getDescricao(), m.getTipo(), m.getValor(), m.isPaga());
        }
    }

    @FXML
    private void buscarContrato(){
        Optional<Integer> id = pedirInteiro("ID do contrato");

        if (id.isEmpty()){
            erro("O ID nao pode ser vazio");
            return;
        }
        List<Multa> multas = sistema.listarMultasPorContrato(id.get());

        if (multas.isEmpty()){
            escrever("Nenhuma multa encontrada para esse contrato");
            return;
        }

        escreverf("%-3s %-15s %-20s %-15s %-12s %-10s", "ID", "DATA-CRIAÇÃO","DESCRIÇÃO" , "TIPO", "VALOR", "PAGA");
        for (Multa m : multas){
            escreverf("%-3d %-22s %-30s %-15s %-12.2f %-10b", m.getId(), m.getDataCriacao(), m.getDescricao(), m.getTipo(), m.getValor(), m.isPaga());


        }

    }

    @FXML
    private void multasPedentens() {
        List<Multa> multas = sistema.listarMultasPendentes();

        if (multas.isEmpty()) {
            erro("nenhuma multa pedente");
        }

        escreverf("%-3s %-15s %-20s %-15s %-12s %-10s", "ID", "DATA-CRIAÇÃO", "DESCRIÇÃO", "TIPO", "VALOR", "PAGA");
        for (Multa m : multas) {
            escreverf("%-3d %-15s %-20s %-15s %-12.2f %-10b", m.getId(), m.getDataCriacao(), m.getDescricao(), m.getTipo(), m.getValor(), m.isPaga());

        }
    }

    @FXML
    private void quitarMulta(){
        Optional<Integer> id = pedirInteiro("ID da Multa");

        if (sistema.quitarMulta(id.get())) {
            escrever("Multa quitada com sucesso.");
        } else {
            escrever("Multa não encontrada ou já estava paga.");
        }

    }
}
