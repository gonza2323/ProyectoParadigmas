package bancolafamilia;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.time.*;

import bancolafamilia.banco.*;

/**
 * Simula operaciones en el banco. Las programa en el banco para
 * que se ejecuten automáticamente. Siempre genera un solo evento
 * de lavado de dinero.
 */
public class Simulation {
    
    private final int avgDailyOperations = 30;
    private final int offsetRange = 5;

    private final float transferProbability = 0.5f;
    private final float withdrawalProbability = 0.2f;
    private final float depositProbability = 0.2f;
    private final float loanProbability = 0.1f;
    private final float totalProbability;
    
    private final Random random = new Random();
    
    private final Banco banco;
    private final List<Client> clients;
    private final List<Cajero> cajeros;

    public Simulation(Banco banco) {
        this.banco = banco;
        this.clients = banco.getClients();
        this.cajeros = banco.getCajeros();
        this.totalProbability = transferProbability
                                + depositProbability
                                + withdrawalProbability
                                + loanProbability;
    }

    /**
     * Genera una secuencia aleatoria de operaciones
     * @param operationsAmount
     */
    public void performSimulation(int amountOfOperations) {
        LocalDateTime date = TimeSimulation.getTime().plusHours(1);
        
        List<Operacion> operations = new LinkedList<>();
        int remainingOperations = amountOfOperations - 1;

        moneyLaunderingOperation();
        
        while (remainingOperations > avgDailyOperations + offsetRange) {
            int offset = - offsetRange + 2 * random.nextInt(offsetRange);
            int dailyOperations = avgDailyOperations + offset;
            operations.addAll(generateDayOperations(date, dailyOperations));
            remainingOperations -= dailyOperations;
            date = date.plusDays(1);
        }
        
        operations.addAll(generateDayOperations(date, remainingOperations));
        
        banco.programarOperaciones(operations);
    }

    private List<Operacion> generateDayOperations(LocalDateTime date, int amountOfOperations) {
        List<Operacion> operations = new LinkedList<>();
        for (int i = 0; i < amountOfOperations; i++) {
            date = date.plusMinutes(random.nextInt(60 * 24));
            operations.add(generateRandomOperation(date));
        }

        return operations;
    }

    private Operacion generateRandomOperation(LocalDateTime date) {
        float operation = (float)Math.random();
        float accumulatedProb = transferProbability / totalProbability;

        if (operation < accumulatedProb)
            return generateRandomTransfer(date);

        accumulatedProb += depositProbability / totalProbability;
        if (operation < accumulatedProb)
            return generateRandomDeposit(date);
        
        accumulatedProb += withdrawalProbability / totalProbability;
        if (operation < accumulatedProb)
            return generateRandomWithdrawal(date);

        return generateRandomLoan(date);
    }

    private Operacion generateRandomLoan(LocalDateTime date) {
        Client client = clients.get(random.nextInt(clients.size()));
        float maxLoan = banco.getMaxClientLoan(client);
        return new Prestamo(date, client, maxLoan - 0.9f * (float)Math.random(), banco.getAnualInterestRate(), random.nextInt(12) + 1);
    }

    private Operacion generateRandomWithdrawal(LocalDateTime date) {
        Client client = clients.get(random.nextInt(clients.size()));
        Cajero cajero = cajeros.get(random.nextInt(cajeros.size()));
        float maxAmount = Math.max(client.getBalance(), Retiro.montoMax);
        return new Retiro(date, client, maxAmount * (float)Math.random(), cajero);
    }

    private Operacion generateRandomTransfer(LocalDateTime date) {
        Client client1 = clients.get(random.nextInt(clients.size()));
        Client client2 = clients.get(random.nextInt(clients.size()));
        float maxAmount = Math.max(Transferencia.maxAmountImmediate, client1.getBalance());
        return new Transferencia(date, client1, client2, maxAmount * (float)Math.random(), "simulación");
    }

    private Operacion generateRandomDeposit(LocalDateTime date) {
        Client client = clients.get(random.nextInt(clients.size()));
        Cajero cajero = cajeros.get(random.nextInt(cajeros.size()));
        return new Deposito(date, client, Deposito.montoMax * (float)Math.random(), cajero);
    }

    private void moneyLaunderingOperation() {
        List<Cajero> cajerosEspeciales = cajeros.stream()
                                .filter(c -> !c.getDocuments().isEmpty())
                                .collect(Collectors.toList());
        
        if (cajerosEspeciales.isEmpty())
            return;
        
        Cajero cajero = cajerosEspeciales.get(random.nextInt(cajerosEspeciales.size()));
        DocumentoClienteEspecial docu = cajero.getDocuments().get(random.nextInt(cajero.getDocuments().size()));
        Client cliente = docu.getClient();
        
        float amount = Deposito.montoMax + (AgenteEspecial.montoMaxOpEspecial - Deposito.montoMax) * (float)Math.random();
        banco.solicitudDeposito(cliente, amount, cajero);
        
        AgenteEspecial agente = docu.getAgenteEspecial();
        DocumentoClienteEspecial tarea = agente.getTareasPendientes().stream().filter(d -> d.getClient() == docu.getClient()).findFirst().get();
        agente.executeTask(tarea);
    }
}
