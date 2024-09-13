package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Prestamo extends Operacion{
    
    public static final float annualInterestRate = 1.15f;
    
    private LocalDateTime cancellationDate;

    public Prestamo(LocalDateTime fecha, Cliente client, float monto) {
        super(fecha, client, monto);
    }

    @Override
    public String getDescription() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String cancellationDateFormatted = cancellationDate.format(formatter);
        return "Interés: " + annualInterestRate + "\nVencimiento: " + cancellationDateFormatted;
    }

    @Override
    public void realizarOperacion(Cliente cliente, float amount) {

    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Gerente;
    }
}
