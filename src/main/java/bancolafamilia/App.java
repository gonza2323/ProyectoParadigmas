package bancolafamilia;

import bancolafamilia.banco.*;
import bancolafamilia.gui.Interfaz;

import java.io.IOException;

public class App {
    public static void main( String[] args ) throws IOException {
        Banco banco = new Banco();

        Cliente cliente1 = new Cliente("Martin", 1234, "martin", "1234");
        Cliente cliente2 = new Cliente("Jorge", 1235, "jorge", "1234");
        Cliente cliente3 = new Cliente("Pedro", 1236, "pedro", "1234");
        Cliente cliente4 = new Cliente("Carlos", 1238, "carlos", "1234");
        Cliente cliente5 = new Cliente("Armando", 54213856, "armando", "1234");

        banco.addUser(cliente1);
        banco.addUser(cliente2);
        banco.addUser(cliente3);
        banco.addUser(cliente4);

        cliente1.setAlias("hola.como.estas");
        cliente2.setAlias("muy.bien.tu");
        cliente3.setAlias("muchas.gracias.chau");
        cliente4.setAlias("mapa.fiar.oro"); //alias asistente ejecutivo
        cliente5.setAlias("que.es.eso");




//        banco.depositFunds(cliente1, 10000);
//        banco.depositFunds(cliente2, 2000);
//        banco.depositFunds(cliente3, 50000000);
//        banco.depositFunds(cliente5, 30000000);

        AgenteEspecial asistente = new AgenteEspecial("carlos", 1238, "especial", "hunter3");
        banco.addUser(asistente);
        banco.addUser(new Gerente("admin", 1237, "admin", "hunter2",asistente));

        Interfaz interfaz = new Interfaz(banco);
        interfaz.start();
    }
}
