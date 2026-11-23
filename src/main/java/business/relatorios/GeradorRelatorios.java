package business.relatorios;

import business.interfaces.IGerenciamentoContratos;
import business.interfaces.IGerenciamentoUsuarios;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import repositories.ManipulaArquivo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GeradorRelatorios implements IRelatorios {

    private final IGerenciamentoContratos gerenciamentoContratos;
    private final IGerenciamentoUsuarios gerenciamentoUsuarios;
    private final ManipulaArquivo manipulaArquivo;

    public GeradorRelatorios(IGerenciamentoContratos gerenciamentoContratos, IGerenciamentoUsuarios gerenciamentoUsuarios) {
        this.gerenciamentoContratos = gerenciamentoContratos;
        this.gerenciamentoUsuarios = gerenciamentoUsuarios;
        this.manipulaArquivo = new ManipulaArquivo();
    }

    @Override
    public List<Item> gerarRelatorioItensDisponiveis() {
        return manipulaArquivo.buscaExata(Item.class, "status", "DISPONIVEL").stream()
                .filter(Item::isAtivo)
                .toList();
    }

    @Override
    public List<ContratoAluguel> gerarHistoricoCliente(int idCliente) {
        Usuario usuario = gerenciamentoUsuarios.buscarUsuario(idCliente);
        if (usuario == null) {
            return new ArrayList<>();
        }
        return manipulaArquivo.buscaExata(ContratoAluguel.class, "cliente.id", idCliente);
    }

    @Override
    public List<ContratoAluguel> gerarRelatorioItensAlugados() {
        return manipulaArquivo.buscaExata(ContratoAluguel.class, "status", "ATIVO");
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