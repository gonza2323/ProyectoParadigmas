package bancolafamilia;

import java.time.Duration;
import java.time.Instant;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import bancolafamilia.banco.Banco;
import bancolafamilia.gui.Interfaz;

public class Simulation implements Runnable {

    private final Banco banco;
    private final Interfaz gui;

    private final Duration tickInterval;
    private final double timeMultiplier;
    private final Object pauseLock = new Object();
    
    private volatile boolean isPaused;
    private volatile boolean isStopped;
    private Instant currentTime;

    public Simulation(Banco banco, Interfaz gui, Duration tickInterval, double timeMultiplier) {
        this.banco = banco;
        this.gui = gui;
        this.tickInterval = tickInterval;
        this.timeMultiplier = timeMultiplier;
        this.currentTime = Instant.now();
        this.isPaused = true;
        this.isStopped = false;
    }

    public void run() {
        Instant nextTickTime = Instant.now().plus(tickInterval);

        while (!isStopped) {
            synchronized (pauseLock) {
                while (isPaused) {
                    try {
                        pauseLock.wait(); // Wait while paused
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            
            performTick();
            
            Instant currentTime = Instant.now();
            long sleepDuration = Duration.between(currentTime, nextTickTime).toMillis();
            sleepDuration = sleepDuration < 0 ? tickInterval.toMillis() : sleepDuration;

            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            nextTickTime = Instant.now().plus(tickInterval);
        }
    }

    private void performTick() {
        currentTime = currentTime.plus(tickInterval.multipliedBy((long) timeMultiplier));
        gui.updateTime();

        boolean hasChanged = banco.procesarOperacionesProgramadas(getDateTime());

        if (hasChanged)
            gui.update();
    }

    public void pause() {
        synchronized (pauseLock) {
            isPaused = true;
        }
    }

    public void resume() {
        synchronized (pauseLock) {
            isPaused = false;
            pauseLock.notify();
        }
    }

    public void stop() {
        isStopped = true; // Set stop flag to true
        resume(); // Ensure thread wakes up if paused and exits
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.ofInstant(currentTime, ZoneId.systemDefault());
    }
}