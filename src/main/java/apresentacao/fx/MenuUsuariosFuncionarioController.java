package apresentacao.fx;

import java.util.List;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;
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

public class MenuUsuariosFuncionarioController extends MenuController {
    private static final String STYLE_TEXT_RED = "-fx-text-fill: red;";
    private static final String OPCAO_TODOS = "Todos";
    protected static final String TIPO_CLIENTE = "Cliente";

    @FXML
    protected StackPane painelConteudo;

    @FXML
    protected VBox painelTabela;

    @FXML
    protected ComboBox<String> cbFiltro;

    @FXML
    protected TextField txtPesquisa;

    @FXML
    protected Button btnLimparPesquisa;

    @FXML
    protected TableView<Usuario> tabelaUsuarios;

    @FXML
    protected TableColumn<Usuario, Integer> colId;

    @FXML
    protected TableColumn<Usuario, String> colNome;

    @FXML
    protected TableColumn<Usuario, String> colEmail;

    @FXML
    protected TableColumn<Usuario, String> colPerfil;

    @FXML
    protected TableColumn<Usuario, String> colAtivo;

    @FXML
    protected VBox painelCadastro;

    @FXML
    protected Label lblCadTitulo;

    @FXML
    protected TextField txtCadNome;

    @FXML
    protected TextField txtCadEmail;

    @FXML
    protected TextField txtCadCpf;

    @FXML
    protected PasswordField txtCadSenha;

    @FXML
    protected Button btnCadSalvar;

    @FXML
    protected Label lblCadMensagem;

    @FXML
    protected VBox painelBusca;

    @FXML
    protected TextField txtBuscaId;

    @FXML
    protected Label lblBuscaResultado;

    @FXML
    protected Button btnCadastrarCliente;

    @FXML
    protected Button btnBuscarUsuario;

    @FXML
    protected Button btnListarUsuarios;

    @FXML
    protected Button btnVoltar;

    protected ComboBox<String> cbTipoCadastroForm;
    protected String tipoCadastroAtual = TIPO_CLIENTE;
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
        configurarFormularioUnificado();

