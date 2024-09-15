package bancolafamilia;

import bancolafamilia.banco.*;
import bancolafamilia.gui.Interfaz;

import java.time.Duration;
import java.util.Scanner;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        Banco banco = new Banco();
        Interfaz interfaz = new Interfaz(banco);

        Duration tickInterval = Duration.ofSeconds(1);
        double timeMultiplier = 24*60*60; // Simular 1 día / s

        Simulation timeSim = new Simulation(banco, interfaz, tickInterval, timeMultiplier);
        Thread simThread = new Thread(timeSim);
        simThread.start();

        Client cliente1 = new Client("Martin", 1234, "martin", "1234");
        Client cliente2 = new Client("Jorge", 1235, "jorge", "1234");
        Client cliente3 = new Client("Pedro", 1236, "pedro", "1234");
        Client cliente4 = new Client("Carlos", 1238, "carlos", "1234");
        Client cliente5 = new Client("Armando", 54213856, "armando", "1234");

        banco.addUser(cliente1);
        banco.addUser(cliente2);
        banco.addUser(cliente3);
        banco.addUser(cliente4);
        banco.addUser(cliente5);

        cliente1.setAlias("hola.como.estas");
        cliente2.setAlias("muy.bien.tu");
        cliente3.setAlias("muchas.gracias.chau");
        cliente4.setAlias("la.cosa.nostra"); // alias asistente ejecutivo
        cliente5.setAlias("que.es.eso");

        
        Cajero cajero1 = new Cajero("jorge", 1239, "caja1", "1234");
        Cajero cajero2 = new Cajero("jose", 1240, "caja2", "1234");
        banco.addUser(cajero1);
        banco.addUser(cajero2);

        banco.solicitudDeposito(cliente1, 70000, cajero1);
        banco.solicitudDeposito(cliente2, 2000, cajero1);
        banco.solicitudDeposito(cliente3, 500000, cajero1);
        banco.solicitudDeposito(cliente5, 400000, cajero1);
        
        // verificar que los datos del cliente y del agente especial coincidan
        AgenteEspecial asistente = new AgenteEspecial("armando", 54213856, "especial", "hunter3", cliente5);
        banco.addUser(asistente);
        banco.addUser(new Gerente("admin", 1237, "admin", "hunter2", asistente));

        for (int i = 0; i < 16; i++) {
            banco.solicitudTransferencia(cliente3, cliente2, 1204037.99f, "prueba\nprueba");
        }

        interfaz.start();
        timeSim.stop();

        try {
            simThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
