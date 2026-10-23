package apresentacao.fx;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class ThemeToggleSwitch extends HBox {

    private final FontIcon iconSol;
    private final FontIcon iconLua;
    private final StackPane trilho;
    private final StackPane botaoDeslizante;
    private boolean animando = false;

    public ThemeToggleSwitch() {
        setAlignment(Pos.CENTER);
        setSpacing(6);
        setCursor(Cursor.HAND);

        // Trava o tamanho do componente para não esticar na tela
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // Ícone Sol (compacto)
        iconSol = new FontIcon("fas-sun");
        iconSol.setIconSize(11);

        // Trilho rebaixado pequeno (36x18px)
        trilho = new StackPane();
        trilho.setPrefSize(36, 18);
        trilho.setMaxSize(36, 18);
        trilho.setMinSize(36, 18);

        // Botão circular deslizante (14x14px)
        botaoDeslizante = new StackPane();
        botaoDeslizante.setPrefSize(14, 14);
        botaoDeslizante.setMaxSize(14, 14);
        botaoDeslizante.setMinSize(14, 14);

        trilho.getChildren().add(botaoDeslizante);

        // Ícone Lua (compacto)
        iconLua = new FontIcon("fas-moon");
        iconLua.setIconSize(11);

        getChildren().addAll(iconSol, trilho, iconLua);

        setOnMouseClicked(e -> alternar());

        atualizarVisualSemAnimacao(GerenciadorTema.isModoEscuro());
    }

    private void alternar() {
        if (animando || getScene() == null) return;
        animando = true;

        boolean irParaEscuro = !GerenciadorTema.isModoEscuro();
        double destinoX = irParaEscuro ? 9.0 : -9.0;

        TranslateTransition tt = new TranslateTransition(Duration.millis(180), botaoDeslizante);
        tt.setToX(destinoX);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.setOnFinished(e -> {
            GerenciadorTema.alternarTema(getScene());
            atualizarCores(irParaEscuro);
            animando = false;
        });
        tt.play();
    }

    public void atualizarVisualSemAnimacao(boolean escuro) {
        botaoDeslizante.setTranslateX(escuro ? 9.0 : -9.0);
        atualizarCores(escuro);
    }

    private void atualizarCores(boolean escuro) {
        if (escuro) {
            setStyle(
                "-fx-background-color: rgba(30, 32, 38, 0.85);" +
                "-fx-background-radius: 16px;" +
                "-fx-border-color: rgba(255, 255, 255, 0.12);" +
                "-fx-border-radius: 16px;" +
                "-fx-padding: 3 8 3 8;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.35), 6, 0, 0, 2);"
            );
            trilho.setStyle(
                "-fx-background-color: #24262b;" +
                "-fx-background-radius: 9px;" +
                "-fx-effect: innershadow(gaussian, rgba(0, 0, 0, 0.6), 3, 0, 1, 1);"
            );
            botaoDeslizante.setStyle(
                "-fx-background-color: #5a5e69;" +
                "-fx-background-radius: 7px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 3, 0, 0, 1);"
            );
            iconSol.setIconColor(Color.web("#6b7280"));
            iconLua.setIconColor(Color.web("#a5b4fc"));
        } else {
            setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.90);" +
                "-fx-background-radius: 16px;" +
                "-fx-border-color: rgba(0, 0, 0, 0.08);" +
                "-fx-border-radius: 16px;" +
                "-fx-padding: 3 8 3 8;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.08), 6, 0, 0, 2);"
            );
            trilho.setStyle(
                "-fx-background-color: #d6d8de;" +
                "-fx-background-radius: 9px;" +
                "-fx-effect: innershadow(gaussian, rgba(0, 0, 0, 0.15), 3, 0, 1, 1);"
            );
            botaoDeslizante.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-background-radius: 7px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.18), 3, 0, 0, 1);"
            );
            iconSol.setIconColor(Color.web("#f59e0b"));
            iconLua.setIconColor(Color.web("#9ca3af"));
        }
    }
}