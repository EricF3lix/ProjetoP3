package apresentacao.fx;

import facade.SistemaFacade;
import facade.SistemaFactory;
import javafx.application.Application;
import javafx.stage.Stage;


public class App extends Application {

    @Override
    public void start(Stage stage) {
        SistemaFacade sistema = SistemaFactory.criar();

        Navegador navegador = new Navegador(stage, sistema);
        navegador.abrir("MenuLogin.fxml");

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}