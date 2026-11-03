package apresentacao.fx;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class ThemeToggleSwitch extends StackPane {
    private final Rectangle fundo = new Rectangle(54, 28);
    private final Circle botao = new Circle(11);
    private final Label lblIcone = new Label();
    private final StackPane containerBotao;
    private Scene scene;

    public ThemeToggleSwitch() {
        this(null);
    }

    public ThemeToggleSwitch(Scene scene) {
        this.scene = scene;

        setMinSize(54, 28);
        setPrefSize(54, 28);
        setMaxSize(54, 28);

        fundo.setArcWidth(28);
        fundo.setArcHeight(28);

        botao.setFill(Color.WHITE);
        botao.setEffect(new javafx.scene.effect.DropShadow(4, Color.rgb(0, 0, 0, 0.25)));

        lblIcone.setMouseTransparent(true);

        containerBotao = new StackPane(botao, lblIcone);
        containerBotao.setMouseTransparent(true);

        getChildren().addAll(fundo, containerBotao);
        setAlignment(Pos.CENTER);
        setCursor(Cursor.HAND);

        boolean escuroInicial = GerenciadorTema.isDarkMode();
        atualizarVisual(escuroInicial, false);

        sceneProperty().addListener((obs, velhaCena, novaCena) -> {
            if (novaCena != null) {
                this.scene = novaCena;
                atualizarVisual(GerenciadorTema.isDarkMode(), false);
            }
        });

        setOnMouseClicked(e -> alternar());
    }

    private void alternar() {
        Scene cenaAtual = (getScene() != null) ? getScene() : this.scene;
        if (cenaAtual != null) {
            GerenciadorTema.alternarTema(cenaAtual);
        }
        boolean escuro = GerenciadorTema.isDarkMode();
        atualizarVisual(escuro, true);
    }

    private void atualizarVisual(boolean dark, boolean animar) {
        fundo.setFill(dark ? Color.valueOf("#34C759") : Color.valueOf("#D1D1D6"));

        try {
            FontIcon fi = new FontIcon(dark ? "fas-moon" : "fas-sun");
            fi.setIconSize(12);
            fi.setIconColor(dark ? Color.valueOf("#1C1C1E") : Color.valueOf("#F59E0B"));
            lblIcone.setGraphic(fi);
            lblIcone.setText("");
        } catch (Throwable t) {
            lblIcone.setGraphic(null);
            lblIcone.setText(dark ? "🌙" : "☀️");
            lblIcone.setStyle("-fx-font-size: 11px;");
        }

        double destinoX = dark ? 13 : -13;

        if (animar) {
            TranslateTransition transicao = new TranslateTransition(Duration.millis(200), containerBotao);
            transicao.setToX(destinoX);
            transicao.play();
        } else {
            containerBotao.setTranslateX(destinoX);
        }
    }
}