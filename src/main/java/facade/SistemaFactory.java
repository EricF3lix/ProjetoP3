package facade;

import business.cruds.*;
import business.interfaces.*;
import business.relatorios.GeradorRelatorios;
import business.relatorios.IRelatorios;
import entidades.Administrador;
import repositories.*;


public final class SistemaFactory {

    private static final String EMAIL_ADMIN_PADRAO = "jacksonmepassaporfavor@loja.com";

    private SistemaFactory() {
    }

    public static SistemaFacade criar() {

        // REPOSITÓRIOS
        UsuarioRepositorio    usuarioRepositorio    = new UsuarioRepositorio();
        CategoriaRepositorio  categoriaRepositorio  = new CategoriaRepositorio();
        FornecedorRepositorio fornecedorRepositorio = new FornecedorRepositorio();
        ItemRepositorio       itemRepositorio       = new ItemRepositorio(categoriaRepositorio, fornecedorRepositorio);
        ContratoRepositorio   contratoRepositorio   = new ContratoRepositorio(usuarioRepositorio, itemRepositorio);
        MultaRepositorio      multaRepositorio      = new MultaRepositorio();

        // CAMADA DE NEGÓCIO
        IGerenciamentoContratos  gerenciamentoContratos  = new GerenciamentoContratos(contratoRepositorio);
        IGerenciamentoUsuarios   gerenciamentoUsuarios   = new GerenciamentoUsuarios(usuarioRepositorio, gerenciamentoContratos);
        IGerenciamentoCategoria  gerenciamentoCategoria  = new GerenciamentoCategoria(categoriaRepositorio);
        IGerenciamentoFornecedor gerenciamentoFornecedor = new GerenciamentoFornecedor(fornecedorRepositorio);
        IGerenciamentoItens      gerenciamentoItens      = new GerenciamentoItens(itemRepositorio);
        IGerenciamentoMultas     gerenciamentoMultas     = new GerenciamentoMultas(multaRepositorio);

        IRelatorios geradorRelatorios = new GeradorRelatorios(gerenciamentoItens, gerenciamentoContratos, gerenciamentoUsuarios);

        // FACADE
        SistemaFacade sistema = new SistemaFacade(
                gerenciamentoUsuarios, gerenciamentoContratos, gerenciamentoItens,
                gerenciamentoCategoria, gerenciamentoFornecedor, gerenciamentoMultas,
                geradorRelatorios);

        criarAdminPadrao(sistema);
        return sistema;
    }

    public static void criarAdminPadrao(SistemaFacade sistema) {
        if (sistema.buscarUsuarioPorEmail(EMAIL_ADMIN_PADRAO) == null) {
            Administrador admin = new Administrador(
                    sistema.gerarProximoIdUsuario(),
                    "Jackson-Raniel",
                    EMAIL_ADMIN_PADRAO,
                    "000.000.000-00",
                    "obrigadojackson");
            sistema.cadastrarUsuario(admin);
        }
    }
}