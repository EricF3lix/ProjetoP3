package support;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import entidades.Usuario;
import repositories.IUsuarioRepositorioDAO;

public class FakeUsuarioRepositorio implements IUsuarioRepositorioDAO {

    private final List<Usuario> usuarios = new ArrayList<>();
    private int proximoId = 1;
    private int chamadasAtualizar = 0;
    private int chamadasDesativar = 0;

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
                .filter(usuario -> Objects.equals(usuario.getCpf(), cpf))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(usuario -> usuario.getEmail() != null && usuario.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean atualizar(String coluna, Object valor, int id) {
        chamadasAtualizar++;
        Usuario usuario = buscarPorId(id);
        if (usuario == null || coluna == null) {
            return false;
        }

        switch (coluna.toLowerCase()) {
            case "nome" -> usuario.setNome(String.valueOf(valor));
            case "email" -> usuario.setEmail(String.valueOf(valor));
            case "senha" -> usuario.setSenha(String.valueOf(valor));
            case "ativo" -> usuario.setAtivo(
                    valor instanceof Boolean ativo ? ativo : Boolean.parseBoolean(String.valueOf(valor))
            );
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean desativarUsuario(int id) {
        chamadasDesativar++;
        Usuario usuario = buscarPorId(id);
        if (usuario == null) {
            return false;
        }
        usuario.setAtivo(false);
        return true;
    }

    public int getChamadasAtualizar() {
        return chamadasAtualizar;
    }

    public int getChamadasDesativar() {
        return chamadasDesativar;
    }
}