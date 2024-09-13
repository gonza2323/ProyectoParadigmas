package bancolafamilia.banco;

import java.time.LocalDateTime;

public class Retiro extends Operacion{

    public static final float montoMax = 1000000;

    public Retiro(LocalDateTime fecha, Cliente client, float monto) {
        super(fecha, client, monto);
    }

    @Override
    public void realizarOperacion(Cliente cliente, float amount) {
        cliente.balance -= amount;
    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Cajero;
    }
}
