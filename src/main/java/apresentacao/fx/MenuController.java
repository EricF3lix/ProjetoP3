package apresentacao.fx;

import facade.SistemaFacade;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class MenuController {

    protected SistemaFacade sistema;
    protected Navegador navegador;

    @FXML
    protected TextArea saida;

    protected boolean listenerConfigurado = false;
    
    @FXML
    protected TextField pesquisa;

    @FXML protected FontIcon iconSair;
    @FXML protected FontIcon iconUsuarios;
    @FXML protected FontIcon iconItens;
    @FXML protected FontIcon iconCategorias;
    @FXML protected FontIcon iconFornecedores;
    @FXML protected FontIcon iconContratos;
    @FXML protected FontIcon iconMultas;
    @FXML protected FontIcon iconRelatorios;

    protected void configurarIcones() {
        definirIcone(iconSair, "fas-sign-out-alt");
        definirIcone(iconUsuarios, "fas-users");
        definirIcone(iconItens, "fas-box");
        definirIcone(iconCategorias, "fas-tag");
        definirIcone(iconFornecedores, "fas-truck");
        definirIcone(iconContratos, "fas-file-alt");
        definirIcone(iconMultas, "fas-exclamation-triangle");
        definirIcone(iconRelatorios, "fas-chart-bar");
    }

    private void definirIcone(FontIcon icone, String literal) {
        if (icone != null) {
            icone.setIconLiteral(literal);
        }
    }


    protected abstract String getTitulo();

    protected void aoExibir() {}

    final void inicializar(SistemaFacade sistema, Navegador navegador) {
        this.sistema = sistema;
        this.navegador = navegador;
    }

    public void voltar() {
        if (navegador != null) {
            navegador.voltar();
        }
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
        notificarPopUpSucesso(mensagem);
    }

    public void notificarPopUpSucesso(String mensagem) {
        exibirPopUp(mensagem, true);
    }

    public void notificarPopUpErro(String mensagem) {
        exibirPopUp(mensagem, false);
    }

    public void notificarPopUp(String mensagem, boolean sucesso) {
        exibirPopUp(mensagem, sucesso);
    }

    private void exibirPopUp(String mensagem, boolean sucesso) {
        if (navegador == null || navegador.getStage() == null) {
            info(mensagem);
            return;
        }

        Stage stage = navegador.getStage();
        Popup popup = new Popup();

        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12, 22, 12, 22));

        String iconeSimbolo;
        String estiloCard;

        if (sucesso) {
            iconeSimbolo = "✓";
            estiloCard =
                "-fx-background-color: linear-gradient(to bottom right, #34D058, #248A3D); " +
                "-fx-background-radius: 25px; " +
                "-fx-border-color: #5BE87B; " +
                "-fx-border-width: 1.5px; " +
                "-fx-border-radius: 25px;";
        } else {
            iconeSimbolo = "✕";
            estiloCard =
                "-fx-background-color: linear-gradient(to bottom right, #FF5B52, #C0271E); " +
                "-fx-background-radius: 25px; " +
                "-fx-border-color: #FF8A80; " +
                "-fx-border-width: 1.5px; " +
                "-fx-border-radius: 25px;";
        }

        card.setStyle(estiloCard);

        Label lblIcone = new Label(iconeSimbolo);
        lblIcone.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblTexto = new Label(mensagem);
        lblTexto.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 13px; -fx-font-weight: 600; -fx-font-family: -apple-system, 'SF Pro Text', sans-serif;");

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
        if (rotulos == null) {
            return Optional.empty();
        }

        List<String> opcoes = new ArrayList<>();
        if (rotuloNenhum != null) {
            opcoes.add(rotuloNenhum);
        }
        opcoes.addAll(rotulos);

        if (opcoes.isEmpty()) {
            return Optional.empty();
        }

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
        if (novo != null && !novo.isBlank()) {
            return novo;
        }
        return antigo;
    }
    
    protected <T> void configurarTabelaEPesquisa(
        javafx.scene.control.TableView<T> tabela, 
        java.util.function.Consumer<String> metodoPesquisa) {

        if (tabela != null) {
            tabela.setPlaceholder(new javafx.scene.control.Label(""));
        }

        if (pesquisa != null && !listenerConfigurado && metodoPesquisa != null) {
            pesquisa.textProperty().addListener((obs, antigo, novo) -> metodoPesquisa.accept(novo));
            this.listenerConfigurado = true;
        }
    }
}