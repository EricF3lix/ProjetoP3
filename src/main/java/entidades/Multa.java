package entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int idContrato;
    private String tipo;
    @Column(length = 500)
    private String descricao;
    private double valor;
    private boolean paga;
    private String dataCriacao;

    protected Multa() {
    }

    public Multa(int idContrato, String tipo, String descricao, double valor, String dataCriacao) {
        this.idContrato = idContrato;
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.paga = false;
        this.dataCriacao = dataCriacao;
    }

    public int getId() {
        return id;
    }

    public int getIdContrato() {
        return idContrato;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValor() {
        return valor;
    }

    public boolean isPaga() {
        return paga;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setPaga(boolean paga) {
        this.paga = paga;
    }

    public void pagar() {
        this.paga = true;
    }

}