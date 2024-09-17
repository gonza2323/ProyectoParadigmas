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

/**
 * Simula el paso del tiempo y actualiza al banco y la interfaz cuando es necesario
 */
public class TimeSimulation implements Runnable {

    private static TimeSimulation timeSim;
    private final float[] speeds = {1f, 60f, 3600f, 3600f * 12f, 3600f * 24f, 3600f * 24f * 7};
    private final String[] timeMultiplierStrings = {"Tiempo Real", "1 min/s", "1 hora/s", "12 horas/s", "1 día/s", "1 semana/s"};
    private int currentSelectedSpeed = 2;

    private Banco banco;
    private Interfaz gui;
    private Simulation simulation;

    private final Duration tickInterval;
    private double timeMultiplier;
    private Instant currentTime;
    
    private volatile boolean isPaused;
    private volatile boolean isStopped;
    
    private final Object pauseLock = new Object();

    public TimeSimulation(Duration tickInterval, double timeMultiplier) {
        timeSim = this;
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

    public LocalDateTime getDateTime() {
        return LocalDateTime.ofInstant(currentTime, ZoneId.systemDefault());
    }

    public void setBank(Banco bank) {
        this.banco = bank;
    }

    public void setGui(Interfaz gui) {
        this.gui = gui;
    }

    public static LocalDateTime getTime() {
        return timeSim.getDateTime();
    }

    public static TimeSimulation getInstance() {
        return timeSim;
    }

    public void togglePause() {
        if (isPaused)
            resume();
        else
            pause();
    }

    public void decreaseSpeed() {
        if (currentSelectedSpeed > 0) {
            currentSelectedSpeed -= 1;
            timeMultiplier = speeds[currentSelectedSpeed];
        }
    }

    public void increaseSpeed() {
        if (currentSelectedSpeed < speeds.length - 1) {
            currentSelectedSpeed += 1;
            timeMultiplier = speeds[currentSelectedSpeed];
        }
    }

    public void setTimeMultiplier(int timeMultiplierIndex) {
        if (0 < timeMultiplierIndex && timeMultiplierIndex < speeds.length) {
            currentSelectedSpeed = timeMultiplierIndex;
            timeMultiplier = speeds[currentSelectedSpeed];
        }
    }

    public String getTimeMultiplierStr() {
        return timeMultiplierStrings[currentSelectedSpeed];
    }



}