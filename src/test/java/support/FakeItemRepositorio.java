package support;

import entidades.Item;
import repositories.IItemRepositorioDAO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FakeItemRepositorio implements IItemRepositorioDAO {

    private final Map<Integer, Item> itens = new LinkedHashMap<>();

    @Override
    public boolean adicionar(Item item) {
        if (item == null || itens.containsKey(item.getId())) {
            return false;
        }
        itens.put(item.getId(), item);
        return true;
    }

    @Override
    public List<Item> listarTodos() {
        return new ArrayList<>(itens.values());
    }

    @Override
    public List<Item> buscarItens(String tipofiltro, String filtro) {
        List<Item> resultado = new ArrayList<>();
        if (tipofiltro == null || filtro == null) {
            return resultado;
        }
        String alvo = filtro.toLowerCase();
        for (Item item : itens.values()) {
            boolean combina = switch (tipofiltro) {
                case "id" -> String.valueOf(item.getId()).equals(filtro);
                case "nome" -> contem(item.getNome(), alvo);
                case "descricao" -> contem(item.getDescricao(), alvo);
                case "status" -> contem(item.getStatus(), alvo);
                default -> false;
            };
            if (combina) {
                resultado.add(item);
            }
        }
        return resultado;
    }

    @Override
    public boolean atualizar(String coluna, String valor, int id) {
        Item item = itens.get(id);
        if (item == null || coluna == null) {
            return false;
        }
        try {
            switch (coluna) {
                case "nome" -> item.setNome(valor);
                case "descricao" -> item.setDescricao(valor);
                case "taxaDiaria" -> item.setTaxaDiaria(Double.parseDouble(valor));
                case "estadoConservacao" -> item.setEstadoConservacao(valor);
                case "valorReposicao" -> item.setValorReposicao(Double.parseDouble(valor));
                default -> { return false; }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean desativarItem(int id) {
        Item item = itens.get(id);
        if (item == null) {
            return false;
        }
        item.desativar();
        return true;
    }

    private boolean contem(String texto, String alvo) {
        return texto != null && texto.toLowerCase().contains(alvo);
    }
}