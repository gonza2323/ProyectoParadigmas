package bancolafamilia.banco;

import java.io.Serializable;

public class SimulacionDeRecirculacion implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int nextId = 1;

    private final int id;
    private final float amount;
    private final int tiempoDias;
    private final int numTransferenciasDiarias;
    private final float montoMaxDiario;

    public SimulacionDeRecirculacion(float amount, int tiempoDias, int maxTransferenciasDiarias, float montoMaxDiario, int totalTransfers) {
        this.id = nextId++;
        this.amount = amount;
        this.tiempoDias = tiempoDias;
        this.numTransferenciasDiarias = maxTransferenciasDiarias;
        this.montoMaxDiario = montoMaxDiario;
    }

    public float getMontoMaxDiario() {
        return montoMaxDiario;
    }

    public int getNumTransferenciasDiarias() {
        return numTransferenciasDiarias;
    }

    public int getTiempoDias() {
        return tiempoDias;
    }

    public int getId() { return id; }
    public float getMonto() { return amount; }
    public int getTotalTransfers() { return tiempoDias * numTransferenciasDiarias; }
}
