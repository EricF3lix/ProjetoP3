package apresentacao.fx;

import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import javafx.fxml.FXML;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public class MenuRelatoriosController extends MenuController{

    @Override
    protected String getTitulo() {return "Menu Relatorios";}

    @FXML
    private void itensPorCategoria(){
        List<Item> itens = sistema.gerarRelatorioItensDisponiveis();
        if(!itens.isEmpty()){
            limpar();
            escreverf("%-20s %-5s %-25s %-10s%n","CATEGORIA", "ID", "NOME", "TAXA");

            for(Item i : itens){
                escreverf("%-20s %-5s %-25s %-10.2f", (i.getCategoria()).getNome(),i.getId(),i.getNome(),i.getTaxaDiaria());
            }
        }
        else{
            erro("Erro ao encontrar relatorios");
        }
    }

    @FXML
    private void historicoAlugueisCliente(){
        Optional<Integer> id = pedirInteiro("ID do cliente:");

        if (!id.isEmpty()){
            Usuario usuario = sistema.buscarUsuario(id.get());

            if (usuario != null){
                List<ContratoAluguel> historico = sistema.gerarHistoricoCliente(id.get());

                limpar();
                if (historico.isEmpty()){
                    escrever("Cliente sem historico");
                }
                else {
                    escrever("Cliente: " + usuario.getNome());
                    escreverf("%-5s %-20s %-12s %-12s %-12s %-10s %-10s%n", "ID", "ITEM", "RETIRADA", "PREVISTA", "STATUS", "VALOR", "MULTA");

                    for (ContratoAluguel contrato : historico) {

                        escreverf("%-5d %-20s %-12s %-12s %-12s %-10.2f %-10.2f%n", contrato.getId(), contrato.getItem().getNome(), contrato.getDataRetirada(),
                                contrato.getDataDevolucaoPrevista(), contrato.getStatus(), contrato.getValorTotal(), contrato.getValorMulta());
                    }

                    escrever("\nArquivo CSV gerado na pasta relatorios");
                }

            }
            else{
                erro("Usuario não encontrado");
            }
        }
        else{
            erro("O ID não pode ser vazio!");
        }
    }

}
