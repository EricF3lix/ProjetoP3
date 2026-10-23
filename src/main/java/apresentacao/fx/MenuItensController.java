package apresentacao.fx;

import java.util.List;
import java.util.Optional;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
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
    }

    @Override
    protected void aoExibir() {

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
            Optional<Integer> idOpt = pedirInteiro("Digite o ID do item:");
            if (idOpt.isEmpty()) return;
            texto = String.valueOf(idOpt.get());
        }

        try {
            int id = Integer.parseInt(texto);
            Item item = sistema != null ? sistema.buscarItem(id) : null;
            
            if (item != null) {
                if (cbFiltro != null) cbFiltro.setValue("ID");
                if (pesquisa != null) pesquisa.setText(String.valueOf(id));
            } else {
                erro("Nenhum item encontrado com o ID " + id);
            }
        } catch (NumberFormatException e) {
            erro("Por favor, informe um ID numérico válido.");
        }
    }
    @FXML
    public void cadastrarItem(ActionEvent event) {
        Optional<String> nome = pedirTexto("Nome do item:");
        if (nome.isEmpty()) return;

        Optional<String> desc = pedirTexto("Descrição:");
        if (desc.isEmpty()) return;

        Optional<String> taxaStr = pedirTexto("Taxa diária:");
        if (taxaStr.isEmpty()) return;

        Optional<String> estado = pedirTexto("Estado de conservação:");
        if (estado.isEmpty()) return;

        Optional<String> valorRepStr = pedirTexto("Valor de reposição:");
        if (valorRepStr.isEmpty()) return;

       
        StringBuilder sbCats = new StringBuilder();
        for (Categoria c : sistema.listarCategorias()) {
            if (sbCats.length() > 0) sbCats.append(", ");
            sbCats.append(c.getId()).append("-").append(c.getNome());
        }
        String listaCats = sbCats.length() > 0 ? sbCats.toString() : "Nenhuma";

        Optional<String> catInput = pedirTexto("Categoria (Opções: " + listaCats + ")\n[Deixe em branco para sem categoria]:");
        if (catInput.isEmpty()) return;

        Categoria categoria = null;
        if (!catInput.get().isBlank()) {
            String termo = catInput.get().trim();
            for (Categoria c : sistema.listarCategorias()) {
                if (String.valueOf(c.getId()).equals(termo) || (c.getNome() != null && c.getNome().equalsIgnoreCase(termo))) {
                    categoria = c;
                    break;
                }
            }

            if (categoria == null) {
                erro("Categoria '" + termo + "' não encontrada. Cadastre-a primeiro ou deixe em branco.");
                return;
            }
        }

        StringBuilder sbForns = new StringBuilder();
        for (Fornecedor f : sistema.listarFornecedores()) {
            if (sbForns.length() > 0) sbForns.append(", ");
            sbForns.append(f.getId()).append("-").append(f.getRazaoSocial());
        }
        String listaForns = sbForns.length() > 0 ? sbForns.toString() : "Nenhum";

        Optional<String> fornInput = pedirTexto("Fornecedor (Opções: " + listaForns + ")\n[Deixe em branco para sem fornecedor]:");
        if (fornInput.isEmpty()) return;

        Fornecedor fornecedor = null;
        if (!fornInput.get().isBlank()) {
            String termo = fornInput.get().trim();
            for (Fornecedor f : sistema.listarFornecedores()) {
                if (String.valueOf(f.getId()).equals(termo) || (f.getRazaoSocial() != null && f.getRazaoSocial().equalsIgnoreCase(termo))) {
                    fornecedor = f;
                    break;
                }
            }

            if (fornecedor == null) {
                erro("Fornecedor '" + termo + "' não encontrado. Cadastre-o primeiro ou deixe em branco.");
                return;
            }
        }

        try {
            double taxa = Double.parseDouble(taxaStr.get().replace(",", ".").trim());
            double valorRep = Double.parseDouble(valorRepStr.get().replace(",", ".").trim());
            int id = sistema.gerarProximoIdItem();

            Item novoItem = new Item(
                    id,
                    nome.get().trim(),
                    desc.get().trim(),
                    taxa,
                    estado.get().trim(),
                    valorRep,
                    categoria,
                    fornecedor
            );

            if (sistema.cadastrarItem(novoItem)) {
                info("Item cadastrado com sucesso! ID: " + id);
                carregarTabela();
            } else {
                erro("Erro ao cadastrar o item.");
            }
        } catch (NumberFormatException e) {
            erro("Valores numéricos inválidos para taxa diária ou valor de reposição.");
        }
    }

    @FXML
    public void atualizarItem(ActionEvent event) {
        Item atual = tabela != null ? tabela.getSelectionModel().getSelectedItem() : null;

        if (atual == null) {
            Optional<Integer> idOpt = pedirInteiro("Digite o ID do item:");
            if (idOpt.isEmpty()) return;
            atual = sistema != null ? sistema.buscarItem(idOpt.get()) : null;
        }

        if (atual == null) {
            erro("Item não encontrado.");
            return;
        }

        String catAtualNome = atual.getCategoria() != null ? atual.getCategoria().getNome() : "Sem Categoria";
        String fornAtualNome = atual.getFornecedor() != null ? atual.getFornecedor().getRazaoSocial() : "Sem Fornecedor";

        Optional<String> nome = pedirTexto("Novo nome (atual: " + atual.getNome() + "):");
        if (nome.isEmpty()) return;

        StringBuilder sbCats = new StringBuilder();
        for (Categoria c : sistema.listarCategorias()) {
            if (sbCats.length() > 0) sbCats.append(", ");
            sbCats.append(c.getId()).append("-").append(c.getNome());
        }
        String listaCats = sbCats.length() > 0 ? sbCats.toString() : "Nenhuma";

        Optional<String> catInput = pedirTexto("Categoria (Atual: " + catAtualNome + " | Opções: " + listaCats + ")\n[Enter mantém | Digite 'nenhum' para remover]:");
        if (catInput.isEmpty()) return;

        Categoria novaCategoria = atual.getCategoria();
        if (!catInput.get().isBlank()) {
            String termo = catInput.get().trim();
            if (termo.equalsIgnoreCase("nenhum")) {
                novaCategoria = null;
            } else {
                Categoria encontrada = null;
                for (Categoria c : sistema.listarCategorias()) {
                    if (String.valueOf(c.getId()).equals(termo) || (c.getNome() != null && c.getNome().equalsIgnoreCase(termo))) {
                        encontrada = c;
                        break;
                    }
                }

                if (encontrada == null) {
                    erro("Categoria '" + termo + "' não encontrada.");
                    return;
                }
                novaCategoria = encontrada;
            }
        }

        StringBuilder sbForns = new StringBuilder();
        for (Fornecedor f : sistema.listarFornecedores()) {
            if (sbForns.length() > 0) sbForns.append(", ");
            sbForns.append(f.getId()).append("-").append(f.getRazaoSocial());
        }
        String listaForns = sbForns.length() > 0 ? sbForns.toString() : "Nenhum";

        Optional<String> fornInput = pedirTexto("Fornecedor (Atual: " + fornAtualNome + " | Opções: " + listaForns + ")\n[Enter mantém | Digite 'nenhum' para remover]:");
        if (fornInput.isEmpty()) return;

        Fornecedor novoFornecedor = atual.getFornecedor();
        if (!fornInput.get().isBlank()) {
            String termo = fornInput.get().trim();
            if (termo.equalsIgnoreCase("nenhum")) {
                novoFornecedor = null;
            } else {
                Fornecedor encontrado = null;
                for (Fornecedor f : sistema.listarFornecedores()) {
                    if (String.valueOf(f.getId()).equals(termo) || (f.getRazaoSocial() != null && f.getRazaoSocial().equalsIgnoreCase(termo))) {
                        encontrado = f;
                        break;
                    }
                }

                if (encontrado == null) {
                    erro("Fornecedor '" + termo + "' não encontrado.");
                    return;
                }
                novoFornecedor = encontrado;
            }
        }

        Optional<String> taxaStr = pedirTexto("Nova taxa diária (atual: " + atual.getTaxaDiaria() + "):");
        if (taxaStr.isEmpty()) return;

        Optional<String> estado = pedirTexto("Novo estado (atual: " + atual.getEstadoConservacao() + "):");
        if (estado.isEmpty()) return;

        try {
            String novoNome = nome.get().isBlank() ? atual.getNome() : nome.get().trim();
            double novaTaxa = taxaStr.get().isBlank() ? atual.getTaxaDiaria() : Double.parseDouble(taxaStr.get().replace(",", ".").trim());
            String novoEstado = estado.get().isBlank() ? atual.getEstadoConservacao() : estado.get().trim();

            Item itemAtualizado = new Item(
                    atual.getId(),
                    novoNome,
                    atual.getDescricao(),
                    novaTaxa,
                    novoEstado,
                    atual.getValorReposicao(),
                    novaCategoria,
                    novoFornecedor
            );

            itemAtualizado.setAtivo(atual.isAtivo());
            if (!atual.isAtivo()) {
                itemAtualizado.setStatus("INDISPONIVEL");
            } else {
                itemAtualizado.setStatus(atual.getStatus());
            }

            if (sistema != null && sistema.atualizarItem(itemAtualizado)) {
                info("Item atualizado com sucesso!");
                carregarTabela();
            } else {
                erro("Erro ao atualizar o item.");
            }
        } catch (NumberFormatException e) {
            erro("Valor da taxa diária inválido.");
        }
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