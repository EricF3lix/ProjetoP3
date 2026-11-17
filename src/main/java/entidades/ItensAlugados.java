package entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "itens_alugados")
public class ItensAlugados {

    @Id
    private Long contrato;
    private String cliente;
    private String item;
    private LocalDate dataPrevista;
    private String situacao;

    public ItensAlugados() {
        // Construtor vazio obrigatório para o JPA
    }

    public Long getContrato() {
        return contrato;
    }

    public void setContrato(Long contrato) {
        this.contrato = contrato;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    public void setDataPrevista(LocalDate dataPrevista) {
        this.dataPrevista = dataPrevista;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}