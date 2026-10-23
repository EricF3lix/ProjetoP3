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
import org.kordamp.ikonli.javafx.FontIcon;

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

    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    @Override
    public String getTitulo() {
        return "Gerenciar Itens";
    }

    @FXML
    public void initialize() {
        configurarResponsividade();
        configurarFiltroPesquisa();
        configurarColunaTaxaDiaria();
        configurarColunaAtivoEStatus();
        configurarTabelaVazia();
        removerRedundanciaBusca();
    }

    @Override
    protected void aoExibir() {
        carregarTabela();
    }

    @SuppressWarnings("deprecation")
    private void configurarResponsividade() {
        if (tabela != null) {
            VBox.setVgrow(tabela, Priority.ALWAYS);
            tabela.setMaxWidth(Double.MAX_VALUE);
            tabela.setMaxHeight(Double.MAX_VALUE);
            tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }
    }

    /**
     * Empty State elegante estilo macOS caso a busca não retorne nenhum item.
     */
    private void configurarTabelaVazia() {
        if (tabela == null) return;

        VBox estadoVazio = new VBox(8);
        estadoVazio.setAlignment(Pos.CENTER);
        estadoVazio.setStyle("-fx-padding: 30px;");

        FontIcon icone = new FontIcon("fas-boxes");
        icone.setIconSize(32);
        icone.setStyle("-fx-icon-color: #8e8e93;");

        Label titulo = new Label("Nenhum item encontrado");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1c1c1e;");

        Label subtitulo = new Label("Tente ajustar o termo pesquisado ou verifique os filtros.");
        subtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #8e8e93;");

        estadoVazio.getChildren().addAll(icone, titulo, subtitulo);
        tabela.setPlaceholder(estadoVazio);
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
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #1c1c1e;");
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
                                    setStyle("-fx-font-weight: bold; -fx-text-fill: #1c1c1e;");
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

    /**
     * Badges coloridas de alta visibilidade para status e disponibilidade.
     */
    private void configurarColunaAtivoEStatus() {
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
                        Item item = getTableRow() != null ? getTableRow().getItem() : null;
                        String statusTexto = (item != null && item.getStatus() != null) 
                                ? item.getStatus().toString().toLowerCase() 
                                : "";

                        if (!ativo) {
                            badge.setText("● Indisponível");
                            badge.setStyle(
                                "-fx-background-color: #fee2e2; " +
                                "-fx-text-fill: #991b1b; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 3 8 3 8; " +
                                "-fx-background-radius: 10; " +
                                "-fx-font-size: 11px;"
                            );
                        } else if (statusTexto.contains("alugado") || statusTexto.contains("locado")) {
                            badge.setText("● Alugado");
                            badge.setStyle(
                                "-fx-background-color: #dbeafe; " +
                                "-fx-text-fill: #1e40af; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 3 8 3 8; " +
                                "-fx-background-radius: 10; " +
                                "-fx-font-size: 11px;"
                            );
                        } else if (statusTexto.contains("manuten") || statusTexto.contains("reparo")) {
                            badge.setText("● Manutenção");
                            badge.setStyle(
                                "-fx-background-color: #fef3c7; " +
                                "-fx-text-fill: #92400e; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 3 8 3 8; " +
                                "-fx-background-radius: 10; " +
                                "-fx-font-size: 11px;"
                            );
                        } else {
                            badge.setText("● Disponível");
                            badge.setStyle(
                                "-fx-background-color: #d1fae5; " +
                                "-fx-text-fill: #065f46; " +
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
            
            String catTexto = item.getCategoria() != null ? String.valueOf(item.getCategoria()).toLowerCase() : "";
            boolean bateuCategoria = catTexto.contains(termoFormatado);

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
                notificarPopUp("Item '" + item.getNome() + "' localizado com sucesso!", true);
            } else {
                animarErroShake(pesquisa);
                notificarPopUp("Nenhum item encontrado com o ID " + id, false);
            }
        } catch (NumberFormatException e) {
            animarErroShake(pesquisa);
            notificarPopUp("Por favor, informe um ID numérico válido.", false);
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
            animarErroShake(tabela);
            notificarPopUp("Selecione um item na tabela para editar.", false);
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Atualizar Item");
        dialog.setHeaderText("Editar dados do item: " + selecionado.getNome() + " (ID: " + selecionado.getId() + ")");

        ButtonType btnSalvar = new ButtonType("Salvar Alterações", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtNome = new TextField(selecionado.getNome());
        String nomeCat = selecionado.getCategoria() != null ? String.valueOf(selecionado.getCategoria()) : "";
        TextField txtCategoria = new TextField(nomeCat);
        txtCategoria.setDisable(true);
        TextField txtTaxa = new TextField(String.valueOf(selecionado.getTaxaDiaria()));
        TextField txtEstado = new TextField(selecionado.getEstadoConservacao());

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(txtNome, 1, 0);
        grid.add(new Label("Categoria:"), 0, 1);
        grid.add(txtCategoria, 1, 1);
        grid.add(new Label("Taxa Diária (R$):"), 0, 2);
        grid.add(txtTaxa, 1, 2);
        grid.add(new Label("Conservação:"), 0, 3);
        grid.add(txtEstado, 1, 3);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(txtNome::requestFocus);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == btnSalvar) {
            try {
                String novoNome = txtNome.getText().trim();
                double novaTaxa = Double.parseDouble(txtTaxa.getText().trim());
                String novoEstado = txtEstado.getText().trim();

                boolean atualizou = false;
                try {
                    var m = sistema.getClass().getMethod("atualizarItem", Item.class);
                    selecionado.setNome(novoNome);
                    selecionado.setTaxaDiaria(novaTaxa);
                    selecionado.setEstadoConservacao(novoEstado);
                    atualizou = (boolean) m.invoke(sistema, selecionado);
                } catch (NoSuchMethodException e1) {
                    try {
                        var m = sistema.getClass().getMethod("atualizarItem", int.class, String.class, double.class, String.class);
                        atualizou = (boolean) m.invoke(sistema, selecionado.getId(), novoNome, novaTaxa, novoEstado);
                    } catch (Exception e2) {
                        atualizou = false;
                    }
                } catch (Exception e) {
                    atualizou = false;
                }

                if (atualizou) {
                    notificarPopUp("Item atualizado com sucesso!", true);
                    carregarTabela();
                } else {
                    notificarPopUp("Não foi possível atualizar o item.", false);
                }
            } catch (NumberFormatException e) {
                notificarPopUp("Valor inválido para a taxa diária.", false);
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
            dialog.setHeaderText("Desativar Item do Catálogo");
            dialog.setContentText("Digite o ID do item:");
            Optional<String> resultado = dialog.showAndWait();
            if (resultado.isPresent()) {
                try {
                    idAlvo = Integer.parseInt(resultado.get().trim());
                } catch (NumberFormatException e) {
                    animarErroShake(tabela);
                    notificarPopUp("Digite um ID numérico válido.", false);
                    return;
                }
            } else {
                return;
            }
        }

        boolean sucesso = sistema.excluirItem(idAlvo);
        if (sucesso) {
            notificarPopUp("Item desativado com sucesso!", true);
            carregarTabela();
        } else {
            animarErroShake(tabela);
            notificarPopUp("Não foi possível desativar o item selecionado.", false);
        }
    }
}