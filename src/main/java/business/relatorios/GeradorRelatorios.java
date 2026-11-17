package business.relatorios;

import business.interfaces.IGerenciamentoContratos;
import business.interfaces.IGerenciamentoItens;
import business.interfaces.IGerenciamentoUsuarios;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GeradorRelatorios implements IRelatorios {

    private final IGerenciamentoItens gerenciamentoItens;
    private final IGerenciamentoContratos gerenciamentoContratos;
    private final IGerenciamentoUsuarios gerenciamentoUsuarios;

    public GeradorRelatorios(IGerenciamentoItens gerenciamentoItens, IGerenciamentoContratos gerenciamentoContratos, IGerenciamentoUsuarios gerenciamentoUsuarios) {
        this.gerenciamentoItens = gerenciamentoItens;
        this.gerenciamentoContratos = gerenciamentoContratos;
        this.gerenciamentoUsuarios = gerenciamentoUsuarios;
    }

    @Override
    public List<Item> gerarRelatorioItensDisponiveis() {
        return gerenciamentoItens.listarItens().stream()
                .filter(Item::estaDisponivel)
                .toList();
    }

    @Override
    public List<ContratoAluguel> gerarHistoricoCliente(int idCliente) {
        Usuario usuario = gerenciamentoUsuarios.buscarUsuario(idCliente);
        if (usuario == null) {
            return new ArrayList<>();
        }

        return gerenciamentoContratos.listarContratos().stream()
                .filter(contrato -> contrato.getCliente() != null && contrato.getCliente().getId() == idCliente)
                .toList();
    }

    @Override
    public List<ContratoAluguel> gerarRelatorioItensAlugados() {
        return gerenciamentoContratos.listarContratos().stream()
                .filter(ContratoAluguel::estaAtivo)
                .toList();
    }

    @Override
    public List<ContratoAluguel> gerarRelatorioFaturamento(String dataInicial, String dataFinal) {
        LocalDate inicio = LocalDate.parse(dataInicial);
        LocalDate fim = LocalDate.parse(dataFinal);
        List<ContratoAluguel> contratosPeriodo = new ArrayList<>();

        for (ContratoAluguel contrato : gerenciamentoContratos.listarContratos()) {
            LocalDate dataContrato = LocalDate.parse(contrato.getDataRetirada());
            if (!dataContrato.isBefore(inicio) && !dataContrato.isAfter(fim)) {
                contratosPeriodo.add(contrato);
            }
        }
        return contratosPeriodo;
    }
}