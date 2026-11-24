package repositories;

import java.util.List;

import entidades.Usuario;

public interface IUsuarioRepositorioDAO {

    public abstract void adicionar(Usuario usuario);

    public abstract List<Usuario> listarTodos();

    public abstract Usuario buscarPorCpf(String cpf);

    public abstract Usuario buscarPorId(int id);

    public abstract Usuario buscarPorEmail(String email);

    public abstract boolean atualizar(String coluna, Object valor, int id);

    public abstract boolean desativarUsuario(int id);

}