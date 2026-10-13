package apresentacao.fx;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class SaudacaoHelper {

    /**
     * Exibe um modal de boas-vindas estilo iOS (iPhone) sobre o painel indicado.
     *
     * @param containerAlvo Container principal da tela (ex: AnchorPane raiz)
     * @param titulo        Título de destaque
     * @param subtitulo     Descrição ou instrução breve
     * @param iconeEmoji    Emoji/ícone de destaque (ex: "👋", "🛡️")
     */
    public static void exibir(Pane containerAlvo, String titulo, String subtitulo, String iconeEmoji) {
        if (containerAlvo == null) return;

        Platform.runLater(() -> {
            // Fundo escurecido translúcido moderno
            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(15, 23, 42, 0.60);");

            if (containerAlvo instanceof AnchorPane) {
                AnchorPane.setTopAnchor(overlay, 0.0);
                AnchorPane.setBottomAnchor(overlay, 0.0);
                AnchorPane.setLeftAnchor(overlay, 0.0);
                AnchorPane.setRightAnchor(overlay, 0.0);
            } else {
                overlay.prefWidthProperty().bind(containerAlvo.widthProperty());
                overlay.prefHeightProperty().bind(containerAlvo.heightProperty());
            }

            // Cartão estilo Apple (cantos arredondados, fundo branco e padding)
            VBox card = new VBox(18);
            card.setAlignment(Pos.CENTER);
            card.setMaxWidth(430);
            card.setMaxHeight(360);
            card.setStyle(
                "-fx-background-color: #ffffff; " +
                "-fx-background-radius: 26px; " +
                "-fx-padding: 36 40 36 40;"
            );

            // Sombra suave estilo iOS
            DropShadow sombra = new DropShadow();
            sombra.setColor(Color.rgb(0, 0, 0, 0.18));
            sombra.setRadius(32);
            sombra.setOffsetY(10);
            card.setEffect(sombra);

            // Distintivo circular com gradiente azul iOS
            Label iconeBadge = new Label(iconeEmoji != null ? iconeEmoji : "👋");
            iconeBadge.setAlignment(Pos.CENTER);
            iconeBadge.setStyle(
                "-fx-font-size: 32px; " +
                "-fx-min-width: 66px; " +
                "-fx-min-height: 66px; " +
                "-fx-max-width: 66px; " +
                "-fx-max-height: 66px; " +
                "-fx-background-radius: 33px; " +
                "-fx-background-color: linear-gradient(to bottom right, #007AFF, #5856D6); " +
                "-fx-text-fill: white;"
            );

            // Título estilo tipografia Apple
            Label lblTitulo = new Label(titulo);
            lblTitulo.setStyle(
                "-fx-font-family: -apple-system, 'SF Pro Display', 'Segoe UI', sans-serif; " +
                "-fx-font-size: 22px; " +
                "-fx-font-weight: 800; " +
                "-fx-text-fill: #1d1d1f;"
            );
            lblTitulo.setWrapText(true);
            lblTitulo.setAlignment(Pos.CENTER);

            // Subtítulo
            Label lblSubtitulo = new Label(subtitulo);
            lblSubtitulo.setStyle(
                "-fx-font-family: -apple-system, 'SF Pro Text', 'Segoe UI', sans-serif; " +
                "-fx-font-size: 13.5px; " +
                "-fx-text-fill: #6e6e73; " +
                "-fx-text-alignment: center;"
            );
            lblSubtitulo.setWrapText(true);
            lblSubtitulo.setAlignment(Pos.CENTER);

            // Botão estilo pílula do iPhone
            Button btnProsseguir = new Button("Prosseguir   ➔");
            btnProsseguir.setMaxWidth(Double.MAX_VALUE);
            btnProsseguir.setStyle(
                "-fx-background-color: #007AFF; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: -apple-system, 'SF Pro Text', 'Segoe UI', sans-serif; " +
                "-fx-font-size: 15px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 28 12 28; " +
                "-fx-background-radius: 25px; " +
                "-fx-cursor: hand;"
            );

            // Hover do botão
            btnProsseguir.setOnMouseEntered(e ->
                btnProsseguir.setStyle(
                    "-fx-background-color: #0062cc; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-family: -apple-system, 'SF Pro Text', 'Segoe UI', sans-serif; " +
                    "-fx-font-size: 15px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 12 28 12 28; " +
                    "-fx-background-radius: 25px; " +
                    "-fx-cursor: hand;"
                )
            );
            btnProsseguir.setOnMouseExited(e ->
                btnProsseguir.setStyle(
                    "-fx-background-color: #007AFF; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-family: -apple-system, 'SF Pro Text', 'Segoe UI', sans-serif; " +
                    "-fx-font-size: 15px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 12 28 12 28; " +
                    "-fx-background-radius: 25px; " +
                    "-fx-cursor: hand;"
                )
            );

            // Transição de saída estilo desbloqueio do iPhone (Slide Up + Fade Out)
            btnProsseguir.setOnAction(event -> {
                FadeTransition fade = new FadeTransition(Duration.millis(260), overlay);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);

                TranslateTransition slide = new TranslateTransition(Duration.millis(260), card);
                slide.setFromY(0);
                slide.setToY(-50);

                ParallelTransition animSaida = new ParallelTransition(fade, slide);
                animSaida.setOnFinished(e -> containerAlvo.getChildren().remove(overlay));
                animSaida.play();
            });

            card.getChildren().addAll(iconeBadge, lblTitulo, lblSubtitulo, btnProsseguir);
            overlay.getChildren().add(card);

            // Transição de entrada suave (Pop-in com Fade)
            overlay.setOpacity(0.0);
            card.setTranslateY(25);
            containerAlvo.getChildren().add(overlay);

            FadeTransition entradaFade = new FadeTransition(Duration.millis(220), overlay);
            entradaFade.setFromValue(0.0);
            entradaFade.setToValue(1.0);

            TranslateTransition entradaSlide = new TranslateTransition(Duration.millis(220), card);
            entradaSlide.setFromY(25);
            entradaSlide.setToY(0);

            new ParallelTransition(entradaFade, entradaSlide).play();
        });
    }
}