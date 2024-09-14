package bancolafamilia;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

import bancolafamilia.banco.Banco;
import bancolafamilia.gui.Interfaz;

/**
 * Simula el avance del tiempo y actualiza el estado del banco y,
 * de ser necesario, el de la interfaz gráfica
 */
public class Simulation implements Runnable {

    private final Duration tickInterval;
    private float timeMultiplier;
    private Instant currentTime;
    private boolean isPaused;

    private Banco bank;
    private Interfaz gui;

    public Simulation(Banco banco, Interfaz gui) {
        this.tickInterval = Duration.ofSeconds(1);
        this.currentTime = Instant.now();
        this.isPaused = true;
        this.bank = banco;
        this.gui = gui;
    }

    /**
     * Implementación necesaria de run (por implementar Runnable)
     * Se ejecuta en otro hilo
     * La simulación actualiza el estado de la aplicación cada vez que pasa
     * el tiempo indicado en tickInterval. En cada tick, se avanza
     * la fecha simulada dentro de la aplicación, el estado del banco y, si
     * es necesario, la interfaz gráfica.
     */
    @Override
    public void run() {
        Instant nextTickTime = Instant.now().plus(tickInterval);

        while (true) {
            Instant currentTime = Instant.now();

            // Actualizar estado del banco e interfaz
            Boolean bankHasChanged = updateBank();

            if (bankHasChanged)
                updateUI();

            // Calculamos el tiempo restante para completar el tick y esperamos
            // long sleepTime = nextTickTime - currentTime;
            // if (sleepTime > 0) {
            //     try {
            //         Thread.sleep(sleepTime);
            //     } catch (InterruptedException e) {
            //         Thread.currentThread().interrupt();
            //         break;
            //     }
            // }

            // nextTickTime += tickInterval;
        }
    }

    private Boolean updateBank() {
        return true;
    }

    private void updateUI() {
        return;
    }

    public void togglePause() {
        this.isPaused = !isPaused;
    }
}
