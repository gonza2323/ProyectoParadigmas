package bancolafamilia.banco;

import java.io.Serializable;
import java.time.LocalDateTime;

public class financialAdvice implements Serializable {

    private static final long serialVersionUID = 1L;

    public String advice;
    public LocalDateTime fecha;
    public String tipo;
    private final Client client;

    public financialAdvice(LocalDateTime fecha, Client client, String advice, String tipo) {
        this.advice = advice;
        this.fecha = fecha;
        this.tipo = tipo;
        this.client = client;
    }

    public String getAdvice() {
        return advice;
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Client getClient() {
        return client;
    }
}
