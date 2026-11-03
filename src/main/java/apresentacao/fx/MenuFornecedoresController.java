package apresentacao.fx;

import entidades.Fornecedor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class MenuFornecedoresController extends MenuController {

    private static final String ID_FORNECEDOR = "Digite o ID do fornecedor: ";
    private static final String NAO_ENCONTRADO = "Fornecedor não encontrado.";

    @FXML private TableView<Fornecedor> tabela;
    @FXML private TableColumn<Fornecedor, Integer> colID;
    @FXML private TableColumn<Fornecedor, String> colNome;
    @FXML private TableColumn<Fornecedor, String> colCNPJ;
    @FXML private TableColumn<Fornecedor, String> colEmail;
    @FXML private TableColumn<Fornecedor, String> colTelefone;
    @FXML private TableColumn<Fornecedor, String> colunaStatus;

    @FXML private ComboBox<String> filtro;
    @FXML private VBox dadosFornecedor;
    @FXML private VBox tabelaFornecedor;
    @FXML private VBox painelDesativar;

    @FXML private Label tituloFornecedor;
    @FXML private Label tituloTabela;

    @FXML private TextField txtNome;
    @FXML private TextField txtCNPJ;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtIdDesativar;

    @FXML private Button btnCadastrar;
    @FXML private Button btnAtualizar;

    private boolean listener = false;

    @Override
    protected String getTitulo() {
        return "Gerenciar Fornecedores";
    }

    @Override
    protected void aoExibir() {
        tabela.setPlaceholder(new Label("Nenhum fornecedor cadastrado"));
        if (!listener) {
            pesquisa.textProperty().addListener((obj, antigo, novo) -> fazPesquisa(novo));
            this.listener = true;
        }
        filtro.setValue("Todos");
        listarFornecedores();
    }

    private void tiraTabela() {
        tabelaFornecedor.setVisible(false);
        tabelaFornecedor.setManaged(false);

        dadosFornecedor.setVisible(true);
        dadosFornecedor.setManaged(true);

        if (painelDesativar != null) {
            painelDesativar.setVisible(false);
            painelDesativar.setManaged(false);
        }
    }

    private void colocaTabela() {
        tabelaFornecedor.setVisible(true);
        tabelaFornecedor.setManaged(true);

        dadosFornecedor.setVisible(false);
        dadosFornecedor.setManaged(false);

        if (painelDesativar != null) {
            painelDesativar.setVisible(false);
            painelDesativar.setManaged(false);
        }
    }

    @FXML
    private void trocaCadastro() {
        tiraTabela();
        tituloFornecedor.setText("Cadastrar Fornecedor");

        btnCadastrar.setVisible(true);
        btnCadastrar.setManaged(true);
        btnAtualizar.setVisible(false);
        btnAtualizar.setManaged(false);
        btnAtualizar.setUserData(null);

        txtNome.clear();
        txtCNPJ.clear();
        txtEmail.clear();
        txtTelefone.clear();
    }

    @FXML
    private void trocaAtualizar() {
        Fornecedor alvo = obterFornecedorAlvo();
        if (alvo == null) return;

        tiraTabela();
        tituloFornecedor.setText("Atualizar Fornecedor");

        btnCadastrar.setVisible(false);
        btnCadastrar.setManaged(false);
        btnAtualizar.setVisible(true);
        btnAtualizar.setManaged(true);
        btnAtualizar.setUserData(alvo);

        txtNome.setText(alvo.getRazaoSocial());
        txtCNPJ.setText(alvo.getCnpj());
        txtEmail.setText(alvo.getEmail());
        txtTelefone.setText(alvo.getTelefone());
    }

    @FXML
    private void cadastrarFornecedor() {
        String nome = txtNome.getText().trim();
        String cnpj = txtCNPJ.getText().trim();
        String email = txtEmail.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isEmpty() || cnpj.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
            notificarPopUpErro("Preencha todos os campos!");
            return;
        }

        int id = sistema.gerarProximoIdFornecedor();
        Fornecedor fornecedor = new Fornecedor(id, nome, cnpj, email, telefone);

        if (sistema.cadastrarFornecedor(fornecedor)) {
            notificarPopUp("Fornecedor cadastrado com sucesso! ID: " + id);
            txtNome.clear();
            txtCNPJ.clear();
            txtEmail.clear();
            txtTelefone.clear();
            listarFornecedores();
        } else {
            notificarPopUpErro("Já existe um fornecedor cadastrado com este CNPJ ou Nome.");
        }
    }

    @FXML
    private void atualizarFornecedor() {
        Fornecedor atual = (Fornecedor) btnAtualizar.getUserData();
        if (atual == null) return;

        String nome = txtNome.getText().trim();
        String cnpj = txtCNPJ.getText().trim();
        String email = txtEmail.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isEmpty() || cnpj.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
            notificarPopUpErro("Preencha todos os campos!");
            return;
        }

        Fornecedor atualizado = new Fornecedor(atual.getId(), nome, cnpj, email, telefone);

        if (sistema.atualizarFornecedor(atualizado)) {
            notificarPopUp("Fornecedor atualizado com sucesso!");
            btnAtualizar.setUserData(null);
            txtNome.clear();
            txtCNPJ.clear();
            txtEmail.clear();
            txtTelefone.clear();
            listarFornecedores();
        } else {
            notificarPopUpErro("Erro ao atualizar dados do fornecedor.");
        }
    }

    @FXML
    private void desativarFornecedor() {
        tabelaFornecedor.setVisible(false);
        tabelaFornecedor.setManaged(false);
        dadosFornecedor.setVisible(false);
        dadosFornecedor.setManaged(false);

        if (painelDesativar != null) {
            painelDesativar.setVisible(true);
            painelDesativar.setManaged(true);

            Fornecedor selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado != null) {
                txtIdDesativar.setText(String.valueOf(selecionado.getId()));
            } else {
                txtIdDesativar.clear();
            }
            txtIdDesativar.requestFocus();
        }
    }

    @FXML
    private void confirmarDesativacaoFornecedor() {
        String textoId = txtIdDesativar.getText().trim();

        if (textoId.isEmpty()) {
            tremer(painelDesativar);
            notificarPopUpErro("Informe o ID do fornecedor");
            return;
        }

        try {
            int id = Integer.parseInt(textoId);

            if (sistema.desativarFornecedor(id)) {
                notificarPopUp("Fornecedor desativado com sucesso.");
                txtIdDesativar.clear();
                listarFornecedores();
            } else {
                tremer(painelDesativar);
                notificarPopUpErro("Não foi possível desativar o fornecedor.");
            }
        } catch (NumberFormatException e) {
            tremer(painelDesativar);
            notificarPopUpErro("Digite um ID numérico válido.");
        }
    }

    @FXML
    private void listarFornecedores() {
        colocaTabela();
        List<Fornecedor> lista = sistema.listarFornecedores();
        if (lista != null) {
            tabela.setItems(FXCollections.observableArrayList(lista));
            tabela.refresh();
        }
    }

    private void fazPesquisa(String pesquisado) {
        colocaTabela();

        if (filtro.getValue() == null) {
            filtro.getSelectionModel().selectFirst();
        }

        switch (filtro.getValue()) {
            case "Razão Social" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarPorNome(pesquisado)));
            case "CNPJ" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarPorCnpj(pesquisado)));
            case "E-mail" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarPorEmail(pesquisado)));
            case "Telefone" -> tabela.setItems(FXCollections.observableArrayList(sistema.buscarPorTelefone(pesquisado)));
            case "ID" -> {
                try {
                    int id = Integer.parseInt(pesquisado.trim());
                    Fornecedor f = sistema.buscarFornecedor(id);
                    tabela.setItems(f != null ? FXCollections.observableArrayList(f) : FXCollections.observableArrayList());
                } catch (Exception e) {
                    tabela.setItems(FXCollections.observableArrayList());
                }
            }
            default -> tabela.setItems(FXCollections.observableArrayList(sistema.listarFornecedores()));
        }
    }

    @FXML
    private void aoLimparPesquisa() {
        colocaTabela();
        pesquisa.clear();
        filtro.getSelectionModel().clearSelection();
    }

    private Fornecedor obterFornecedorAlvo() {
        Fornecedor atual = tabela.getSelectionModel().getSelectedItem();
        tabela.getSelectionModel().clearSelection();

        if (atual == null) {
            Optional<Integer> id = pedirInteiro(ID_FORNECEDOR);
            if (id.isEmpty()) return null;
            atual = sistema.buscarFornecedor(id.get());
            if (atual == null) {
                notificarPopUpErro(NAO_ENCONTRADO);
            }
        }
        return atual;
    }
}