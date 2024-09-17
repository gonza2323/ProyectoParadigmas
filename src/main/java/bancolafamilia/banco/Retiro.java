package bancolafamilia.banco;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Retiro extends Operacion implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final float montoMax = 500000;

    private final Cajero cajeroResponsable;

    public Retiro(LocalDateTime fecha, Client client, float monto, Cajero cajeroResponsable) {
        super(fecha, client, monto);

        this.cajeroResponsable = cajeroResponsable;
    }

    @Override
    public String getDescription() {
        return "Realizado en sucursal";
    }

    @Override
    public OpStatus process(IOperationProcessor processor) {
        return processor.processOperation(this);
    }

    @Override
    public List<User> getParticipants() {
        return Arrays.asList(client, cajeroResponsable);
    }
}
