package business.cruds;

import business.interfaces.IGerenciamentoContratos;
import entidades.ContratoAluguel;
import repositories.IContratoRepositorio;

import java.util.List;

public class GerenciamentoContratos implements IGerenciamentoContratos {

    private final IContratoRepositorio repositorio;

    public GerenciamentoContratos(IContratoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean cadastrarContrato(ContratoAluguel contrato) {

        if (repositorio.buscarPorId(contrato.getId()) != null) {
            return false;
        }

        if (repositorio.buscarContratoAtivoPorItem(contrato.getItem().getId()) != null) {
            return false;
        }

        repositorio.adicionar(contrato);

        return true;
    }

    @Override
    public ContratoAluguel buscarContrato(int id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<ContratoAluguel> listarContratos() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean finalizarContrato(int id) {

        ContratoAluguel contrato = repositorio.buscarPorId(id);

        if (contrato == null) {
            return false;
        }

        contrato.finalizarContrato();

        repositorio.salvar();

        return true;
    }

    @Override
    public List<ContratoAluguel> filtrarContratos(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return listarContratos();
        }

        String termo = filtro.toLowerCase().trim();

        return listarContratos().stream()
                .filter(c -> String.valueOf(c.getId()).contains(termo)
                        || (c.getCliente() != null && c.getCliente().getNome() != null 
                            && c.getCliente().getNome().toLowerCase().contains(termo))
                        || (c.getItem() != null && c.getItem().getNome() != null 
                            && c.getItem().getNome().toLowerCase().contains(termo))
                        || (c.getStatus() != null && c.getStatus().toLowerCase().contains(termo)))
                .toList();
    }

    @Override
    public boolean cancelarContrato(int id) {

        ContratoAluguel contrato = repositorio.buscarPorId(id);

        if (contrato == null) {
            return false;
        }

        contrato.cancelarContrato();

        repositorio.salvar();

        return true;
    }

    @Override
    public boolean quitarMultaContrato(int idContrato) {
    
        ContratoAluguel contrato = repositorio.buscarPorId(idContrato);
    
        if (contrato == null) {
            return false;
        }
    
        if (contrato.getValorMulta() <= 0 || contrato.isMultaPaga()) {
            return false;
        }
    
        contrato.setMultaPaga(true);
    
        repositorio.salvar();
    
        return true;
    }

    @Override
    public boolean clientePossuiMultaPendente(int idCliente) {

        for (ContratoAluguel contrato : repositorio.listarTodos()) {

            if (contrato.getCliente().getId() == idCliente
                    && contrato.getValorMulta() > 0
                    && !contrato.isMultaPaga()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean clientePossuiHistorico(int idCliente) {

        for (ContratoAluguel contrato : repositorio.listarTodos()) {

            if (contrato.getCliente().getId() == idCliente) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int gerarProximoId() {
        return repositorio.gerarProximoId();
    }

    @Override
    public double calcularMulta(long diasAtraso, double taxaDiaria) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        return 10 + (0.05 * taxaDiaria * diasAtraso);
    }
    
    @Override
    public List<ContratoAluguel> filtrarPorId(String pesquisado) {
        String termo = pesquisado.toLowerCase();
        return repositorio.listarTodos().stream()
                .filter(contrato -> String.valueOf(contrato.getId()).toLowerCase().startsWith(termo))
                .toList();
    }

    @Override
    public List<ContratoAluguel> filtrarPorItem(String pesquisado) {
        String termo = pesquisado.toLowerCase();
        return repositorio.listarTodos().stream()
                .filter(contrato -> contrato.getItem() != null
                        && contrato.getItem().getNome() != null
                        && contrato.getItem().getNome().toLowerCase().startsWith(termo))
                .toList();
    }

    @Override
    public List<ContratoAluguel> filtrarPorDataRetirada(String pesquisado) {
        String termo = pesquisado.toLowerCase();
        return repositorio.listarTodos().stream()
                .filter(contrato -> contrato.getDataRetirada() != null
                        && String.valueOf(contrato.getDataRetirada()).toLowerCase().startsWith(termo))
                .toList();
    }

    @Override
    public List<ContratoAluguel> filtrarPorDataDevolucao(String pesquisado) {
        String termo = pesquisado.toLowerCase();
        return repositorio.listarTodos().stream()
                .filter(contrato -> contrato.getDataDevolucaoPrevista() != null
                        && String.valueOf(contrato.getDataDevolucaoPrevista()).toLowerCase().startsWith(termo))
                .toList();
    }

    @Override
    public List<ContratoAluguel> filtrarPorStatus(String pesquisado) {
        String termo = pesquisado.toLowerCase();
        return repositorio.listarTodos().stream()
                .filter(contrato -> contrato.getStatus() != null
                        && contrato.getStatus().toLowerCase().startsWith(termo))
                .toList();
    }

    @Override
    public List<ContratoAluguel> filtrarPorValorTotal(String pesquisado) {
        String termo = pesquisado.toLowerCase();
        return repositorio.listarTodos().stream()
                .filter(contrato -> String.valueOf(contrato.getValorTotal()).toLowerCase().startsWith(termo))
                .toList();
    }

    @Override
    public List<ContratoAluguel> filtrarPorValorMulta(String pesquisado) {
        String termo = pesquisado.toLowerCase();
        return repositorio.listarTodos().stream()
                .filter(contrato -> String.valueOf(contrato.getValorMulta()).toLowerCase().startsWith(termo))
                .toList();
    }
}