package entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ContratoAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "item_id")
    private Item item;

    private String dataRetirada;
    private String dataDevolucaoPrevista;
    private String dataDevolucaoReal;
    private double valorTotal;
    private String status;
    private boolean ativo;
    private double valorMulta;
    private boolean multaPaga;

    protected ContratoAluguel() {
    }

    public ContratoAluguel(Cliente cliente, Item item, String dataRetirada, String dataDevolucaoPrevista, double valorTotal) {
        this.cliente = cliente;
        this.item = item;
        this.dataRetirada = dataRetirada;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataDevolucaoReal = "";
        this.valorTotal = valorTotal;
        this.status = "ATIVO";
        this.ativo = true;
        this.valorMulta = 0.0;
        this.multaPaga = true;
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Item getItem() {
        return item;
    }

    public String getItemNome() {
        return item.getNome();
    }

    public String getDataRet irada() {
        return dataRetirada; 
    } 
 
    public String getDataDev olucaoPrevista() {
        return dataDevolucao Prevista;
    } 
 
    public void setDataDevol ucaoPrevista(String dataDevolucaoPrevista) {
        this.dataDevolucaoPr evista = dataDevolucaoPrevista;
    } 

    public String getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public void setDataDevolucaoReal(String dataDevolucaoReal) {
        this.dataDevolucaoReal = dataDevolucaoReal;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public String getValorTotalFormatado() {
        return ("R$ " + String.format("%.2f", valorTotal).replace('.', ','));
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public double getValorMulta() {
        return valorMulta;
    }

    public String getValorMultaFormatado() {
        return ("R$ " + String.format("%.2f", valorMulta).replace('.', ','));
    }

    public void setValorMulta(double valorMulta) {
        this.valorMulta = valorMulta;
    }

    public boolean isMultaPaga() {
        return multaPaga;
    }

    public void setMultaPaga(boolean multaPaga) {
        this.multaPaga = multaPaga;
    }

    public boolean estaAtivo() {
        return status.equals("ATIVO");
    }

    public boolean estaFinalizado() {
        return status.equals("FINALIZADO");
    }

    public boolean estaCancelado() {
        return status.equals("CANCELADO");
    }

    public boolean possuiMulta() {
        return (valorMulta > 0);
    }

    public void finalizarContrato() {
        this.status = "FINALIZADO";
        this.ativo = false;
    }

    public void cancelarContrato() {
        this.ativo = false;
        this.status = "CANCELADO";
    }

}