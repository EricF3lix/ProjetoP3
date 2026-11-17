package entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "faturamento")
public class Faturamento {

    @Id
    private Long idContrato;
    private String cliente;
    private Double valorAluguel;
    private Double multa;

    public Faturamento() {
        // Construtor vazio obrigatório para o JPA
    }

    public Faturamento(Long idContrato, String cliente, Double valorAluguel, Double multa) {
        this.idContrato = idContrato;
        this.cliente = cliente;
        this.valorAluguel = valorAluguel;
        this.multa = multa;
    }

    public Long getIdContrato() { return idContrato; }
    public void setIdContrato(Long idContrato) { this.idContrato = idContrato; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public Double getValorAluguel() { return valorAluguel; }
    public void setValorAluguel(Double valorAluguel) { this.valorAluguel = valorAluguel; }

    public Double getMulta() { return multa; }
    public void setMulta(Double multa) { this.multa = multa; }
}