package entidades;

@SuppressWarnings("java:S107")
public class Item {

    private final int id;
    private String nome;
    private String descricao;
    private double taxaDiaria;
    private String estadoConservacao;
    private double valorReposicao;
    private Categoria categoria;
    private Fornecedor fornecedor;
    private String status;
    private boolean ativo;
    private static final String DISPONIVEL = "DISPONIVEL";
    private static final String INDISPONIVEL = "INDISPONIVEL";

    public Item(int id, String nome, String descricao, double taxaDiaria,
                String estadoConservacao, double valorReposicao,
                Categoria categoria, Fornecedor fornecedor) {

        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.taxaDiaria = taxaDiaria;
        this.estadoConservacao = estadoConservacao;
        this.valorReposicao = valorReposicao;
        this.categoria = categoria;
        this.fornecedor = fornecedor;
        this.status = DISPONIVEL;
        this.ativo = true;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.getNome() != null ? this.getNome() : "-";
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getTaxaDiaria() {
        return taxaDiaria;
    }

    public void setTaxaDiaria(double taxaDiaria) {
        this.taxaDiaria = taxaDiaria;
    }

    public String getEstadoConservacao() {
        return estadoConservacao;
    }

    public void setEstadoConservacao(String estadoConservacao) {
        this.estadoConservacao = estadoConservacao;
    }

    public double getValorReposicao() {
        return valorReposicao;
    }

    public void setValorReposicao(double valorReposicao) {
        this.valorReposicao = valorReposicao;
    }

    public Categoria getCategoria() {
        return categoria;
    }
    
    public String getCategoriaNome() {
        return categoria != null ? categoria.getNome() : "-";
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!this.ativo) {
            this.status = INDISPONIVEL;
        } else {
            this.status = status != null ? status.toUpperCase() : INDISPONIVEL;
        }
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
        if (!ativo) {
            this.status = INDISPONIVEL;
        }
    }

    public void desativar() {
        this.ativo = false;
        this.status = INDISPONIVEL;
    }

    public boolean estaDisponivel() {
        return ativo && DISPONIVEL.equalsIgnoreCase(status);
    }

    public boolean estaAlugado() {
        return "ALUGADO".equalsIgnoreCase(status);
    }

    public boolean estaEmManutencao() {
        return "MANUTENCAO".equalsIgnoreCase(status);
    }

    public void alugar() {
        if (ativo) {
            this.status = "ALUGADO";
        }
    }

    public void devolver() {
        if (ativo) {
            this.status = DISPONIVEL;
        }
    }

    public void enviarParaManutencao() {
        if (ativo) {
            this.status = "MANUTENCAO";
        }
    }
    
    public String getTaxaDiariaFormatada() {
        return ("R$ " + String.format("%.2f", taxaDiaria).replace('.', ','));
    }
}