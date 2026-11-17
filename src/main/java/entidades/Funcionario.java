package entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Funcionario extends Usuario {

    public Funcionario(String nome, String email, String cpf, String senha) {

        super(nome, email, cpf, senha);
    }
    @Override
    public String getTipo() {
        return "FUNCIONARIO";
    }

}