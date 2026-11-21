package support;

import java.util.ArrayList;
import java.util.List;

import entidades.Usuario;
import repositories.IUsuarioRepositorioDAO;

public class FakeUsuarioRepositorio implements IUsuarioRepositorioDAO {

    private final List<Usuario> usuarios = new ArrayList<>();
    private int proximoId = 1;
    private int chamadasAtualizar = 0;

    @Override
    public void adicionar(Usuario usuario) {
        usuario.setId(proximoId++);
        usuarios.add(usuario);
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }

    @Override
    public Usuario buscarPorId(int id) {
        return usuarios.stream()
                .filter(usuario -> usuario.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Usuario buscarPorCpf(String cpf) {
        return usuarios.stream()
                .filter(usuario -> usuario.getCpf().equals(cpf))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(usuario -> usuario.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean atualizar(String coluna, Object valor, int id) {
        chamadasAtualizar++;
        usuarios.replaceAll(atual -> atual.getId() == id ? atual : atual);
        return true;
    }

    public int getChamadasAtualizar() {
        return chamadasAtualizar;
    }
}