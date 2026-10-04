package apresentacao.fx;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.function.Function;

import facade.SistemaFacade;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

public abstract class MenuController {

    protected SistemaFacade sistema;
    protected Navegador navegador;

    @FXML
    protected TextArea saida;

    protected abstract String getTitulo();

    protected void aoExibir() {
        // opcional
    }

    final void inicializar(SistemaFacade sistema, Navegador navegador) {
        this.sistema = sistema;
        this.navegador = navegador;
    }

    public void voltar() {
        navegador.voltar();
    }


    protected void limpar() {
        if (saida != null) {
            saida.clear();
        }
    }

    protected void escrever(String texto) {
        if (saida != null) {
            saida.appendText(texto + "\n");
        }
    }

    protected void escreverf(String formato, Object... args) {
        escrever(String.format(formato, args));
    }


    protected void info(String mensagem) {
        new Alert(Alert.AlertType.INFORMATION, mensagem).showAndWait();
    }

    protected void erro(String mensagem) {
        new Alert(Alert.AlertType.ERROR, mensagem).showAndWait();
    }

    protected boolean confirmar(String mensagem) {
        Optional<ButtonType> r = new Alert(Alert.AlertType.CONFIRMATION, mensagem).showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }


    protected Optional<String> pedirTexto(String mensagem) {
        return pedirTexto(mensagem, "");
    }

    protected Optional<String> pedirTexto(String mensagem, String valorPadrao) {
        TextInputDialog dialogo = new TextInputDialog(valorPadrao);
        dialogo.setHeaderText(mensagem);
        return dialogo.showAndWait().map(String::trim);
    }

    protected Optional<Integer> pedirInteiro(String mensagem) {
        return pedir(mensagem, Integer::parseInt, "Digite apenas números");
    }

    protected Optional<Double> pedirDouble(String mensagem) {
        return pedir(mensagem, Double::parseDouble, "Digite um valor numérico válido");
    }

    protected Optional<String> pedirData(String mensagem) {
        return pedir(mensagem + " (AAAA-MM-DD)", texto -> {
            try {
                LocalDate.parse(texto);
                return texto;
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }, "Formato inválido. Use AAAA-MM-DD");
    }

    protected <T> Optional<T> pedir(String mensagem, Function<String, T> conversor, String msgErro) {
        String texto = "";
        while (true) {
            Optional<String> resposta = pedirTexto(mensagem, texto);
            if (resposta.isEmpty()) {
                return Optional.empty();
            }
            texto = resposta.get();
            try {
                return Optional.of(conversor.apply(texto));
            } catch (IllegalArgumentException e) {
                erro(msgErro);
            }
        }
    }
}