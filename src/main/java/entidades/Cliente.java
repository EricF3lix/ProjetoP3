package entidades;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    protected Cliente() {
    }

    public Cliente(String nome, String email, String cpf, String senha) {

        super(nome, email, cpf, senha);
    }

    @Override
    public String getTipo() {
        return "CLIENTE";
    }

    @Override
    public String toString() {
        return this.getNome() != null ? this.getNome() : "-";
    }

}