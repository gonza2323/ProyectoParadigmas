package bancolafamilia;

import java.io.IOException;

import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Cliente;
import bancolafamilia.banco.Gerente;
import bancolafamilia.gui.Interfaz;

public class App {
    public static void main( String[] args ) throws IOException {
        Banco banco = new Banco();

        Cliente cliente1 = new Cliente("Martin", 1234, "martin", "1234");
        Cliente cliente2 = new Cliente("Jorge", 1235, "jorge", "1234");
        Cliente cliente3 = new Cliente("Pedro", 1236, "pedro", "1234");

        banco.addUser(cliente1);
        banco.addUser(cliente2);
        banco.addUser(cliente3);

        cliente1.setAlias("hola.como.estas");
        cliente2.setAlias("muy.bien.tu");
        cliente3.setAlias("muchas.gracias.chau");

        banco.depositFunds(cliente1, 10000);
        banco.depositFunds(cliente2, 2000);
        banco.depositFunds(cliente3, 5000);

        banco.addUser(new Gerente("admin", 1237, "admin", "hunter2"));

        Interfaz interfaz = new Interfaz(banco);
        interfaz.start();
    }
}
