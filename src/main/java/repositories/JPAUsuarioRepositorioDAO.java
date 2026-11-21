package repositories;

import java.util.List;

import entidades.Usuario;

public class JPAUsuarioRepositorioDAO implements IUsuarioRepositorioDAO {

    private final ManipulaArquivo manipulaArquivo;

    public JPAUsuarioRepositorioDAO() {
        this(new ManipulaArquivo());
    }

    public JPAUsuarioRepositorioDAO(ManipulaArquivo manipulaArquivo) {
        this.manipulaArquivo = manipulaArquivo;
    }

    @Override
    public void adicionar(Usuario usuario) {
        manipulaArquivo.escrever(usuario);
    }

    @Override
    public List<Usuario> listarTodos() {
        return manipulaArquivo.carregarTudo(Usuario.class);
    }

    @Override
    public Usuario buscarPorId(int id) {
        return manipulaArquivo.buscar(Usuario.class,"id", id).stream().filter(usuario -> usuario.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Usuario buscarPorCpf(String cpf) {
        return manipulaArquivo.buscar(Usuario.class, "cpf", cpf).stream()
                .filter(usuario -> usuario.getCpf().equals(cpf))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return manipulaArquivo.buscar(Usuario.class, "email", email).stream()
                .filter(usuario -> usuario.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean atualizar(String coluna, Object valor, int id) {
        manipulaArquivo.atualizar(Usuario.class, coluna, valor, id);
        return true;
    }

}