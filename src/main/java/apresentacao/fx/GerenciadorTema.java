package apresentacao.fx;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;

public class GerenciadorTema {
    private static boolean isDarkMode = false;

    public static void aplicarTema(Scene scene) {
        if (scene == null) return;

        scene.getStylesheets().removeIf(css -> css.contains("tema-claro.css") || css.contains("tema-escuro.css"));

        String nomeArquivo = isDarkMode ? "tema-escuro.css" : "tema-claro.css";
        URL url = buscarRecursoCss(nomeArquivo);

        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
        }

        Parent raiz = scene.getRoot();
        if (raiz != null) {
            aplicarRecursivo(raiz, isDarkMode);
        }
    }

    private static URL buscarRecursoCss(String nomeArquivo) {
        URL url = GerenciadorTema.class.getResource("/estilos/" + nomeArquivo);
        if (url == null) {
            url = GerenciadorTema.class.getResource("/apresentacao/fx/estilos/" + nomeArquivo);
        }
        if (url == null) {
            url = GerenciadorTema.class.getClassLoader().getResource("estilos/" + nomeArquivo);
        }
        return url;
    }

    private static void aplicarRecursivo(Node no, boolean dark) {
        if (no == null) return;

        if (no instanceof FontIcon fi) {
            if ("iconSair".equals(fi.getId())) {
                fi.setIconColor(Color.valueOf("#FF453A"));
            } else {
                fi.setIconColor(dark ? Color.valueOf("#0A84FF") : Color.valueOf("#007AFF"));
            }
            return;
        }

        if (no instanceof TextInputControl tic) {
            if (dark) {
                tic.setStyle("-fx-background-color: #2C2C2E; -fx-background-radius: 12px; -fx-border-color: transparent; -fx-border-width: 0; -fx-padding: 12px 16px; -fx-font-size: 14px; -fx-text-fill: #F5F5F7; -fx-prompt-text-fill: #8E8E93;");
            } else {
                tic.setStyle("-fx-background-color: #F2F2F7; -fx-background-radius: 12px; -fx-border-color: transparent; -fx-border-width: 0; -fx-padding: 12px 16px; -fx-font-size: 14px; -fx-text-fill: #1D1D1F; -fx-prompt-text-fill: #8E8E93;");
            }
            return;
        }

        if (!no.getProperties().containsKey("origStyle")) {
            no.getProperties().put("origStyle", no.getStyle() != null ? no.getStyle() : "");
        }

        String orig = (String) no.getProperties().get("origStyle");

        if (dark) {
            if (!orig.isEmpty()) {
                String s = orig;
                s = s.replaceAll("(?i)#007aff|#6d28d9|#7c3aed|#5b21b6", "#0A84FF");
                s = s.replaceAll("(?i)#f2f5f9|#f5f5f7|#f5f3ff|#f3f4f6", "#121214");
                s = s.replaceAll("(?i)#ffffff|\\bwhite\\b", "#1c1c1e");
                s = s.replaceAll("(?i)#eff6ff|#faf5ff|#f8f9fb|#f2f2f7|#ede9fe|#e3e9fb", "#2c2c2e");
                s = s.replaceAll("(?i)#dbeafe|#e5e5ea|#d1d1d6|#ddd6fe|#d9dde5|#c7c7cc", "#3a3a3c");
                s = s.replaceAll("(?i)#1d1d1f|#2e1065|#0f1b3d", "#f5f5f7");
                s = s.replaceAll("(?i)#86868b|#6e6e73|#4b5563", "#a1a1aa");
                no.setStyle(s);
            }
        } else {
            String s = orig;
            s = s.replaceAll("(?i)#6d28d9|#7c3aed|#5b21b6", "#007AFF");
            s = s.replaceAll("(?i)#f5f3ff", "#f2f5f9");
            s = s.replaceAll("(?i)#ede9fe", "#eff6ff");
            no.setStyle(s);
        }

        if (no instanceof Parent p) {
            for (Node filho : p.getChildrenUnmodifiable()) {
                aplicarRecursivo(filho, dark);
            }
        }
    }

    public static void alternarTema(Scene scene) {
        isDarkMode = !isDarkMode;
        aplicarTema(scene);
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }
}