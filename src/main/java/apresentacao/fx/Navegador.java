package apresentacao.fx;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;

import facade.SistemaFacade;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Navegador {

    private record Tela(Parent raiz, MenuController controller) {
    }

    private final Stage stage;
    private final Scene scene;
    private final SistemaFacade sistema;
    private final Deque<Tela> pilha = new ArrayDeque<>();

    public Navegador(Stage stage, SistemaFacade sistema) {
        this.stage = stage;
        this.sistema = sistema;
        this.scene = new Scene(new StackPane(), 900, 600);
        stage.setScene(scene);
    }

    public void abrir(String fxml) {
        abrir(fxml, c -> { });
    }

    public <T extends MenuController> void abrir(String fxml, Consumer<T> configurador) {
        URL url = Objects.requireNonNull(Navegador.class.getResource(fxml), "FXML não encontrado: " + fxml);
        try {
            FXMLLoader loader = new FXMLLoader(url);
            Parent raiz = loader.load();
            T controller = loader.getController();
            controller.inicializar(sistema, this);
            configurador.accept(controller);

            Tela tela = new Tela(raiz, controller);
            pilha.push(tela);
            mostrar(tela);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao carregar " + fxml, e);
        }
    }

    public void voltar() {
        if (pilha.size() > 1) {
            pilha.pop();
            mostrar(pilha.peek());
        }
    }

    public void reiniciar(String fxml) {
        pilha.clear();
        abrir(fxml);
    }

    public Stage getStage() {
        return stage;
    }

    private void mostrar(Tela tela) {
        scene.setRoot(tela.raiz());
        stage.setTitle(tela.controller().getTitulo());
        tela.controller().aoExibir();
    }
}