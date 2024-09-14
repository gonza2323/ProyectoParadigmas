package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

public class Retiro extends Operacion{
    public static final float montoMax = 1000000;

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
    public void realizarOperacion() {
        client.balance -= amount;
    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Cajero;
    }

    @Override
    public List<User> getParticipants() {
        return Arrays.asList(client, cajeroResponsable);
    }
}
