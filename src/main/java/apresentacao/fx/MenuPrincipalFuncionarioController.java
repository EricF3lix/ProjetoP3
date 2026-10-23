package apresentacao.fx;

import java.util.List;

import entidades.Funcionario;
import entidades.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

public class MenuPrincipalFuncionarioController extends MenuController {

    @FXML private FontIcon iconSair;
    @FXML private FontIcon iconTema;
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

    @FXML private Label kpiUsuarios;
    @FXML private Label kpiItens;
    @FXML private Label kpiCategorias;
    @FXML private Label kpiFornecedores;
    @FXML private Label kpiContratos;
    @FXML private Label kpiMultas;
    @FXML private Label kpiRelatorios;

    @FXML private Button btnTema;
    @FXML private Button btnUsuarios;
    @FXML private Button btnItens;
    @FXML private Button btnCategorias;
    @FXML private Button btnFornecedores;
    @FXML private Button btnContratos;
    @FXML private Button btnMultas;
    @FXML private Button btnRelatorios;

    @FXML private HBox boxUsuarios;
    @FXML private HBox boxItens;
    @FXML private HBox boxCategorias;
    @FXML private HBox boxFornecedores;
    @FXML private HBox boxContratos;
    @FXML private HBox boxMultas;
    @FXML private HBox boxRelatorios;

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
        Platform.runLater(this::ajustarLarguraBoxes);
        atualizarIdentificacaoUsuario();
        carregarIndicadoresKPI();
        atualizarIconeTema();

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

    @FXML
    private void alternarTema() {
        if (btnTema != null && btnTema.getScene() != null) {
            GerenciadorTema.alternarTema(btnTema.getScene());
            atualizarIconeTema();
            carregarIndicadoresKPI();
            boolean escuro = GerenciadorTema.isModoEscuro();
            notificarPopUp("Modo " + (escuro ? "Escuro" : "Claro") + " ativado.", true);
        }
    }

    private void atualizarIconeTema() {
        if (iconTema == null) return;
        boolean escuro = GerenciadorTema.isModoEscuro();
        iconTema.setIconLiteral(escuro ? "fas-sun" : "fas-moon");
        iconTema.setIconColor(Color.web(escuro ? "#f59e0b" : "#1c1c1e"));
    }

    private void ajustarLarguraBoxes() {
        vincularLargura(btnUsuarios, boxUsuarios);
        vincularLargura(btnItens, boxItens);
        vincularLargura(btnCategorias, boxCategorias);
        vincularLargura(btnFornecedores, boxFornecedores);
        vincularLargura(btnContratos, boxContratos);
        vincularLargura(btnMultas, boxMultas);
        vincularLargura(btnRelatorios, boxRelatorios);
    }

    private void vincularLargura(Button btn, HBox box) {
        if (btn != null && box != null) {
            box.prefWidthProperty().bind(btn.widthProperty().subtract(36));
        }
    }

    private void carregarIndicadoresKPI() {
        if (sistema == null) return;

        // KPI Usuários
        try {
            List<Usuario> usuarios = sistema.listarUsuarios();
            if (usuarios != null) {
                long ativos = usuarios.stream().filter(Usuario::isAtivo).count();
                atualizarBadge(kpiUsuarios, ativos + " ativos", "badge-info");
            }
        } catch (Exception ignored) {}

        // KPI Itens
        try {
            var itens = sistema.listarItens();
            if (itens != null) {
                atualizarBadge(kpiItens, itens.size() + " itens", "badge-info");
            }
        } catch (Exception ignored) {}

        // KPI Categorias
        try {
            var metodo = sistema.getClass().getMethod("listarCategorias");
            var lista = (List<?>) metodo.invoke(sistema);
            if (lista != null) {
                atualizarBadge(kpiCategorias, lista.size() + " ativas", "badge-sucesso");
            }
        } catch (Exception e) {
            atualizarBadge(kpiCategorias, "Catálogo", "badge-sucesso");
        }

        // KPI Fornecedores
        try {
            var fornecedores = sistema.listarFornecedores();
            if (fornecedores != null) {
                atualizarBadge(kpiFornecedores, fornecedores.size() + " parceiros", "badge-aviso");
            }
        } catch (Exception ignored) {}

        // KPI Contratos
        try {
            var contratos = sistema.gerarRelatorioItensAlugados();
            if (contratos != null) {
                atualizarBadge(kpiContratos, contratos.size() + " em aberto", "badge-info");
            }
        } catch (Exception ignored) {}

        // KPI Multas
        try {
            var pendentes = sistema.listarMultasPendentes();
            if (pendentes != null && !pendentes.isEmpty()) {
                atualizarBadge(kpiMultas, pendentes.size() + " pendente" + (pendentes.size() > 1 ? "s" : ""), "badge-perigo");
            } else {
                atualizarBadge(kpiMultas, "Zeradas", "badge-sucesso");
            }
        } catch (Exception ignored) {}

        // KPI Relatórios
        atualizarBadge(kpiRelatorios, "PDF", "badge-neutro");
    }

    private void atualizarBadge(Label badge, String texto, String tipoClasse) {
        if (badge == null) return;
        badge.setText(texto);
        badge.setStyle("");
        badge.getStyleClass().removeAll("badge-sucesso", "badge-perigo", "badge-aviso", "badge-info", "badge-neutro");
        badge.getStyleClass().add(tipoClasse);
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
        Button[] botoes = { btnUsuarios, btnItens, btnCategorias, btnFornecedores, btnContratos, btnMultas, btnRelatorios };
        for (Button btn : botoes) {
            if (btn != null && !btn.getStyleClass().contains("menu-card")) {
                btn.getStyleClass().add("menu-card");
            }
        }
    }

    private void atualizarIdentificacaoUsuario() {
        String nome = (funcionario != null && funcionario.getNome() != null && !funcionario.getNome().isBlank())
                ? funcionario.getNome()
                : "Funcionário";

        if (lblBemVindo != null) lblBemVindo.setText("Olá, " + nome);
        if (lblPerfil != null) lblPerfil.setText("Operador · Painel Operacional");
        if (lblIniciais != null) lblIniciais.setText(extrairIniciais(nome));
    }

    private String extrairIniciais(String nome) {
        if (nome == null || nome.isBlank()) return "OP";
        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }
        return ("" + partes[0].charAt(0) + partes[partes.length - 1].charAt(0)).toUpperCase();
    }

    @FXML private void abrirCategorias() { navegador.abrir("MenuCategorias.fxml"); }
    @FXML private void abrirMultas() { navegador.abrir("MenuMultas.fxml"); }
    @FXML private void abrirRelatorios() { navegador.abrir("MenuRelatorios.fxml"); }
    @FXML private void abrirItens() { navegador.abrir("MenuItens.fxml"); }
    @FXML private void abrirFornecedores() { navegador.abrir("MenuFornecedores.fxml"); }
    @FXML private void abrirContratos() { navegador.abrir("MenuContratos.fxml"); }

    @FXML
    private void abrirUsuarios() {
        try {
            navegador.abrir("MenuUsuariosFuncionario.fxml");
        } catch (Exception e) {
            System.err.println("Tentativa direta falhou, buscando alternativa: " + e.getMessage());
            try {
                navegador.abrir("MenuUsuariosAdmin.fxml");
            } catch (Exception e2) {
                animarErroShake(btnUsuarios);
                notificarPopUp("Erro ao carregar módulo de usuários.", false);
            }
        }
    }
}