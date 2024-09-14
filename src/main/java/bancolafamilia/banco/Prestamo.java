package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Prestamo extends Operacion{
    
    private final float montoIntereses;
    private final float tasaDeInteresAnual;
    private final LocalDateTime cancellationDate;

    public Prestamo(LocalDateTime fecha, Cliente client, float monto, float tasaDeInteresAnual, int meses) {
        super(fecha, client, monto);

        this.montoIntereses = monto * tasaDeInteresAnual * meses / 12;
        this.tasaDeInteresAnual = tasaDeInteresAnual;
        this.cancellationDate = fecha.plusMonths(12);
    }

    @Override
    public String getDescription() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String cancellationDateFormatted = cancellationDate.format(formatter);
        return "Intereses: " + montoIntereses
            + "\nTasa anual: " + tasaDeInteresAnual
            + "\nVencimiento: " + cancellationDateFormatted;
    }

    @Override
    public void realizarOperacion(Cliente cliente, float amount) {

    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Gerente;
    }
}
