package bancolafamilia.banco;

import java.time.LocalDateTime;

public class Prestamo extends Operacion{

    public Prestamo(LocalDateTime fecha, Cliente client, float monto) {
        super(fecha, client, monto);
    }

    @Override
    public void realizarOperacion(Cliente cliente, float amount) {

    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Gerente;
    }
}
