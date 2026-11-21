package support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import entidades.ContratoAluguel;
import repositories.IContratoRepositorioDAO;

public class FakeContratoRepositorio implements IContratoRepositorioDAO {

    private final List<ContratoAluguel> contratos = new ArrayList<>();
    private int proximoId = 1;
    private int chamadasAtualizar = 0;

    @Override
    public void adicionar(ContratoAluguel contrato) {
        definirId(contrato, proximoId++);
        contratos.add(contrato);
    }

    @Override
    public List<ContratoAluguel> listarTodos() {
        return new ArrayList<>(contratos);
    }

    @Override
    public ContratoAluguel buscarPorId(int id) {
        return contratos.stream()
                .filter(contrato -> contrato.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ContratoAluguel> buscarPorCliente(int idCliente) {
        return contratos.stream()
                .filter(contrato -> contrato.getCliente().getId() == idCliente)
                .toList();
    }

    @Override
    public ContratoAluguel buscarContratoAtivoPorItem(int idItem) {
        return contratos.stream()
                .filter(contrato -> contrato.getItem().getId() == idItem && contrato.estaAtivo())
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ContratoAluguel> buscarContratos(String tipofiltro, String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return listarTodos();
        }
        String termo = filtro.toLowerCase();
        return contratos.stream()
                .filter(contrato -> {
                    Object valor = lerCampo(contrato, tipofiltro);
                    return valor != null && valor.toString().toLowerCase().startsWith(termo);
                })
                .toList();
    }

    @Override
    public void atualizar(String coluna, Object valor, int id) {
        chamadasAtualizar++;
        ContratoAluguel contrato = buscarPorId(id);
        if (contrato != null && coluna != null) {
            atualizarCampo(contrato, coluna, valor);
        }
    }

    public int getChamadasAtualizar() {
        return chamadasAtualizar;
    }

    public static void definirId(Object entidade, int id) {
        Class<?> classe = entidade.getClass();
        while (classe != null) {
            try {
                Field campo = classe.getDeclaredField("id");
                campo.setAccessible(true);
                campo.set(entidade, id);
                return;
            } catch (NoSuchFieldException e) {
                classe = classe.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalArgumentException("A classe " + entidade.getClass().getSimpleName() + " não tem o campo id");
    }

    private static void atualizarCampo(Object entidade, String nomeCampo, Object valor) {
        Class<?> classe = entidade.getClass();
        while (classe != null) {
            try {
                Field campo = classe.getDeclaredField(nomeCampo);
                campo.setAccessible(true);
                campo.set(entidade, valor);
                return;
            } catch (NoSuchFieldException e) {
                classe = classe.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static Object lerCampo(Object alvo, String caminho) {
        Object atual = alvo;
        for (String parte : caminho.split("\\.")) {
            if (atual == null) {
                return null;
            }
            atual = lerAtributoDireto(atual, parte);
        }
        return atual;
    }

    private static Object lerAtributoDireto(Object alvo, String nomeCampo) {
        Class<?> classe = alvo.getClass();
        while (classe != null) {
            try {
                Field campo = classe.getDeclaredField(nomeCampo);
                campo.setAccessible(true);
                return campo.get(alvo);
            } catch (NoSuchFieldException e) {
                classe = classe.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }
}