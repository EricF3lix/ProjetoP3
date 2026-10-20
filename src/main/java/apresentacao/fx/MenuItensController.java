package apresentacao.fx;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import entidades.Item;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MenuItensController extends MenuController {

    @FXML
    private ComboBox<String> cbFiltro;

    @FXML
    private TextField pesquisa;

    @FXML
    private Button btnLimparPesquisa;

    @FXML
    private Button btnBuscarItem;

    @FXML
    private TableView<Item> tabela;

    @FXML
    private TableColumn<Item, Double> colTaxaDiaria;

    @FXML
    private TableColumn<Item, Boolean> colAtivo;

    private final ObservableList<Item> listaItens = FXCollections.observableArrayList();
    private FilteredList<Item> listaFiltrada;

    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @Override
    public String getTitulo() {
        return "Gerenciar Itens";
    }

    @FXML
    public void initialize() {
        configurarResponsividade();
        configurarFiltroPesquisa();
        configurarColunaTaxaDiaria();
        configurarColunaAtivo();
        removerRedundanciaBusca();
    }

    @Override
    protected void aoExibir() {
        carregarTabela();
    }

    private void configurarResponsividade() {
        if (tabela != null) {
            VBox.setVgrow(tabela, Priority.ALWAYS);
            tabela.setMaxWidth(Double.MAX_VALUE);
            tabela.setMaxHeight(Double.MAX_VALUE);
            tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }
    }

    private void removerRedundanciaBusca() {
        if (btnBuscarItem != null) {
            btnBuscarItem.setVisible(false);
            btnBuscarItem.setManaged(false);
        }
    }

    private void carregarTabela() {
        if (sistema != null && tabela != null) {
            List<Item> itens = sistema.listarItens();
            if (itens != null) {
                listaItens.setAll(itens);
            }
        }
    }

    private void configurarColunaTaxaDiaria() {
        if (colTaxaDiaria != null) {
            colTaxaDiaria.setCellFactory(col -> new TableCell<Item, Double>() {
                @Override
                protected void updateItem(Double valor, boolean empty) {
                    super.updateItem(valor, empty);
                    if (empty || valor == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(MOEDA.format(valor));
                        setAlignment(Pos.CENTER_RIGHT);
                    }
                }
            });
        } else if (tabela != null) {
            for (TableColumn<Item, ?> col : tabela.getColumns()) {
                String titulo = col.getText() != null ? col.getText().toLowerCase() : "";
                if (titulo.contains("taxa") || titulo.contains("diária") || titulo.contains("diaria")) {
                    @SuppressWarnings("unchecked")
                    TableColumn<Item, Object> colGenerica = (TableColumn<Item, Object>) col;
                    colGenerica.setCellFactory(c -> new TableCell<Item, Object>() {
                        @Override
                        protected void updateItem(Object valor, boolean empty) {
                            super.updateItem(valor, empty);
                            if (empty || valor == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                try {
                                    double num = Double.parseDouble(valor.toString());
                                    setText(MOEDA.format(num));
                                    setAlignment(Pos.CENTER_RIGHT);
                                } catch (NumberFormatException e) {
                                    setText(valor.toString());
                                }
                            }
                        }
                    });
                    break;
                }
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
            cbFiltro.setItems(FXCollections.observableArrayList("Todos", "Nome", "ID", "Categoria"));
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
            boolean bateuCategoria = item.getCategoria() != null && item.getCategoria().toLowerCase().contains(termoFormatado);

            return switch (filtroSelecionado) {
                case "ID" -> bateuId;
                case "Nome" -> bateuNome;
                case "Categoria" -> bateuCategoria;
                default -> bateuId || bateuNome || bateuCategoria;
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
                    "Taxa Diária: " + MOEDA.format(item.getTaxaDiaria()) + "\n" +
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
        if (tabela == null || sistema == null) return;

        Item selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(AlertType.WARNING, "Seleção Necessária", "Selecione um item na tabela para atualizar.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Atualizar Item");
        dialog.setHeaderText("Editar dados do item: " + selecionado.getNome() + " (ID: " + selecionado.getId() + ")");

        ButtonType btnSalvar = new ButtonType("Salvar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField txtNome = new TextField(selecionado.getNome());
        TextField txtCategoria = new TextField(selecionado.getCategoria());
        TextField txtTaxa = new TextField(String.valueOf(selecionado.getTaxaDiaria()));
        TextField txtEstado = new TextField(selecionado.getEstadoConservacao());

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(txtNome, 1, 0);
        grid.add(new Label("Categoria:"), 0, 1);
        grid.add(txtCategoria, 1, 1);
        grid.add(new Label("Taxa Diária:"), 0, 2);
        grid.add(txtTaxa, 1, 2);
        grid.add(new Label("Estado:"), 0, 3);
        grid.add(txtEstado, 1, 3);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(txtNome::requestFocus);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == btnSalvar) {
            try {
                String novoNome = txtNome.getText().trim();
                String novaCat = txtCategoria.getText().trim();
                double novaTaxa = Double.parseDouble(txtTaxa.getText().trim());
                String novoEstado = txtEstado.getText().trim();

                boolean atualizou = false;
                try {
                    var m = sistema.getClass().getMethod("atualizarItem", int.class, String.class, String.class, double.class, String.class);
                    atualizou = (boolean) m.invoke(sistema, selecionado.getId(), novoNome, novaCat, novaTaxa, novoEstado);
                } catch (NoSuchMethodException e1) {
                    try {
                        var m = sistema.getClass().getMethod("atualizarItem", Item.class);
                        selecionado.setNome(novoNome);
                        selecionado.setCategoria(novaCat);
                        selecionado.setTaxaDiaria(novaTaxa);
                        selecionado.setEstadoConservacao(novoEstado);
                        atualizou = (boolean) m.invoke(sistema, selecionado);
                    } catch (Exception e2) {
                        atualizou = false;
                    }
                } catch (Exception e) {
                    atualizou = false;
                }

                if (atualizou) {
                    mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Item atualizado com sucesso!");
                    carregarTabela();
                } else {
                    mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível atualizar o item.");
                }
            } catch (NumberFormatException e) {
                mostrarAlerta(AlertType.ERROR, "Erro", "Valor inválido para a taxa diária.");
            }
        }
    }

    @FXML
    public void desativarItem(ActionEvent event) {
        if (tabela == null || sistema == null) return;

        Item selecionado = tabela.getSelectionModel().getSelectedItem();
        int idAlvo = -1;

        if (selecionado != null) {
            idAlvo = selecionado.getId();
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Desativar Item");
            dialog.setHeaderText("Desativar Item");
            dialog.setContentText("Digite o ID do item:");
            Optional<String> resultado = dialog.showAndWait();
            if (resultado.isPresent()) {
                try {
                    idAlvo = Integer.parseInt(resultado.get().trim());
                } catch (NumberFormatException e) {
                    mostrarAlerta(AlertType.ERROR, "Erro", "Digite um ID numérico válido.");
                    return;
                }
            } else {
                return;
            }
        }

        boolean sucesso = sistema.excluirItem(idAlvo);
        if (sucesso) {
            mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Item desativado com sucesso!");
            carregarTabela();
        } else {
            mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível desativar o item.");
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