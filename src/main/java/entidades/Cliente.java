package entidades;

import jakarta.persistence.Entity;

@Entity
public class Cliente extends Usuario {

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