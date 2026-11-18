package business.cruds;

import java.util.List;

import business.interfaces.IGerenciamentoMultas;
import entidades.Multa;
import repositories.IMultaRepositorioDAO;

public class GerenciamentoMultas implements IGerenciamentoMultas {

    private final IMultaRepositorioDAO repositorio;

    public GerenciamentoMultas(IMultaRepositorioDAO repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean registrarMulta(Multa multa) {

        repositorio.adicionar(multa);

        return true;
    }

    @Override
    public Multa buscarMulta(int id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Multa> listarMultas() {
        return repositorio.listarTodos();
    }

    @Override
    public List<Multa> listarMultasPorContrato(int idContrato) {
        return repositorio.buscarPorContrato(idContrato);
    }

    @Override
    public List<Multa> listarMultasPendentes() {
        return repositorio.buscarPendentes();
    }

    @Override
    public boolean quitarMulta(int id) {

        Multa multa = repositorio.buscarPorId(id);

        if (multa == null || multa.isPaga()) {
            return false;
        }

        repositorio.atualizar("paga", true, id);

        return true;
    }

}