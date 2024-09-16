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

public class TimeSimulation implements Runnable {

    private Banco banco;
    private Interfaz gui;

    private final Duration tickInterval;
    private double timeMultiplier;
    private Instant currentTime;
    
    private volatile boolean isPaused;
    private volatile boolean isStopped;
    
    private final Object pauseLock = new Object();

    public TimeSimulation(Duration tickInterval, double timeMultiplier) {
        this.tickInterval = tickInterval;
        this.timeMultiplier = timeMultiplier;
        this.currentTime = Instant.now();
        this.isPaused = false;
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

    public double setTimeMultiplier(TimeMultiplier multiplier) {
        switch (multiplier) {
            case REAL_TIME:
                return timeMultiplier = 1.0;
            case ONE_DAY_PER_SECOND:
                return timeMultiplier = 1.0 * 3600 * 24;
            case TWO_DAYS_PER_SECOND:
                return timeMultiplier = 1.0 * 3600 * 24;
            case ONE_WEEK_PER_SECOND:
                return timeMultiplier = 1.0 * 3600 * 24 * 7;
            case ONE_MONTH_PER_SECOND:
                return timeMultiplier = 1.0 * 3600 * 24 * 30;
            default:
                return timeMultiplier = 1.0;
        }
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.ofInstant(currentTime, ZoneId.systemDefault());
    }

    public void setBank(Banco bank) {
        this.banco = bank;
    }

    public void setGui(Interfaz gui) {
        this.gui = gui;
    }

    enum TimeMultiplier {
        REAL_TIME,
        ONE_DAY_PER_SECOND,
        TWO_DAYS_PER_SECOND,
        ONE_WEEK_PER_SECOND,
        ONE_MONTH_PER_SECOND
    }
}