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
        return manipulaArquivo.buscaUnica(Usuario.class, id);
    }

    @Override
    public Usuario buscarPorCpf(String cpf) {
        return manipulaArquivo.buscaExata(Usuario.class, "cpf", cpf).stream().findFirst().orElse(null);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return manipulaArquivo.buscaExata(Usuario.class, "email", email).stream().findFirst().orElse(null);
    }
    
    @Override
    public boolean atualizar(String coluna, Object valor, int id) {
        manipulaArquivo.atualizar(Usuario.class, coluna, valor, id);
        return true;
    }

}