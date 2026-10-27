package main;


import apresentacao.fx.App;


@SuppressWarnings("java:S106")
public class Main {

    public static void main(String[] args) {
        java.util.ServiceLoader.load(org.kordamp.ikonli.IkonHandler.class)
                .forEach(h -> System.out.println("Handler: " + h.getClass().getName()));
    	App.main(args);
        
    }
}