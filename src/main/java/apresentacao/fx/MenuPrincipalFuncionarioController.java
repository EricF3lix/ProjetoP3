package apresentacao.fx;

import entidades.Funcionario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.kordamp.ikonli.javafx.FontIcon;

public class MenuPrincipalFuncionarioController extends MenuController {

    @FXML private FontIcon iconSair;
    @FXML private FontIcon iconUsuarios;
    @FXML private FontIcon iconItens;
    @FXML private FontIcon iconCategorias;
    @FXML private FontIcon iconFornecedores;
    @FXML private FontIcon iconContratos;
    @FXML private FontIcon iconMultas;
    @FXML private FontIcon iconRelatorios;

    @FXML private Label lblBemVindo;
    @FXML private Label lblPerfil;
    @FXML private Label lblIniciais;

    private Funcionario funcionario;
    private boolean saudacaoExibida = false;

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        atualizarIdentificacaoUsuario();
    }

    @Override
    protected String getTitulo() {
        return "Menu Funcionário";
    }

    @Override
    protected void aoExibir() {
        carregarIcones();
        Platform.runLater(this::aplicarEfeitoCardsNosBotoes);
        atualizarIdentificacaoUsuario();

        String nome = (funcionario != null && funcionario.getNome() != null)
                ? funcionario.getNome()
                : "Funcionário";

        if (!saudacaoExibida) {
            saudacaoExibida = true;
            Platform.runLater(() -> {
                notificarPopUp("Bem-vindo(a), " + nome + "! Painel operacional pronto.", true);
            });
        }
    }

    private void carregarIcones() {
        if (iconSair != null) iconSair.setIconLiteral("fas-sign-out-alt");
        if (iconUsuarios != null) iconUsuarios.setIconLiteral("fas-users");
        if (iconItens != null) iconItens.setIconLiteral("fas-box");
        if (iconCategorias != null) iconCategorias.setIconLiteral("fas-tag");
        if (iconFornecedores != null) iconFornecedores.setIconLiteral("fas-truck");
        if (iconContratos != null) iconContratos.setIconLiteral("fas-file-alt");
        if (iconMultas != null) iconMultas.setIconLiteral("fas-exclamation-triangle");
        if (iconRelatorios != null) iconRelatorios.setIconLiteral("fas-chart-bar");
    }

    private void aplicarEfeitoCardsNosBotoes() {
        FontIcon[] icones = {
            iconUsuarios, iconItens, iconCategorias,
            iconFornecedores, iconContratos, iconMultas, iconRelatorios
        };

        for (FontIcon icone : icones) {
            if (icone != null) {
                Node atual = icone.getParent();
                while (atual != null && !(atual instanceof Button)) {
                    atual = atual.getParent();
                }
                if (atual instanceof Button btn && !btn.getStyleClass().contains("menu-card")) {
                    btn.getStyleClass().add("menu-card");
                }
            }
        }
    }

    private void atualizarIdentificacaoUsuario() {
        String nome = (funcionario != null && funcionario.getNome() != null && !funcionario.getNome().isBlank())
                ? funcionario.getNome()
                : "Funcionário";

        if (lblBemVindo != null) {
            lblBemVindo.setText("Olá, " + nome);
        }
        if (lblPerfil != null) {
            lblPerfil.setText("Operador · Painel Operacional");
        }
        if (lblIniciais != null) {
            lblIniciais.setText(extrairIniciais(nome));
        }
    }

    private String extrairIniciais(String nome) {
        if (nome == null || nome.isBlank()) return "OP";
        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }
        return ("" + partes[0].charAt(0) + partes[partes.length - 1].charAt(0)).toUpperCase();
    }

    @FXML
    private void abrirCategorias() {
        navegador.abrir("MenuCategorias.fxml");
    }

    @FXML
    private void abrirMultas() {
        navegador.abrir("MenuMultas.fxml");
    }

    @FXML
    private void abrirRelatorios() {
        navegador.abrir("MenuRelatorios.fxml");
    }

    @FXML
    private void abrirUsuarios() { 
        navegador.abrir("MenuUsuariosFuncionario.fxml");
    }

    @FXML
    private void abrirItens() {
        navegador.abrir("MenuItens.fxml");
    }

    @FXML
    private void abrirFornecedores() {
        navegador.abrir("MenuFornecedores.fxml");
    }

    @FXML
    private void abrirContratos() {
        navegador.abrir("MenuContratos.fxml");
    }
}