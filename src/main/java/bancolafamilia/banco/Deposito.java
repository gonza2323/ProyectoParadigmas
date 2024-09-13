package bancolafamilia.banco;

import java.time.LocalDateTime;

public class Deposito extends Operacion{
    public int caja;
    public static final float montoInmediato = 170000000;
    public static final float montoMax = 300000000;

    public Deposito(LocalDateTime fecha, Cliente client, float monto, int caja) {

        super(fecha, client, monto);
        this.caja = caja;
    }

    @Override
    public String getDescription() {
        return "Realizado en sucursal";
    }
    
    @Override
    public void realizarOperacion(Cliente client, float amount) {
        client.balance += amount;
    }

    public int getCaja() {
        return caja;
    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Cajero;
    }
}
