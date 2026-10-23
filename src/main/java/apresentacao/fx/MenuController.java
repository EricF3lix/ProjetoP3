package apresentacao.fx;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import facade.SistemaFacade;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public abstract class MenuController {

    protected SistemaFacade sistema;
    protected Navegador navegador;

    @FXML
    protected TextArea saida;

    protected boolean listenerConfigurado = false;

    @FXML
    protected TextField pesquisa;

    protected abstract String getTitulo();

    protected void aoExibir() {}

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

    // ==========================================
    // ANIMAÇÕES TÁTEIS E MICROINTERAÇÕES
    // ==========================================

    /**
     * Efeito Shake estilo macOS para alertas visuais de recusa/erro em formulários e contêineres.
     */
    public static void animarErroShake(Node node) {
        if (node == null) return;

        Timeline shake = new Timeline(
            new KeyFrame(Duration.millis(0),   new KeyValue(node.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(40),  new KeyValue(node.translateXProperty(), -8)),
            new KeyFrame(Duration.millis(80),  new KeyValue(node.translateXProperty(), 8)),
            new KeyFrame(Duration.millis(120), new KeyValue(node.translateXProperty(), -6)),
            new KeyFrame(Duration.millis(160), new KeyValue(node.translateXProperty(), 6)),
            new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), -4)),
            new KeyFrame(Duration.millis(240), new KeyValue(node.translateXProperty(), 4)),
            new KeyFrame(Duration.millis(280), new KeyValue(node.translateXProperty(), -1)),
            new KeyFrame(Duration.millis(320), new KeyValue(node.translateXProperty(), 1)),
            new KeyFrame(Duration.millis(360), new KeyValue(node.translateXProperty(), 0))
        );
        shake.play();
    }

    // ==========================================
    // ALERTAS E CONFIRMAÇÕES COM POP-UP ANIMADO
    // ==========================================

    protected void info(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensagem);
        animarDialogo(alerta);
        alerta.showAndWait();
    }

    protected void erro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem);
        animarDialogo(alerta);
        alerta.showAndWait();
    }

    protected boolean confirmar(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensagem);
        animarDialogo(alerta);
        Optional<ButtonType> r = alerta.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }

    private <R> void animarDialogo(Dialog<R> dialogo) {
        if (navegador != null && navegador.getStage() != null) {
            dialogo.initOwner(navegador.getStage());
        }
        DialogPane pane = dialogo.getDialogPane();
        pane.setScaleX(0.75);
        pane.setScaleY(0.75);
        pane.setOpacity(0.0);

        dialogo.setOnShown(e -> {
            ScaleTransition zoom = new ScaleTransition(Duration.millis(220), pane);
            zoom.setFromX(0.75);
            zoom.setFromY(0.75);
            zoom.setToX(1.0);
            zoom.setToY(1.0);
            zoom.setInterpolator(Interpolator.EASE_OUT);

            FadeTransition fade = new FadeTransition(Duration.millis(180), pane);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);

            new ParallelTransition(zoom, fade).play();
        });
    }

    public void notificarPopUp(String mensagem) {
        notificarPopUp(mensagem, true);
    }

    public void notificarPopUp(String mensagem, boolean sucesso) {
        if (navegador == null || navegador.getStage() == null) {
            info(mensagem);
            return;
        }

        Stage stage = navegador.getStage();
        Popup popup = new Popup();

        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10, 20, 10, 18));

        boolean escuro = GerenciadorTema.isModoEscuro();

        String corDestaque = sucesso
                ? (escuro ? "#34D399" : "#059669")
                : (escuro ? "#F87171" : "#DC2626");
        String iconeSimbolo = sucesso ? "✓" : "!";

        if (escuro) {
            card.setStyle(
                "-fx-background-color: #121318; " +
                "-fx-background-radius: 20px; " +
                "-fx-border-color: " + corDestaque + "; " +
                "-fx-border-width: 1.5px; " +
                "-fx-border-radius: 20px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 16, 0, 0, 4);"
            );
        } else {
            card.setStyle(
                "-fx-background-color: #FFFFFF; " +
                "-fx-background-radius: 20px; " +
                "-fx-border-color: " + corDestaque + "; " +
                "-fx-border-width: 1.5px; " +
                "-fx-border-radius: 20px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 14, 0, 0, 4);"
            );
        }

        Label lblIcone = new Label(iconeSimbolo);
        lblIcone.setStyle("-fx-text-fill: " + corDestaque + "; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblTexto = new Label(mensagem);
        String corTexto = escuro ? "#FFFFFF" : "#1D1D1F";
        lblTexto.setStyle("-fx-text-fill: " + corTexto + "; -fx-font-size: 13px; -fx-font-weight: 600; -fx-font-family: -apple-system, 'SF Pro Text', sans-serif;");

        card.getChildren().addAll(lblIcone, lblTexto);
        popup.getContent().add(card);
        popup.setAutoFix(true);

        card.setScaleX(0.75);
        card.setScaleY(0.75);
        card.setOpacity(0.0);

        popup.setOnShown(e -> {
            popup.setX(stage.getX() + (stage.getWidth() - popup.getWidth()) / 2.0);
            popup.setY(stage.getY() + 45);

            ScaleTransition zoomIn = new ScaleTransition(Duration.millis(220), card);
            zoomIn.setFromX(0.75);
            zoomIn.setFromY(0.75);
            zoomIn.setToX(1.0);
            zoomIn.setToY(1.0);
            zoomIn.setInterpolator(Interpolator.EASE_OUT);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(180), card);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            ParallelTransition entrada = new ParallelTransition(zoomIn, fadeIn);
            PauseTransition espera = new PauseTransition(Duration.millis(3000));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(250), card);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> popup.hide());

            entrada.setOnFinished(ev -> espera.play());
            espera.setOnFinished(ev -> fadeOut.play());

            entrada.play();
        });

        popup.show(stage);
    }

    protected Optional<String> pedirTexto(String mensagem) {
        return pedirTexto(mensagem, "");
    }

    protected Optional<String> pedirTexto(String mensagem, String valorPadrao) {
        TextInputDialog dialogo = new TextInputDialog(valorPadrao);
        dialogo.setHeaderText(mensagem);
        animarDialogo(dialogo);
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
        animarDialogo(dialogo);

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

    protected String pegaNovo(String novo, String antigo) {
        return (novo != null && !novo.isBlank()) ? novo : antigo;
    }

    protected <T> void configurarTabelaEPesquisa(
        javafx.scene.control.TableView<T> tabela, 
        java.util.function.Consumer<String> metodoPesquisa) {

        tabela.setPlaceholder(new javafx.scene.control.Label(""));
        if (!listenerConfigurado) {
            pesquisa.textProperty().addListener((obs, antigo, novo) -> metodoPesquisa.accept(novo));
            this.listenerConfigurado = true;
        }
    }
}