package bancolafamilia;

import bancolafamilia.banco.*;
import bancolafamilia.gui.Interfaz;

import java.io.IOException;
import java.time.Duration;

/**
 * Punto de entrada del programa. Genera una simulación de tiempo, un banco con datos
 * iniciales, la interfaz de texto y una simulación de operaciones.
 */
public class App {

    public static void main(String[] args) throws IOException {
        
        // creamos el banco y la interfaz de texto
        Banco banco = new Banco();
        Interfaz interfaz = new Interfaz(banco);
        
       
        // creamos la simulación para el tiempo
        Duration tickInterval = Duration.ofMillis(100); // precisión de la simulación
        double timeMultiplier = 60*60; // 1 hora / seg

        TimeSimulation timeSim = new TimeSimulation(tickInterval, timeMultiplier);

        timeSim.setBank(banco);
        timeSim.setGui(interfaz);


        // configuramos el banco con algunos valores conocidos
        startUpConfig(banco);


        // empezamos la simulación del tiempo en otro hilo
        Thread simThread = new Thread(timeSim);
        simThread.start();
        

        // arrancamos el loop principal de la interfaz
        // el programa no pasa de aquí hasta que termine de ejecutarse
        interfaz.start();

        
        // finalización del programa
        // detenemos el hilo de la simulación del tiempo
        timeSim.stop();
        
        // al finalizar, esperamos a que muera el hilo de la simulación del tiempo
        try {
            simThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Da valores conocidos de fabrica al banco, para poder utilizar
     * mejor la aplicación
     * @param banco El banco a configurar
     */
    private static void startUpConfig(Banco banco) {

        banco.addReserves(500000000f);      //   500.000.000
        banco.addDeposits(2500000000f);     // 2.500.000.000
        banco.addLoanedAmount(3000000000f); // 3.000.000.000
        
        // gerentes
        banco.addUser(new Gerente("Vito Corleone ", 90124587, "admin0", "admin"));
        banco.addUser(new Gerente("Michael Corleone", 91254326, "admin1", "admin"));
        banco.addUser(new Gerente("Tony Soprano", 92254326, "admin2", "admin"));

        // agentes especiales
        AgenteEspecial agent = new AgenteEspecial("Tommy Angelo", 54231542, "agente0", "admin", banco);
        banco.addUser(agent);
        banco.addUser(new AgenteEspecial("Frank Costello", 45213546, "agente1", "admin", banco));
        banco.addUser(new AgenteEspecial("Vito Scaletta", 54781234, "agente2", "admin", banco));
        
        // asesores financieros
        banco.addUser(new AsesorFinanciero("Fredo Corleone", 85426574, "asesor0", "admin"));

        // agente de bolsa
        AgenteBolsa broker = new AgenteBolsa("Lucas Rodriguez", 93021542, 0.10f);
        banco.setBroker(broker);
        
        broker.newActivo(new Activo("EcoEnergy", 12000, 0.8f));
        broker.newActivo(new Activo("HealthPlus", 5000, 0.2f));
        broker.newActivo(new Activo("GreenTech", 30000, 0.5f));
        broker.newActivo(new Activo("DataNet", 4000, 0.6f));
        broker.newActivo(new Activo("Quantum Tech", 20000, 0.7f));

        // cajeros
        Cajero cajero = new Cajero("Jorge", 42587621, "", "");
        banco.addUser(cajero);
        banco.addUser(new Cajero("Pedro", 25215468, "", ""));
        banco.addUser(new Cajero("Maria", 54821356, "", ""));
        banco.addUser(new Cajero("Lucas", 12458753, "", ""));
        banco.addUser(new Cajero("Sofia", 23125458, "", ""));
        banco.addUser(new Cajero("Agustín", 12545875, "", ""));

        // usuarios
        Client cliente0 = new Client("Jorge Ortiz", 12542785, "user0", "1234");
        Client cliente1 = new Client("Armando Perez", 54231564, "user1", "1234");
        Client cliente2 = new Client("Martín Gimenez", 54865212, "user2", "1234");
        Client cliente3 = new Client("Pedro García", 12548652, "user3", "1234");
        Client cliente4 = new Client("Carlos Avila", 12548265, "user4", "1234");
        Client cliente5 = new Client("Sofia Nuñez", 20152458, "user5", "1234");
        Client cliente6 = new Client("Milagros Perez", 20659854, "user6", "1234");
        Client cliente7 = new Client("Juan Aguero", 42563857, "user7", "1234");
        Client cliente8 = new Client("Sol Perez", 54265870, "user8", "1234");
        Client cliente9 = new Client("Agustina Solares", 12542157, "user9", "1234");

        banco.addUser(cliente0);
        banco.addUser(cliente1);
        banco.addUser(cliente2);
        banco.addUser(cliente3);
        banco.addUser(cliente4);
        banco.addUser(cliente5);
        banco.addUser(cliente6);
        banco.addUser(cliente7);
        banco.addUser(cliente8);
        banco.addUser(cliente9);

        cliente0.setAlias("alias.user.cero");
        cliente1.setAlias("alias.user.uno");
        cliente2.setAlias("alias.user.dos");
        cliente3.setAlias("alias.user.tres");
        cliente4.setAlias("alias.user.cuatro");
        cliente5.setAlias("alias.user.cinco");
        cliente6.setAlias("alias.user.seis");
        cliente7.setAlias("alias.user.siete");
        cliente8.setAlias("alias.user.ocho");
        cliente9.setAlias("alias.user.nueve");

        // Operaciones iniciales

        // hacemos algunos depósitos
        banco.solicitudDeposito(cliente0, 421000, cajero);
        banco.solicitudDeposito(cliente1, 12452, cajero);
        banco.solicitudDeposito(cliente2, 4587, cajero);
        banco.solicitudDeposito(cliente3, 1245201, cajero);
        banco.solicitudDeposito(cliente4, 12405, cajero);
        banco.solicitudDeposito(cliente5, 45872, cajero);
        banco.solicitudDeposito(cliente6, 121452, cajero);
        banco.solicitudDeposito(cliente7, 124587, cajero);
        banco.solicitudDeposito(cliente8, 954521, cajero);
        banco.solicitudDeposito(cliente9, 1245328, cajero);

        // Creamos algunos clientes premium (es raro en el código porque la idea es hacerlo por la interfaz)
        Client c = Client.createTempSpecialClient();
        
        // Cliente esperando a ser aceptado como premium (jorge = user0)
        Transferencia t0 = new TransferenciaEspecial(TimeSimulation.getTime(), cliente0, c, 10, "honorarios");
        banco.procesarOperacion(t0);
        
        // Un cliente premium aun sin ser atendido por un agente especial (armando = user1)
        Transferencia t1 = new TransferenciaEspecial(TimeSimulation.getTime(), cliente1, c, 10, "honorarios");
        banco.procesarOperacion(t1);
        banco.aprobarOperacionPendiente(t1);
        
        // Cliente premium aceptado por un agente especial (martin = user2)
        Transferencia t2 = new TransferenciaEspecial(TimeSimulation.getTime(), cliente2, c, 10, "honorarios");
        banco.procesarOperacion(t2);
        banco.aprobarOperacionPendiente(t2);
        agent.acceptClient(cliente2, banco.getPendingPremiumClients(), banco.getCajeros());
    }
}
