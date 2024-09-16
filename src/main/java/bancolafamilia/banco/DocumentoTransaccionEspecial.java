package bancolafamilia.banco;

import java.io.Serializable;

public class DocumentoTransaccionEspecial implements Serializable {

    private static final long serialVersionUID = 1L;
    public int tiempoDias;
    public int numTransferenciasDiarias;
    public float montoMaxDiario;


    public DocumentoTransaccionEspecial(int tiempoDias, int numTransferenciasDiarias, float montoMaxDiario) {
        this.tiempoDias = tiempoDias;
        this. numTransferenciasDiarias = numTransferenciasDiarias;
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
}
