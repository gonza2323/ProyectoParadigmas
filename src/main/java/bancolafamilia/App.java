package bancolafamilia;

import java.io.IOException;

import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Cliente;
import bancolafamilia.banco.Gerente;
import bancolafamilia.gui.Interfaz;

public class App {
    public static void main( String[] args ) throws IOException {
        Banco banco = new Banco();

        banco.addUser(new Cliente("Pedro", 1234, "pedro", "1234"));
        banco.addUser(new Gerente("admin", 1234, "admin", "hunter2"));

        Interfaz interfaz = new Interfaz(banco);
        interfaz.start();
    }
}
