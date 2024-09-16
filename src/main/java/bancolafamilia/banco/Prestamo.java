package bancolafamilia.banco;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Prestamo extends Operacion implements Serializable {


    private static final long serialVersionUID = 1L;
    public static float minimumLoanAmount = 1000;
    public static float maxAmount = 15000000;
    
    private final float interest;
    private final float anualInterestRate;
    private final int months;
    private final LocalDateTime cancellationDate;

    public Prestamo(LocalDateTime date, Client client, float amount, float anualInterestRate, int months) {
        super(date, client, amount);

        this.interest = amount * anualInterestRate * months / 12;
        this.anualInterestRate = anualInterestRate;
        this.months = months;
        this.cancellationDate = date.plusMonths(months);
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
    public OpStatus process(IOperationProcessor processor) {
        return processor.processOperation(this);
    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Gerente;
    }

    public int getMonths() { return months; }
}
