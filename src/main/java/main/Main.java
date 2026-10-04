package main;
import java.util.Scanner;

import apresentacao.MenuLogin;
import apresentacao.fx.App;
import business.cruds.GerenciamentoCategoria;
import business.cruds.GerenciamentoContratos;
import business.cruds.GerenciamentoItens;
import business.cruds.GerenciamentoMultas;
import business.cruds.GerenciamentoUsuarios;
import business.cruds.GerenciamentoFornecedor;
import business.interfaces.IGerenciamentoCategoria;
import business.interfaces.IGerenciamentoContratos;
import business.interfaces.IGerenciamentoFornecedor;
import business.interfaces.IGerenciamentoItens;
import business.interfaces.IGerenciamentoMultas;
import business.interfaces.IGerenciamentoUsuarios;
import business.relatorios.GeradorRelatorios;
import business.relatorios.IRelatorios;
import entidades.Administrador;
import facade.SistemaFacade;
import repositories.CategoriaRepositorio;
import repositories.ContratoRepositorio;
import repositories.FornecedorRepositorio;
import repositories.ItemRepositorio;
import repositories.MultaRepositorio;
import repositories.UsuarioRepositorio;

@SuppressWarnings("java:S106")
public class Main {

    public static void main(String[] args) {
    	App.main(args);
        
    }
}