package support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import entidades.Multa;
import repositories.IMultaRepositorioDAO;

public class FakeMultaRepositorio implements IMultaRepositorioDAO {

    private final List<Multa> multas = new ArrayList<>();
    private int proximoId = 1;

    private int chamadasAtualizar = 0;
    private String ultimaColuna;
    private Object ultimoValor;
    private int ultimoId;

    @Override
    public void adicionar(Multa multa) {
        definirCampo(multa, "id", proximoId++);
        multas.add(multa);
    }

    @Override
    public List<Multa> listarTodos() {
        return new ArrayList<>(multas);
    }

    @Override
    public Multa buscarPorId(int id) {
        return multas.stream()
                .filter(multa -> multa.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Multa> buscarPorContrato(int idContrato) {
        return multas.stream()
                .filter(multa -> multa.getIdContrato() == idContrato)
                .toList();
    }

    @Override
    public List<Multa> buscarPendentes() {
        return multas.stream()
                .filter(multa -> !multa.isPaga())
                .toList();
    }

    @Override
    public List<Multa> buscarMultas(String tipofiltro, String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return listarTodos();
        }
        String termo = filtro.toLowerCase();
        return multas.stream()
                .filter(multa -> {
                    Object valor = lerCampo(multa, tipofiltro);
                    return valor != null && valor.toString().toLowerCase().startsWith(termo);
                })
                .toList();
    }

    @Override
    public void atualizar(String coluna, Object valor, int id) {
        chamadasAtualizar++;
        ultimaColuna = coluna;
        ultimoValor = valor;
        ultimoId = id;

        Multa multa = buscarPorId(id);
        if (multa != null) {
            definirCampo(multa, coluna, valor);
        }
    }

    public int getChamadasAtualizar() {
        return chamadasAtualizar;
    }

    public String getUltimaColuna() {
        return ultimaColuna;
    }

    public Object getUltimoValor() {
        return ultimoValor;
    }

    public int getUltimoId() {
        return ultimoId;
    }

    private static void definirCampo(Multa multa, String campo, Object valor) {
        Class<?> classe = multa.getClass();
        while (classe != null) {
            try {
                Field atributo = classe.getDeclaredField(campo);
                atributo.setAccessible(true);
                atributo.set(multa, valor);
                return;
            } catch (NoSuchFieldException e) {
                classe = classe.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Campo inválido para Multa: " + campo, e);
            }
        }
        throw new IllegalArgumentException("Campo inválido para Multa: " + campo);
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