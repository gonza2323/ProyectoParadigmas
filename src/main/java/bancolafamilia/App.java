package bancolafamilia;

import java.io.IOException;

import bancolafamilia.banco.Banco;
import bancolafamilia.gui.Interfaz;

public class App {
    public static void main( String[] args ) throws IOException {
        Banco banco = new Banco();
        Interfaz interfaz = new Interfaz(banco);
        interfaz.start();
    }
}
