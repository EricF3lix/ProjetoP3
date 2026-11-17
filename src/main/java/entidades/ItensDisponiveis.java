package entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "itens_disponiveis")
public class ItensDisponiveis {

    @Id
    private Long id;
    private String nome;
    private String descricao;
    private Double taxaDiaria;
    private String estadoConservacao;
    private Double valorReposicao;
    private String status;
    private Boolean ativo;
    private Long idCategoria;
    private Long idFornecedor;

    public ItensDisponiveis() {
        // Construtor vazio obrigatório para o JPA
    }

    public ItensDisponiveis(Long id, String nome, String descricao, Double taxaDiaria, 
                            String estadoConservacao, Double valorReposicao, String status, 
                            Boolean ativo, Long idCategoria, Long idFornecedor) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.taxaDiaria = taxaDiaria;
        this.estadoConservacao = estadoConservacao;
        this.valorReposicao = valorReposicao;
        this.status = status;
        this.ativo = ativo;
        this.idCategoria = idCategoria;
        this.idFornecedor = idFornecedor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getTaxaDiaria() { return taxaDiaria; }
    public void setTaxaDiaria(Double taxaDiaria) { this.taxaDiaria = taxaDiaria; }

    public String getEstadoConservacao() { return estadoConservacao; }
    public void setEstadoConservacao(String estadoConservacao) { this.estadoConservacao = estadoConservacao; }

    public Double getValorReposicao() { return valorReposicao; }
    public void setValorReposicao(Double valorReposicao) { this.valorReposicao = valorReposicao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public Long getIdFornecedor() { return idFornecedor; }
    public void setIdFornecedor(Long idFornecedor) { this.idFornecedor = idFornecedor; }
}