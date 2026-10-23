package apresentacao.fx;

import java.util.List;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class MenuUsuariosFuncionarioController extends MenuController {

    @FXML protected StackPane painelConteudo;
    @FXML protected VBox painelTabela;
    @FXML protected ComboBox<String> cbFiltro;
    @FXML protected TextField txtPesquisa;
    @FXML protected Button btnLimparPesquisa;
    @FXML protected TableView<Usuario> tabelaUsuarios;
    @FXML protected TableColumn<Usuario, Integer> colId;
    @FXML protected TableColumn<Usuario, String> colNome;
    @FXML protected TableColumn<Usuario, String> colEmail;
    @FXML protected TableColumn<Usuario, String> colPerfil;
    @FXML protected TableColumn<Usuario, String> colAtivo;

    @FXML protected VBox painelCadastro;
    @FXML protected Label lblCadTitulo;
    @FXML protected TextField txtCadNome;
    @FXML protected TextField txtCadEmail;
    @FXML protected TextField txtCadCpf;
    @FXML protected PasswordField txtCadSenha;
    @FXML protected Button btnCadSalvar;
    @FXML protected Label lblCadMensagem;

    @FXML protected VBox painelBusca;
    @FXML protected TextField txtBuscaId;
    @FXML protected Label lblBuscaResultado;

    @FXML protected Button btnCadastrarCliente;
    @FXML protected Button btnBuscarUsuario;
    @FXML protected Button btnListarUsuarios;
    @FXML protected Button btnVoltar;

    protected ComboBox<String> cbTipoCadastroForm;
    protected String tipoCadastroAtual = "Cliente";
    protected ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    protected FilteredList<Usuario> listaFiltrada;

    @Override
    public String getTitulo() {
        return "Gerenciar Usuários";
    }

    @FXML
    public void initialize() {
        configurarResponsividade();
        configurarColunasTabela();
        configurarFiltroPesquisa();
        removerRedundanciaBusca();
        configurarTabelaVazia();

        Platform.runLater(() -> {
            configurarFormularioUnificado();
            exibirPainel(painelTabela);
            if (sistema != null) {
                carregarTabela();
            }
        });
    }

    private void configurarResponsividade() {
        if (painelConteudo != null) {
            painelConteudo.setMaxWidth(Double.MAX_VALUE);
            painelConteudo.setMaxHeight(Double.MAX_VALUE);
        }

        if (painelTabela != null) {
            VBox.setVgrow(painelTabela, Priority.ALWAYS);
            painelTabela.setMaxWidth(Double.MAX_VALUE);
            painelTabela.setMaxHeight(Double.MAX_VALUE);
            painelTabela.setFillWidth(true);
        }

        if (tabelaUsuarios != null) {
            VBox.setVgrow(tabelaUsuarios, Priority.ALWAYS);
            tabelaUsuarios.setMaxWidth(Double.MAX_VALUE);
            tabelaUsuarios.setMaxHeight(Double.MAX_VALUE);
            tabelaUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }
    }

    private void configurarTabelaVazia() {
        if (tabelaUsuarios == null) return;

        VBox estadoVazio = new VBox(8);
        estadoVazio.setAlignment(Pos.CENTER);
        estadoVazio.setStyle("-fx-padding: 30px;");

        FontIcon icone = new FontIcon("fas-search");
        icone.setIconSize(32);
        icone.setStyle("-fx-icon-color: #8e8e93;");

        Label titulo = new Label("Nenhum usuário encontrado");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        Label subtitulo = new Label("Ajuste os termos da busca ou mude a opção de filtro.");
        subtitulo.getStyleClass().add("label-subtitulo");
        subtitulo.setStyle("-fx-font-size: 13px;");

        estadoVazio.getChildren().addAll(icone, titulo, subtitulo);
        tabelaUsuarios.setPlaceholder(estadoVazio);
    }

    private void removerRedundanciaBusca() {
        if (btnBuscarUsuario != null) {
            btnBuscarUsuario.setVisible(false);
            btnBuscarUsuario.setManaged(false);
        }
    }

    protected void configurarFormularioUnificado() {
        try {
            if (btnCadastrarCliente != null) {
                btnCadastrarCliente.setText("Novo Usuário");
            }

            if (painelCadastro != null && cbTipoCadastroForm == null) {
                cbTipoCadastroForm = new ComboBox<>(obterOpcoesCadastro());
                cbTipoCadastroForm.setValue("Cliente");
                cbTipoCadastroForm.setMaxWidth(Double.MAX_VALUE);

                cbTipoCadastroForm.valueProperty().addListener((obs, antigo, novo) -> {
                    if (novo != null) {
                        this.tipoCadastroAtual = novo;
                        if (lblCadTitulo != null) lblCadTitulo.setText("Cadastrar " + novo);
                        if (btnCadSalvar != null) btnCadSalvar.setText("Salvar " + novo);
                    }
                });

                if (!painelCadastro.getChildren().contains(cbTipoCadastroForm)) {
                    int indice = 0;
                    if (lblCadTitulo != null && painelCadastro.getChildren().contains(lblCadTitulo)) {
                        indice = painelCadastro.getChildren().indexOf(lblCadTitulo) + 1;
                    }
                    if (indice >= 0 && indice <= painelCadastro.getChildren().size()) {
                        painelCadastro.getChildren().add(indice, cbTipoCadastroForm);
                    } else {
                        painelCadastro.getChildren().add(cbTipoCadastroForm);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Aviso controlado em configurarFormularioUnificado: " + e.getMessage());
        }
    }

    protected ObservableList<String> obterOpcoesCadastro() {
        return FXCollections.observableArrayList("Cliente");
    }

    private void configurarColunasTabela() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colNome != null) colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        if (colEmail != null) colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        if (colPerfil != null) {
            colPerfil.setCellValueFactory(dado ->
                new SimpleStringProperty(dado.getValue().getClass().getSimpleName()));
        }

        if (colAtivo != null) {
            colAtivo.setCellValueFactory(dado ->
                new SimpleStringProperty(dado.getValue().isAtivo() ? "Sim" : "Não"));
            colAtivo.setCellFactory(col -> new TableCell<Usuario, String>() {
                private final Label badge = new Label();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        boolean ativo = "Sim".equalsIgnoreCase(item) || "true".equalsIgnoreCase(item);
                        badge.getStyleClass().removeAll("badge-sucesso", "badge-perigo");
                        if (ativo) {
                            badge.setText("● Ativo");
                            badge.getStyleClass().add("badge-sucesso");
                        } else {
                            badge.setText("● Inativo");
                            badge.getStyleClass().add("badge-perigo");
                        }
                        setAlignment(Pos.CENTER);
                        setGraphic(badge);
                        setText(null);
                    }
                }
            });
        }
    }

    private void configurarFiltroPesquisa() {
        if (cbFiltro != null) {
            cbFiltro.setItems(FXCollections.observableArrayList("Todos", "Nome", "ID", "E-mail", "CPF"));
            cbFiltro.setValue("Todos");
            cbFiltro.valueProperty().addListener((obs, antigo, novo) ->
                aplicarFiltro(txtPesquisa != null ? txtPesquisa.getText() : "")
            );
        }

        listaFiltrada = new FilteredList<>(listaUsuarios, p -> true);

        if (txtPesquisa != null) {
            txtPesquisa.textProperty().addListener((obs, valorAntigo, novoValor) -> aplicarFiltro(novoValor));
        }

        SortedList<Usuario> listaOrdenada = new SortedList<>(listaFiltrada);
        if (tabelaUsuarios != null) {
            listaOrdenada.comparatorProperty().bind(tabelaUsuarios.comparatorProperty());
            tabelaUsuarios.setItems(listaOrdenada);
        }
    }

    private void aplicarFiltro(String termo) {
        if (listaFiltrada == null) return;

        listaFiltrada.setPredicate(usuario -> {
            if (usuario == null) return false;
            if (termo == null || termo.trim().isEmpty()) {
                return true;
            }

            String termoFormatado = termo.trim().toLowerCase();
            String termoNumerico = termoFormatado.replaceAll("\\D", "");
            String filtroSelecionado = (cbFiltro != null && cbFiltro.getValue() != null) ? cbFiltro.getValue() : "Todos";

            boolean bateuId = String.valueOf(usuario.getId()).contains(termoFormatado);
            boolean bateuNome = usuario.getNome() != null && usuario.getNome().toLowerCase().contains(termoFormatado);
            boolean bateuEmail = usuario.getEmail() != null && usuario.getEmail().toLowerCase().contains(termoFormatado);

            String cpfUsuario = usuario.getCpf() != null ? usuario.getCpf().replaceAll("\\D", "") : "";
            boolean bateuCpf = !termoNumerico.isEmpty() && cpfUsuario.contains(termoNumerico);

            return switch (filtroSelecionado) {
                case "ID" -> bateuId;
                case "Nome" -> bateuNome;
                case "E-mail" -> bateuEmail;
                case "CPF" -> bateuCpf;
                default -> bateuId || bateuNome || bateuEmail || bateuCpf;
            };
        });
    }

    @FXML
    public void aoLimparPesquisa(ActionEvent event) {
        if (txtPesquisa != null) txtPesquisa.clear();
        if (cbFiltro != null) cbFiltro.setValue("Todos");
    }

    public void setSistema(SistemaFacade sistema) {
        this.sistema = sistema;
        carregarTabela();
    }

    protected void carregarTabela() {
        if (sistema != null) {
            List<Usuario> usuarios = sistema.listarUsuarios();
            if (usuarios != null) {
                listaUsuarios.setAll(usuarios);
            }
        }
    }

    protected void exibirPainel(Node painelDesejado) {
        Node[] paineis = { painelTabela, painelCadastro, painelBusca };

        for (Node painel : paineis) {
            if (painel != null && painel != painelDesejado) {
                painel.setVisible(false);
                painel.setOpacity(0.0);
            }
        }

        if (painelDesejado != null) {
            painelDesejado.setOpacity(0.0);
            painelDesejado.setVisible(true);
            painelDesejado.toFront();

            FadeTransition fade = new FadeTransition(Duration.millis(200), painelDesejado);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
        }
    }

    @FXML
    public void aoListarUsuarios(ActionEvent event) {
        aoLimparPesquisa(event);
        exibirPainel(painelTabela);
        carregarTabela();
    }

    @FXML
    public void aoCadastrarCliente(ActionEvent event) {
        prepararFormularioCadastro(tipoCadastroAtual);
    }

    protected void prepararFormularioCadastro(String tipo) {
        this.tipoCadastroAtual = tipo;
        if (cbTipoCadastroForm != null) {
            cbTipoCadastroForm.setValue(tipo);
        }
        if (lblCadTitulo != null) lblCadTitulo.setText("Cadastrar " + tipo);
        if (btnCadSalvar != null) btnCadSalvar.setText("Salvar " + tipo);
        limparCamposCadastro();
        if (lblCadMensagem != null) lblCadMensagem.setText("");
        exibirPainel(painelCadastro);
    }

    @FXML
    public void aoSalvarCadastro(ActionEvent event) {
        if (sistema == null) return;

        String nome = txtCadNome.getText().trim();
        String email = txtCadEmail.getText().trim();
        String cpf = txtCadCpf.getText().trim();
        String senha = txtCadSenha.getText().trim();

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || senha.isEmpty()) {
            if (lblCadMensagem != null) {
                lblCadMensagem.setText("Preencha todos os campos obrigatórios.");
                lblCadMensagem.setStyle("-fx-text-fill: #F87171; -fx-font-weight: bold;");
            }
            marcarCampoInvalido(txtCadNome, nome.isEmpty());
            marcarCampoInvalido(txtCadEmail, email.isEmpty());
            marcarCampoInvalido(txtCadCpf, cpf.isEmpty());
            marcarCampoInvalido(txtCadSenha, senha.isEmpty());

            animarErroShake(painelCadastro);
            return;
        }

        int id = sistema.gerarProximoIdUsuario();
        Usuario novoUsuario = switch (tipoCadastroAtual) {
            case "Funcionário" -> new Funcionario(id, nome, email, cpf, senha);
            case "Administrador" -> new Administrador(id, nome, email, cpf, senha);
            default -> new Cliente(id, nome, email, cpf, senha);
        };

        if (sistema.cadastrarUsuario(novoUsuario)) {
            if (lblCadMensagem != null) {
                lblCadMensagem.setText(tipoCadastroAtual + " cadastrado com sucesso! ID: " + id);
                lblCadMensagem.setStyle("-fx-text-fill: #34D399; -fx-font-weight: bold;");
            }
            limparCamposCadastro();
            carregarTabela();
            notificarPopUp(tipoCadastroAtual + " cadastrado com sucesso!", true);
        } else {
            if (lblCadMensagem != null) {
                lblCadMensagem.setText("Erro: CPF ou e-mail já cadastrado.");
                lblCadMensagem.setStyle("-fx-text-fill: #F87171; -fx-font-weight: bold;");
            }
            marcarCampoInvalido(txtCadEmail, true);
            marcarCampoInvalido(txtCadCpf, true);
            animarErroShake(painelCadastro);
        }
    }

    private void marcarCampoInvalido(TextField campo, boolean invalido) {
        if (campo == null) return;
        if (invalido) {
            if (!campo.getStyleClass().contains("campo-invalido")) {
                campo.getStyleClass().add("campo-invalido");
            }
            PauseTransition pausa = new PauseTransition(Duration.seconds(2.5));
            pausa.setOnFinished(e -> campo.getStyleClass().remove("campo-invalido"));
            pausa.play();
        } else {
            campo.getStyleClass().remove("campo-invalido");
        }
    }

    private void limparCamposCadastro() {
        txtCadNome.clear();
        txtCadEmail.clear();
        txtCadCpf.clear();
        txtCadSenha.clear();
        txtCadNome.getStyleClass().remove("campo-invalido");
        txtCadEmail.getStyleClass().remove("campo-invalido");
        txtCadCpf.getStyleClass().remove("campo-invalido");
        txtCadSenha.getStyleClass().remove("campo-invalido");
    }

    @FXML
    public void aoBuscarUsuario(ActionEvent event) {
        if (txtBuscaId != null) txtBuscaId.clear();
        if (lblBuscaResultado != null) lblBuscaResultado.setText("");
        exibirPainel(painelBusca);
    }

    @FXML
    public void aoConfirmarBusca(ActionEvent event) {
        if (sistema == null || txtBuscaId == null) return;

        try {
            int id = Integer.parseInt(txtBuscaId.getText().trim());
            Usuario usuario = sistema.buscarUsuario(id);

            if (usuario != null) {
                if (lblBuscaResultado != null) {
                    lblBuscaResultado.setText(
                        "ID: " + usuario.getId() + "\n" +
                        "Nome: " + usuario.getNome() + "\n" +
                        "E-mail: " + usuario.getEmail() + "\n" +
                        "CPF: " + usuario.getCpf() + "\n" +
                        "Perfil: " + usuario.getClass().getSimpleName() + "\n" +
                        "Ativo: " + (usuario.isAtivo() ? "Sim" : "Não")
                    );
                    lblBuscaResultado.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 13px;");
                }
            } else {
                if (lblBuscaResultado != null) {
                    lblBuscaResultado.setText("Usuário não encontrado.");
                    lblBuscaResultado.setStyle("-fx-text-fill: #F87171; -fx-font-weight: bold;");
                }
                animarErroShake(painelBusca);
            }
        } catch (NumberFormatException ex) {
            if (lblBuscaResultado != null) {
                lblBuscaResultado.setText("Digite um ID numérico válido.");
                lblBuscaResultado.setStyle("-fx-text-fill: #F87171; -fx-font-weight: bold;");
            }
            marcarCampoInvalido(txtBuscaId, true);
            animarErroShake(painelBusca);
        }
    }
}