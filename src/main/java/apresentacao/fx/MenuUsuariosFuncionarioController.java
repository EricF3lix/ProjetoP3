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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MenuUsuariosFuncionarioController extends MenuController {

    @FXML
    protected AnchorPane painelConteudo;

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

    protected String tipoCadastroAtual = "Cliente";
    protected ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    protected FilteredList<Usuario> listaFiltrada;

    @Override
    public String getTitulo() {
        return "Gerenciar Usuários";
    }

    @FXML
    public void initialize() {
        configurarColunasTabela();
        configurarFiltroPesquisa();

        Platform.runLater(() -> {
            exibirPainel(painelTabela);
            if (sistema != null) {
                carregarTabela();
            }
        });
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
            cbFiltro.setItems(FXCollections.observableArrayList("Todos", "Nome", "ID"));
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
            String filtroSelecionado = cbFiltro != null && cbFiltro.getValue() != null ? cbFiltro.getValue() : "Todos";

            boolean bateuId = String.valueOf(usuario.getId()).contains(termoFormatado);
            boolean bateuNome = usuario.getNome() != null && usuario.getNome().toLowerCase().contains(termoFormatado);

            return switch (filtroSelecionado) {
                case "ID" -> bateuId;
                case "Nome" -> bateuNome;
                default -> bateuId || bateuNome;
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
        prepararFormularioCadastro("Cliente");
    }

    protected void prepararFormularioCadastro(String tipo) {
        this.tipoCadastroAtual = tipo;
        lblCadTitulo.setText("Cadastrar " + tipo);
        btnCadSalvar.setText("Salvar " + tipo);
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
            lblCadMensagem.setStyle("-fx-text-fill: red;");
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
            lblCadMensagem.setStyle("-fx-text-fill: red;");
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
                lblBuscaResultado.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException ex) {
            lblBuscaResultado.setText("Digite um ID numérico válido.");
            lblBuscaResultado.setStyle("-fx-text-fill: red;");
        }
    }
}