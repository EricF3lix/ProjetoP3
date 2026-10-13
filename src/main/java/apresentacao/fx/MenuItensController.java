package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Item;
import facade.SistemaFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

public class MenuItensController extends MenuController {

    @FXML
    private ComboBox<String> cbFiltro;

    @FXML
    private TextField pesquisa;

    @FXML
    private Button btnLimparPesquisa;

    @FXML
    private TableView<Item> tabela;

    @FXML
    private TableColumn<Item, Boolean> colAtivo;

    private final ObservableList<Item> listaItens = FXCollections.observableArrayList();
    private FilteredList<Item> listaFiltrada;

    @Override
    public String getTitulo() {
        return "Gerenciar Itens";
    }

    @FXML
    public void initialize() {
        configurarFiltroPesquisa();
        configurarColunaAtivo();
        if (sistema != null) {
            carregarTabela();
        }
    }

    public void setSistema(SistemaFacade sistema) {
        this.sistema = sistema;
        carregarTabela();
    }

    private void carregarTabela() {
        if (sistema != null && tabela != null) {
            List<Item> itens = sistema.listarItens();
            if (itens != null) {
                listaItens.setAll(itens);
            }
        }
    }

    private void configurarColunaAtivo() {
        if (colAtivo != null) {
            colAtivo.setCellFactory(col -> new TableCell<Item, Boolean>() {
                private final Label badge = new Label();

                @Override
                protected void updateItem(Boolean ativo, boolean empty) {
                    super.updateItem(ativo, empty);

                    if (empty || ativo == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        if (ativo) {
                            badge.setText("● Disponível");
                            badge.setStyle(
                                "-fx-background-color: #d4edda; " +
                                "-fx-text-fill: #155724; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 3 8 3 8; " +
                                "-fx-background-radius: 10; " +
                                "-fx-font-size: 11px;"
                            );
                        } else {
                            badge.setText("● Indisponível");
                            badge.setStyle(
                                "-fx-background-color: #f8d7da; " +
                                "-fx-text-fill: #721c24; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 3 8 3 8; " +
                                "-fx-background-radius: 10; " +
                                "-fx-font-size: 11px;"
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
                aplicarFiltro(pesquisa != null ? pesquisa.getText() : "")
            );
        }

        listaFiltrada = new FilteredList<>(listaItens, p -> true);

        if (pesquisa != null) {
            pesquisa.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro(novo));
        }

        SortedList<Item> listaOrdenada = new SortedList<>(listaFiltrada);
        if (tabela != null) {
            listaOrdenada.comparatorProperty().bind(tabela.comparatorProperty());
            tabela.setItems(listaOrdenada);
        }
    }

    private void aplicarFiltro(String termo) {
        if (listaFiltrada == null) return;

        listaFiltrada.setPredicate(item -> {
            if (item == null) return false;
            if (termo == null || termo.trim().isEmpty()) {
                return true;
            }

            String termoFormatado = termo.trim().toLowerCase();
            String filtroSelecionado = cbFiltro != null && cbFiltro.getValue() != null ? cbFiltro.getValue() : "Todos";

            boolean bateuId = String.valueOf(item.getId()).contains(termoFormatado);
            boolean bateuNome = item.getNome() != null && item.getNome().toLowerCase().contains(termoFormatado);

            return switch (filtroSelecionado) {
                case "ID" -> bateuId;
                case "Nome" -> bateuNome;
                default -> bateuId || bateuNome;
            };
        });
    }

    @FXML
    public void aoLimparPesquisa(ActionEvent event) {
        if (pesquisa != null) {
            pesquisa.clear();
        }
        if (cbFiltro != null) {
            cbFiltro.setValue("Todos");
        }
    }

    @FXML
    public void listarItens(ActionEvent event) {
        aoLimparPesquisa(event);
        carregarTabela();
    }

    @FXML
    public void buscarItem(ActionEvent event) {
        String texto = pesquisa != null ? pesquisa.getText().trim() : "";
        if (texto.isEmpty()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Buscar Item");
            dialog.setHeaderText("Buscar Item por ID");
            dialog.setContentText("Digite o ID do item:");
            Optional<String> resultado = dialog.showAndWait();
            if (resultado.isPresent()) {
                texto = resultado.get().trim();
            } else {
                return;
            }
        }

        try {
            int id = Integer.parseInt(texto);
            Item item = sistema != null ? sistema.buscarItem(id) : null;
            if (item != null) {
                if (cbFiltro != null) cbFiltro.setValue("ID");
                if (pesquisa != null) pesquisa.setText(String.valueOf(id));
                mostrarAlerta(AlertType.INFORMATION, "Item Encontrado",
                    "ID: " + item.getId() + "\n" +
                    "Nome: " + item.getNome() + "\n" +
                    "Categoria: " + item.getCategoria() + "\n" +
                    "Taxa Diária: R$ " + item.getTaxaDiaria() + "\n" +
                    "Estado: " + item.getEstadoConservacao() + "\n" +
                    "Status: " + item.getStatus() + "\n" +
                    "Ativo: " + (item.isAtivo() ? "Sim" : "Não")
                );
            } else {
                mostrarAlerta(AlertType.WARNING, "Não Encontrado", "Nenhum item encontrado com o ID " + id);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(AlertType.ERROR, "Erro", "Por favor, informe um ID numérico válido.");
        }
    }

    @FXML
    public void cadastrarItem(ActionEvent event) {
        carregarTabela();
    }

    @FXML
    public void atualizarItem(ActionEvent event) {
        carregarTabela();
    }

    @FXML
    public void desativarItem(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Desativar Item");
        dialog.setHeaderText("Desativar Item");
        dialog.setContentText("Digite o ID do item:");

        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            try {
                int id = Integer.parseInt(resultado.get().trim());
                if (sistema != null) {
                    boolean sucesso = sistema.excluirItem(id);
                    if (sucesso) {
                        mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Item desativado com sucesso!");
                        carregarTabela();
                    } else {
                        mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível desativar o item.");
                    }
                }
            } catch (NumberFormatException e) {
                mostrarAlerta(AlertType.ERROR, "Erro", "Digite um ID numérico válido.");
            }
        }
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}