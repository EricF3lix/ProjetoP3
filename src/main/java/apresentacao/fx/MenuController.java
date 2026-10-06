package apresentacao.fx;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import facade.SistemaFacade;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
public abstract class MenuController {

    protected SistemaFacade sistema;
    protected Navegador navegador;

    @FXML
    protected TextArea saida;

    protected abstract String getTitulo();

    protected void aoExibir() {

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
        return pedir(mensagem, "", MenuController::converterDouble, "Digite um valor numérico válido");
    }

    protected Optional<Double> pedirDouble(String mensagem, double valorPadrao) {
        return pedir(mensagem, String.valueOf(valorPadrao), MenuController::converterDouble,
            "Digite um valor numérico válido");
    }

    private static double converterDouble(String texto) {
        return Double.parseDouble(texto.replace(',', '.'));
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
        return pedir(mensagem, "", conversor, msgErro);
    }
    
    protected <T> Optional<T> pedir(String mensagem, String textoInicial, Function<String, T> conversor, String msgErro) {
        String texto = textoInicial;
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
    protected Optional<Integer> escolherPosicao(String mensagem, List<String> rotulos, String rotuloNenhum) {
        List<String> opcoes = new ArrayList<>();
        if (rotuloNenhum != null) {
            opcoes.add(rotuloNenhum);
        }
        opcoes.addAll(rotulos);

        ChoiceDialog<String> dialogo = new ChoiceDialog<>(opcoes.get(0), opcoes);
        dialogo.setHeaderText(mensagem);

        Optional<String> escolhido = dialogo.showAndWait();
        if (escolhido.isEmpty()) {
            return Optional.empty();
        }

        int posicao = opcoes.indexOf(escolhido.get());
        if (rotuloNenhum != null) {
            posicao = posicao - 1;
        }
        return Optional.of(posicao);
    }
}