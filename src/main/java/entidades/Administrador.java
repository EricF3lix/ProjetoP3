package entidades;

import jakarta.persistence.Entity;

@Entity
public class Administrador extends Usuario {

    public Administrador(String nome, String email, String cpf, String senha) {

        super(nome, email, cpf, senha);
    }
    @Override
    public String getTipo() {
        return "ADMINISTRADOR";
    }
}