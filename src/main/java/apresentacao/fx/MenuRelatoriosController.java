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


}
