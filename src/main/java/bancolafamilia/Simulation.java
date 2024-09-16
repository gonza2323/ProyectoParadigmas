package bancolafamilia;

import java.util.*;
import java.time.*;

import bancolafamilia.banco.*;

/**
 * Simula operaciones en el banco. Genera 
 */
public class Simulation {

    Banco banco;
    TimeSimulation timeSim;
    private PriorityQueue<Operacion> plannedOperations = new PriorityQueue<>();

    public Simulation(Banco banco, TimeSimulation timeSim) {
        this.banco = banco;
    }

    /**
     * Genera una secuencia aleatoria de operaciones
     * De ser necesario, agregará clientes al banco
     * @param operationsAmount
     */
    public void createSimulation(int amountOfOperations, int days) {
        
    }

    public void startSimulation() {
        
    }
}

abstract class Event implements Comparable<Event> {
    private final LocalDateTime date;

    public Event(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public int compareTo(Event other) {
        return this.date.compareTo(other.date);
    }

    public LocalDateTime getDate() { return date; }

    abstract void process();
}