        Platform.runLater(() -> {
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

    private void removerRedundanciaBusca() {
        if (btnBuscarUsuario != null) {
            btnBuscarUsuario.setVisible(false);
            btnBuscarUsuario.setManaged(false);
        }
    }

    protected void configurarFormularioUnificado() {
        if (btnCadastrarCliente != null) {
            btnCadastrarCliente.setText("Novo Usuário");
        }

        if (painelCadastro != null && cbTipoCadastroForm == null) {
            inicializarComboBoxCadastro();
        }
    }

    private void inicializarComboBoxCadastro() {
        cbTipoCadastroForm = new ComboBox<>(obterOpcoesCadastro());
        cbTipoCadastroForm.setValue(TIPO_CLIENTE);
        cbTipoCadastroForm.setMaxWidth(Double.MAX_VALUE);

        cbTipoCadastroForm.valueProperty().addListener((obs, antigo, novo) -> atualizarRotulosFormulario(novo));

        int indice = calcularIndiceComboBox();
        painelCadastro.getChildren().add(indice, cbTipoCadastroForm);
    }

    private void atualizarRotulosFormulario(String novoTipo) {
        if (novoTipo == null) return;
        this.tipoCadastroAtual = novoTipo;
        if (lblCadTitulo != null) {
            lblCadTitulo.setText("Cadastrar " + novoTipo);
        }
        if (btnCadSalvar != null) {
            btnCadSalvar.setText("Salvar " + novoTipo);
        }
    }

    private int calcularIndiceComboBox() {
        if (lblCadTitulo != null && painelCadastro.getChildren().contains(lblCadTitulo)) {
            return painelCadastro.getChildren().indexOf(lblCadTitulo) + 1;
        }
        return 0;
    }

    protected ObservableList<String> obterOpcoesCadastro() {
        return FXCollections.observableArrayList(TIPO_CLIENTE);
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
            colAtivo.setCellFactory(col -> criarCelulaStatus());
        }
    }

    private TableCell<Usuario, String> criarCelulaStatus() {
        return new TableCell<>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                formatarBadgeStatus(badge, item);
                setAlignment(Pos.CENTER);
                setGraphic(badge);
                setText(null);
            }
        };
    }

    private void formatarBadgeStatus(Label badge, String item) {
        boolean ativo = "Sim".equalsIgnoreCase(item) || "true".equalsIgnoreCase(item);
        if (ativo) {
            badge.setText("● Ativo");
            badge.setStyle(
                "-fx-background-color: #d4edda; -fx-text-fill: #155724; " +
                "-fx-font-weight: bold; -fx-padding: 3 8 3 8; " +
                "-fx-background-radius: 10; -fx-font-size: 11px;"
            );
        } else {
            badge.setText("● Inativo");
            badge.setStyle(
                "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; " +
                "-fx-font-weight: bold; -fx-padding: 3 8 3 8; " +
                "-fx-background-radius: 10; -fx-font-size: 11px;"
            );
        }
    }

    private void configurarFiltroPesquisa() {
        if (cbFiltro != null) {
            cbFiltro.setItems(FXCollections.observableArrayList(OPCAO_TODOS, "Nome", "ID", "E-mail", "CPF"));
            cbFiltro.setValue(OPCAO_TODOS);
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

        String filtroSelecionado = (cbFiltro != null && cbFiltro.getValue() != null) ? cbFiltro.getValue() : OPCAO_TODOS;

        listaFiltrada.setPredicate(usuario -> usuarioAtendeFiltro(usuario, termo, filtroSelecionado));
    }

    private boolean usuarioAtendeFiltro(Usuario usuario, String termo, String filtro) {
        if (usuario == null) return false;
        if (termo == null || termo.trim().isEmpty()) return true;

        String termoFormatado = termo.trim().toLowerCase();

        return switch (filtro) {
            case "ID" -> bateuId(usuario, termoFormatado);
            case "Nome" -> bateuNome(usuario, termoFormatado);
            case "E-mail" -> bateuEmail(usuario, termoFormatado);
            case "CPF" -> bateuCpf(usuario, termoFormatado);
            default -> bateuAlgumCampo(usuario, termoFormatado);
        };
    }

    private boolean bateuId(Usuario u, String termo) {
        return String.valueOf(u.getId()).contains(termo);
    }

    private boolean bateuNome(Usuario u, String termo) {
        return u.getNome() != null && u.getNome().toLowerCase().contains(termo);
    }

    private boolean bateuEmail(Usuario u, String termo) {
        return u.getEmail() != null && u.getEmail().toLowerCase().contains(termo);
    }

    private boolean bateuCpf(Usuario u, String termo) {
        if (u.getCpf() == null) return false;
        String termoNumerico = termo.replaceAll("\\D", "");
        if (termoNumerico.isEmpty()) return false;
        String cpfUsuario = u.getCpf().replaceAll("\\D", "");
        return cpfUsuario.contains(termoNumerico);
    }

    private boolean bateuAlgumCampo(Usuario u, String termo) {
        return bateuId(u, termo) || bateuNome(u, termo) || bateuEmail(u, termo) || bateuCpf(u, termo);
    }

    @FXML
    public void aoLimparPesquisa(ActionEvent event) {
        if (txtPesquisa != null) txtPesquisa.clear();
        if (cbFiltro != null) cbFiltro.setValue(OPCAO_TODOS);
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
        if (painelTabela != null) painelTabela.setVisible(painelDesejado == painelTabela);
        if (painelCadastro != null) painelCadastro.setVisible(painelDesejado == painelCadastro);
        if (painelBusca != null) painelBusca.setVisible(painelDesejado == painelBusca);
        if (painelDesejado != null) {
            painelDesejado.toFront();
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
        txtCadNome.clear();
        txtCadEmail.clear();
        txtCadCpf.clear();
        txtCadSenha.clear();
        lblCadMensagem.setText("");
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
            lblCadMensagem.setText("Por favor, preencha todos os campos.");
            lblCadMensagem.setStyle(STYLE_TEXT_RED);
            return;
        }

        int id = sistema.gerarProximoIdUsuario();
        Usuario novoUsuario = switch (tipoCadastroAtual) {
            case "Funcionário" -> new Funcionario(id, nome, email, cpf, senha);
            case "Administrador" -> new Administrador(id, nome, email, cpf, senha);
            default -> new Cliente(id, nome, email, cpf, senha);
        };

        if (sistema.cadastrarUsuario(novoUsuario)) {
            lblCadMensagem.setText(tipoCadastroAtual + " cadastrado com sucesso! ID: " + id);
            lblCadMensagem.setStyle("-fx-text-fill: green;");
            txtCadNome.clear();
            txtCadEmail.clear();
            txtCadCpf.clear();
            txtCadSenha.clear();
            carregarTabela();
        } else {
            lblCadMensagem.setText("Erro: CPF ou e-mail já cadastrado.");
            lblCadMensagem.setStyle(STYLE_TEXT_RED);
        }
    }

    @FXML
    public void aoBuscarUsuario(ActionEvent event) {
        txtBuscaId.clear();
        lblBuscaResultado.setText("");
        exibirPainel(painelBusca);
    }

    @FXML
    public void aoConfirmarBusca(ActionEvent event) {
        if (sistema == null) return;

        try {
            int id = Integer.parseInt(txtBuscaId.getText().trim());
            Usuario usuario = sistema.buscarUsuario(id);

            if (usuario != null) {
                lblBuscaResultado.setText(
                    "ID: " + usuario.getId() + "\n" +
                    "Nome: " + usuario.getNome() + "\n" +
                    "E-mail: " + usuario.getEmail() + "\n" +
                    "CPF: " + usuario.getCpf() + "\n" +
                    "Perfil: " + usuario.getClass().getSimpleName() + "\n" +
                    "Ativo: " + (usuario.isAtivo() ? "Sim" : "Não")
                );
                lblBuscaResultado.setStyle("-fx-text-fill: black;");
            } else {
                lblBuscaResultado.setText("Usuário não encontrado.");
                lblBuscaResultado.setStyle(STYLE_TEXT_RED);
            }
        } catch (NumberFormatException ex) {
            lblBuscaResultado.setText("Digite um ID numérico válido.");
            lblBuscaResultado.setStyle(STYLE_TEXT_RED);
        }
    }
}