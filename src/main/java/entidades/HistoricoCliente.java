package entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "historico_cliente")
public class HistoricoCliente {

    @Id
    private Long idContrato;
    private String cliente;
    private String item;
    private LocalDate dataRetirada;
    private LocalDate dataPrevista;
    private String status;
    private Double valorTotal;
    private Double multa;

    public HistoricoCliente() {
        // Construtor vazio obrigatório para o JPA
    }

    public HistoricoCliente(Long idContrato, String cliente, String item, LocalDate dataRetirada, 
                            LocalDate dataPrevista, String status, Double valorTotal, Double multa) {
        this.idContrato = idContrato;
        this.cliente = cliente;
        this.item = item;
        this.dataRetirada = dataRetirada;
        this.dataPrevista = dataPrevista;
        this.status = status;
        this.valorTotal = valorTotal;
        this.multa = multa;
    }

    // Gets e Sets...
    public Long getIdContrato() { return idContrato; }
    public void setIdContrato(Long idContrato) { this.idContrato = idContrato; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public LocalDate getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(LocalDate dataRetirada) { this.dataRetirada = dataRetirada; }

    public LocalDategetDataPrevista() { return dataPrevista; }
    public void setDataPrevista(LocalDate dataPrevista) { this.dataPrevista = dataPrevista; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public Double getMulta() { return multa; }
    public void setMulta(Double multa) { this.multa = multa; }
}