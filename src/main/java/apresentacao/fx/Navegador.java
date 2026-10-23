package apresentacao.fx;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;

import facade.SistemaFacade;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Navegador {

    private record Tela(Parent raiz, MenuController controller) {}

    private final Stage stage;
    private final Scene scene;
    private final SistemaFacade sistema;
    private final Deque<Tela> pilha = new ArrayDeque<>();
    private boolean animando = false;

    private final StackPane rootMaster;
    private final StackPane camadaTelas;
    private final ThemeToggleSwitch seletorTemaGlobal;

    public Navegador(Stage stage, SistemaFacade sistema) {
        this.stage = stage;
        this.sistema = sistema;

        this.camadaTelas = new StackPane();
        this.seletorTemaGlobal = new ThemeToggleSwitch();

        seletorTemaGlobal.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(seletorTemaGlobal, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(seletorTemaGlobal, new Insets(0, 20, 18, 0));

        this.rootMaster = new StackPane(camadaTelas, seletorTemaGlobal);
        this.scene = new Scene(rootMaster, 1024, 660);

        URL cssUrl = Navegador.class.getResource("style.css");
        if (cssUrl != null) {
            this.scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        GerenciadorTema.aplicarTema(this.scene);

        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
    }

    public void abrir(String fxml) {
        abrir(fxml, c -> {});
    }

    public <T extends MenuController> void abrir(String fxml, Consumer<T> configurador) {
        if (animando) return;

        URL url = Objects.requireNonNull(Navegador.class.getResource(fxml), "FXML não encontrado: " + fxml);
        try {
            FXMLLoader loader = new FXMLLoader(url);
            Parent raiz = loader.load();
            T controller = loader.getController();
            controller.inicializar(sistema, this);
            configurador.accept(controller);

            GerenciadorTema.aplicarTemaEmNo(raiz);

            Tela novaTela = new Tela(raiz, controller);
            Tela telaAnterior = pilha.peek();
            pilha.push(novaTela);

            transicionar(telaAnterior, novaTela, true);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao carregar " + fxml, e);
        }
    }

    public void voltar() {
        if (animando || pilha.size() <= 1) return;

        Tela telaSaindo = pilha.pop();
        Tela telaDestino = pilha.peek();
        transicionar(telaSaindo, telaDestino, false);
    }

    public void reiniciar(String fxml) {
        reiniciar(fxml, c -> {});
    }

    public <T extends MenuController> void reiniciar(String fxml, Consumer<T> configurador) {
        if (animando) return;

        URL url = Objects.requireNonNull(Navegador.class.getResource(fxml), "FXML não encontrado: " + fxml);
        try {
            FXMLLoader loader = new FXMLLoader(url);
            Parent raiz = loader.load();
            T controller = loader.getController();
            controller.inicializar(sistema, this);
            configurador.accept(controller);

            GerenciadorTema.aplicarTemaEmNo(raiz);

            Tela telaAnterior = pilha.peek();
            pilha.clear();

            Tela novaTela = new Tela(raiz, controller);
            pilha.push(novaTela);

            transicionarFade(telaAnterior != null ? telaAnterior.raiz() : null, novaTela);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao carregar " + fxml, e);
        }
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    private void transicionarFade(Parent raizAntiga, Tela destino) {
        animando = true;
        stage.setTitle(destino.controller().getTitulo());
        Parent raizNova = destino.raiz();
        GerenciadorTema.aplicarTemaEmNo(raizNova);

        if (raizAntiga == null) {
            camadaTelas.getChildren().setAll(raizNova);
            raizNova.setOpacity(0.0);
            FadeTransition fade = new FadeTransition(Duration.millis(200), raizNova);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.setOnFinished(e -> {
                animando = false;
                destino.controller().aoExibir();
            });
            fade.play();
            return;
        }

        StackPane container = new StackPane(raizAntiga, raizNova);
        container.setStyle("-fx-background-color: " + (GerenciadorTema.isModoEscuro() ? "#000000;" : "#F5F5F7;"));
        container.setMouseTransparent(true);
        camadaTelas.getChildren().setAll(container);

        raizNova.setOpacity(0.0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(220), raizNova);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(180), raizAntiga);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        ParallelTransition animacao = new ParallelTransition(fadeIn, fadeOut);
        animacao.setOnFinished(e -> {
            container.getChildren().clear();
            camadaTelas.getChildren().setAll(raizNova);
            raizNova.setOpacity(1.0);
            raizAntiga.setOpacity(1.0);
            animando = false;
            destino.controller().aoExibir();
        });
        animacao.play();
    }

    private void transicionar(Tela origem, Tela destino, boolean avancando) {
        if (origem == null) {
            transicionarFade(null, destino);
            return;
        }

        animando = true;
        stage.setTitle(destino.controller().getTitulo());

        Parent raizOrigem = origem.raiz();
        Parent raizDestino = destino.raiz();
        GerenciadorTema.aplicarTemaEmNo(raizDestino);
        double largura = scene.getWidth() > 0 ? scene.getWidth() : 900;

        raizOrigem.setCache(true);
        raizOrigem.setCacheHint(CacheHint.SPEED);
        raizDestino.setCache(true);
        raizDestino.setCacheHint(CacheHint.SPEED);

        StackPane container = new StackPane();
        container.setStyle("-fx-background-color: " + (GerenciadorTema.isModoEscuro() ? "#000000;" : "#F5F5F7;"));
        container.setMouseTransparent(true);
        camadaTelas.getChildren().setAll(container);

        ParallelTransition animacao;

        if (avancando) {
            container.getChildren().addAll(raizOrigem, raizDestino);

            raizDestino.setTranslateX(largura);
            raizDestino.setOpacity(0.8);

            TranslateTransition slideEntrando = new TranslateTransition(Duration.millis(240), raizDestino);
            slideEntrando.setFromX(largura);
            slideEntrando.setToX(0);
            slideEntrando.setInterpolator(Interpolator.EASE_OUT);

            FadeTransition fadeEntrando = new FadeTransition(Duration.millis(240), raizDestino);
            fadeEntrando.setFromValue(0.8);
            fadeEntrando.setToValue(1.0);

            TranslateTransition slideSaindo = new TranslateTransition(Duration.millis(240), raizOrigem);
            slideSaindo.setFromX(0);
            slideSaindo.setToX(-largura * 0.2);
            slideSaindo.setInterpolator(Interpolator.EASE_OUT);

            FadeTransition fadeSaindo = new FadeTransition(Duration.millis(240), raizOrigem);
            fadeSaindo.setFromValue(1.0);
            fadeSaindo.setToValue(0.5);

            animacao = new ParallelTransition(slideEntrando, fadeEntrando, slideSaindo, fadeSaindo);
        } else {
            container.getChildren().addAll(raizDestino, raizOrigem);

            raizDestino.setTranslateX(-largura * 0.2);
            raizDestino.setOpacity(0.5);

            TranslateTransition slideEntrando = new TranslateTransition(Duration.millis(240), raizDestino);
            slideEntrando.setFromX(-largura * 0.2);
            slideEntrando.setToX(0);
            slideEntrando.setInterpolator(Interpolator.EASE_OUT);

            FadeTransition fadeEntrando = new FadeTransition(Duration.millis(240), raizDestino);
            fadeEntrando.setFromValue(0.5);
            fadeEntrando.setToValue(1.0);

            TranslateTransition slideSaindo = new TranslateTransition(Duration.millis(240), raizOrigem);
            slideSaindo.setFromX(0);
            slideSaindo.setToX(largura);
            slideSaindo.setInterpolator(Interpolator.EASE_OUT);

            FadeTransition fadeSaindo = new FadeTransition(Duration.millis(240), raizOrigem);
            fadeSaindo.setFromValue(1.0);
            fadeSaindo.setToValue(0.8);

            animacao = new ParallelTransition(slideEntrando, fadeEntrando, slideSaindo, fadeSaindo);
        }

        animacao.setOnFinished(e -> {
            container.getChildren().clear();
            camadaTelas.getChildren().setAll(raizDestino);

            raizOrigem.setCache(false);
            raizDestino.setCache(false);
            raizDestino.setTranslateX(0);
            raizDestino.setOpacity(1.0);
            raizOrigem.setTranslateX(0);
            raizOrigem.setOpacity(1.0);

            animando = false;
            destino.controller().aoExibir();
        });

        animacao.play();
    }
}