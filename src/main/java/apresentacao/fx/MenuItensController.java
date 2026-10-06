package apresentacao.fx;
 
import java.util.List;
import java.util.Optional;

import entidades.Item;
import javafx.fxml.FXML;
 

public class MenuItensController extends MenuController {
 
    private static final String ID_ITEM = "ID do item:";
    private static final String NAO_ENCONTRADO = "Item não encontrado";
    private static final List<String> ESTADOS = List.of("NOVO", "BOM", "REGULAR", "RUIM");
 
   
    private record Escolha<T>(T valor) {
    }
 
    @Override
    protected String getTitulo() {
        return "Gerenciar Itens";
    }
 
    @FXML
    private void buscarItem() {
        Optional<Integer> id = pedirInteiro(ID_ITEM);
        if (id.isEmpty()) return;
 
        Item item = sistema.buscarItem(id.get());
        if (item == null) {
            erro(NAO_ENCONTRADO);
            return;
        }
 
        limpar();
        escrever("===== DADOS DO ITEM =====");
        escrever("ID:           " + item.getId());
        escrever("Nome:         " + item.getNome());
        escrever("Descrição:    " + item.getDescricao());
        escreverf("Taxa diária:  R$ %.2f", item.getTaxaDiaria());
        escrever("Estado:       " + item.getEstadoConservacao());
        escreverf("Reposição:    R$ %.2f", item.getValorReposicao());
        escrever("Categoria:    " + (item.getCategoria() != null ? item.getCategoria().getNome() : "Sem categoria"));
        escrever("Fornecedor:   " + (item.getFornecedor() != null ? item.getFornecedor().getRazaoSocial() : "Sem fornecedor"));
        escrever("Status:       " + item.getStatus());
        escrever("Ativo:        " + item.isAtivo());
    }
}