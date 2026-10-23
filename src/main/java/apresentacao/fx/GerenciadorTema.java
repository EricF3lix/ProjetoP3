package apresentacao.fx;

import java.net.URL;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public final class GerenciadorTema {

    private static boolean modoEscuro = false;
    private static final String CSS_CLARO = "/estilos/tema-claro.css";
    private static final String CSS_ESCURO = "/estilos/tema-escuro.css";
    private static final String CHAVE_ESTILO_ORIGINAL = "ESTILO_ORIGINAL_TEMA";

    private GerenciadorTema() {}

    public static boolean isModoEscuro() {
        return modoEscuro;
    }

    public static void alternarTema(Scene cena) {
        modoEscuro = !modoEscuro;
        aplicarTema(cena);
    }

    public static void aplicarTema(Scene cena) {
        if (cena == null) return;

        cena.setFill(modoEscuro ? Color.BLACK : Color.web("#F5F5F7"));

        cena.getStylesheets().removeIf(s -> s.contains("tema-claro.css") || s.contains("tema-escuro.css"));
        String caminho = modoEscuro ? CSS_ESCURO : CSS_CLARO;
        URL recurso = GerenciadorTema.class.getResource(caminho);
        if (recurso != null) {
            cena.getStylesheets().add(recurso.toExternalForm());
        }

        Parent raiz = cena.getRoot();
        if (raiz != null) {
            aplicarTemaEmNo(raiz);
        }
    }

    public static void aplicarTemaEmNo(Parent raiz) {
        if (raiz == null) return;
        processarArvore(raiz, modoEscuro);
    }

    private static void processarArvore(Node no, boolean escuro) {
        if (no == null) return;

        // Não alterar a estrutura visual do próprio botão seletor de tema
        if (no instanceof ThemeToggleSwitch) {
            return;
        }

        // Salva o estilo original da primeira vez que o componente é inspecionado
        if (!no.getProperties().containsKey(CHAVE_ESTILO_ORIGINAL)) {
            String estiloOriginal = no.getStyle() != null ? no.getStyle() : "";
            no.getProperties().put(CHAVE_ESTILO_ORIGINAL, estiloOriginal);
        }

        String estiloOrig = (String) no.getProperties().get(CHAVE_ESTILO_ORIGINAL);

        if (!escuro) {
            // MODO CLARO: restaura rigorosamente o estilo original sem qualquer alteração
            no.setStyle(estiloOrig);
        } else {
            // MODO ESCURO: aplica preto OLED, cartões escuros e texto de alto contraste
            aplicarEstiloEscuro(no, estiloOrig);
        }

        if (no instanceof Parent pai) {
            for (Node filho : pai.getChildrenUnmodifiable()) {
                processarArvore(filho, escuro);
            }
        }
    }

    private static void aplicarEstiloEscuro(Node no, String estiloOrig) {
        String estiloLower = estiloOrig.toLowerCase();

        // 1. Botões
        if (no instanceof Button btn) {
            // Botão de encerrar sessão
            if (estiloLower.contains("encerrar") || estiloLower.contains("#b91c1c") || "btnSair".equals(btn.getId())) {
                btn.setStyle(
                    "-fx-background-color: #14161C !important; " +
                    "-fx-text-fill: #F87171 !important; " +
                    "-fx-border-color: rgba(248, 113, 113, 0.40) !important; " +
                    "-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-font-weight: bold; -fx-cursor: hand;"
                );
            }
            // Botão de perigo / exclusão / desativação
            else if (estiloLower.contains("#fee2e2") || estiloLower.contains("#ff3b30") || estiloLower.contains("#991b1b")) {
                btn.setStyle(
                    "-fx-background-color: rgba(239, 68, 68, 0.16) !important; " +
                    "-fx-text-fill: #F87171 !important; " +
                    "-fx-border-color: rgba(248, 113, 113, 0.40) !important; " +
                    "-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-font-weight: bold; -fx-cursor: hand;"
                );
            }
            // Botões de cards dos menus principais
            else if (btn.getStyleClass().contains("menu-card") || estiloLower.contains("#f8f9fb") || estiloLower.contains("0 14 0 14")) {
                btn.setStyle(
                    "-fx-background-color: #0D0E12 !important; " +
                    "-fx-border-color: #1F2229 !important; " +
                    "-fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 0 14 0 14; -fx-cursor: hand;"
                );
            }
            // Botões de ação gerais (Salvar, Voltar, Buscar, etc.)
            else {
                btn.setStyle(
                    "-fx-background-color: #14161C !important; " +
                    "-fx-text-fill: #FFFFFF !important; " +
                    "-fx-border-color: #262933 !important; " +
                    "-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-font-weight: bold; -fx-cursor: hand;"
                );
            }
            return;
        }

        // 2. Rótulos e Textos (não sobrescreve se for badge de status)
        if (no instanceof Label lbl) {
            if (lbl.getStyleClass().stream().anyMatch(c -> c.startsWith("badge-"))) {
                lbl.setStyle("");
                return;
            }

            // Títulos principais ou labels em destaque
            if (estiloLower.contains("#1c1c1e") || estiloLower.contains("#1d1d1f") || estiloLower.contains("bold") || estiloLower.contains("22px") || estiloLower.contains("17px") || estiloLower.contains("15px")) {
                String limpo = removerPropriedade(estiloOrig, "-fx-text-fill");
                lbl.setStyle(limpo + "; -fx-text-fill: #FFFFFF !important;");
            }
            // Subtítulos e descrições secundárias
            else if (estiloLower.contains("#636366") || estiloLower.contains("#8e8e93") || estiloLower.contains("#86868b") || estiloLower.contains("13px") || estiloLower.contains("12px")) {
                String limpo = removerPropriedade(estiloOrig, "-fx-text-fill");
                lbl.setStyle(limpo + "; -fx-text-fill: #A1A1AA !important;");
            } else {
                String limpo = removerPropriedade(estiloOrig, "-fx-text-fill");
                lbl.setStyle(limpo + "; -fx-text-fill: #FFFFFF;");
            }
            return;
        }

        // 3. Caixas de Texto e Senha
        if (no instanceof TextInputControl txt) {
            txt.setStyle(
                "-fx-background-color: #14161C !important; " +
                "-fx-text-fill: #FFFFFF !important; " +
                "-fx-prompt-text-fill: #6B7280 !important; " +
                "-fx-border-color: #262933 !important; " +
                "-fx-border-radius: 8px; -fx-background-radius: 8px;"
            );
            return;
        }

        // 4. Painéis e Contêineres
        if (no instanceof Pane painel) {
            // Raiz da cena ou containers com cor clara global (#f3f4f6 / #f5f5f7)
            if (painel.getParent() == null || (painel.getScene() != null && painel == painel.getScene().getRoot()) || estiloLower.contains("#f3f4f6") || estiloLower.contains("#f5f5f7")) {
                painel.setStyle("-fx-background-color: #000000 !important;");
            }
            // Cartões brancos de conteúdo (incluindo o modal de Login e as áreas de tabela)
            else if (estiloLower.contains("white") || estiloLower.contains("#ffffff") || estiloLower.contains("card-superficie") || estiloLower.contains("-fx-background-radius: 16")) {
                painel.setStyle(
                    "-fx-background-color: #0D0E12 !important; " +
                    "-fx-background-radius: 16px; " +
                    "-fx-border-color: #1F2229 !important; " +
                    "-fx-border-radius: 16px;"
                );
            }
        }
    }

    private static String removerPropriedade(String estilo, String propriedade) {
        if (estilo == null) return "";
        return estilo.replaceAll(propriedade + ":[^;]+;?", "").trim();
    }
}