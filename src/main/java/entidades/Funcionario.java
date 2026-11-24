package entidades;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FUNCIONARIO")
public class Funcionario extends Usuario {

    protected Funcionario() {
    }

    public Funcionario(String nome, String email, String cpf, String senha) {

        super(nome, email, cpf, senha);
    }

    @Override
    public String getTipo() {
        return "FUNCIONARIO";
    }

}