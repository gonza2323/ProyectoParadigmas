package bancolafamilia.banco;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.time.format.DateTimeFormatter;

public class Prestamo extends Operacion{
    
    private final float interest;
    private final float anualInterestRate;
    private final LocalDateTime cancellationDate;

    public Prestamo(LocalDateTime date, Client client, float amount, float anualInterestRate, int months) {
        super(date, client, amount);

        this.interest = amount * anualInterestRate * months / 12;
        this.anualInterestRate = anualInterestRate;
        this.cancellationDate = date.plusMonths(12);
    }

    @Override
    public String getDescription() {
        NumberFormat currencyFormater = NumberFormat.getCurrencyInstance(Locale.US);
        DecimalFormat percentageFormater = new DecimalFormat("0.00%");
        DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        return "Intereses: " + currencyFormater.format(interest)
            + "\nTasa anual: " + percentageFormater.format(anualInterestRate)
            + "\nVencimiento: " + dateFormater.format(cancellationDate);
    }

    @Override
    public void realizarOperacion(Client cliente, float amount) {

    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Gerente;
    }
}
