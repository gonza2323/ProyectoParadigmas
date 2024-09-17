package bancolafamilia;

import java.util.*;
import java.time.*;

import bancolafamilia.banco.*;

/**
 * Simula operaciones en el banco. Genera 
 */
public class Simulation {

    Banco banco;
    private PriorityQueue<Operacion> plannedOperations = new PriorityQueue<>();

    public Simulation(Banco banco) {
        this.banco = banco;
    }

    /**
     * Genera una secuencia aleatoria de operaciones
     * @param operationsAmount
     */
    public void createSimulation(int amountOfOperations, int days) {
        banco.programarOperaciones(null);
    }
}

abstract class Event implements Comparable<Event>, Runnable {
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
