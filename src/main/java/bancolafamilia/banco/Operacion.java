package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public abstract class Operacion {

    private final LocalDateTime fecha;
    private final Cliente client; //solo los clientes realizan estas operaciones
    private final float monto;
    private Boolean aprobada; //es un tipo de objeto envuelto para boolean (tipo de referencia que me permite asignarle como valor inicial null a isAprobada) porque necesito saber cuando la operacion es aprobada, denegada o no se ha visto todavia la solicitud


    public Operacion(LocalDateTime fecha, Cliente client, float monto) {
        this.fecha = fecha;
        this.client = client;
        this.monto = monto;
    }

    public float getMonto() {
        return monto;
    }

    public Cliente getCliente() {
        return client;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Boolean isAprobada(){
        return aprobada;
    }

    public void aprobar(){
        this.aprobada = true;
    }

    public void denegar(){
        this.aprobada = false;
    }

    public abstract String getDescription();

    public abstract void realizarOperacion(Cliente cliente, float amount);

    public abstract boolean isAprobadaPor(Empleado employee);

}

/*class Transferencia extends Operacion{

    public Transferencia(Date fecha, Cliente client, float monto, String estado) {
        super(fecha, client, monto, estado);
    }
}*/

class Transferencia extends Operacion{

    public static final float montoInmediata = 1000000; //monto minimo para transf inmediatas
    public static final float montoMax = 10000000; //uso static para la ctte pertenzeca a la clase transferencia (que solo haya una copia de la constante en la memoria) en lugar de que pertenezaca a cada instancia individual de la clase
    
    private Cliente recipient;
    private String motivo;

    public Transferencia(LocalDateTime fecha, Cliente client, Cliente recipient, float monto, String motivo) {
        super(fecha, client, monto);
        this.recipient = recipient;
        this.motivo = motivo;

    }

    @Override
    public String getDescription() {
        return "Ordenante: " + getCliente().getNombre() + "\nBeneficiario: " + recipient.getNombre();
    }

    @Override
    public void realizarOperacion(Cliente client, float amount) {
        client.balance -= amount; //se hace la transferencia
        recipient.balance += amount;
    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Gerente;
    }

    public String getMotive() {
        return motivo;
    }
}


class Prestamo extends Operacion{
    public static final float annualInterestRate = 1.15f;
    
    private LocalDateTime cancellationDate;

    public Prestamo(LocalDateTime fecha, Cliente client, float monto, String estado, LocalDateTime cancellationDate) {
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

class Deposito extends Operacion{

    public Deposito(LocalDateTime fecha, Cliente client, float monto, String estado) {
        super(fecha, client, monto);
    }

    @Override
    public String getDescription() {
        return "Realizado en sucursal";
    }
    
    @Override
    public void realizarOperacion(Cliente cliente, float amount) {

    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Cajero;
    }
}

class Retiro extends Operacion{

    public Retiro(LocalDateTime fecha, Cliente client, float monto, String estado) {
        super(fecha, client, monto);
    }

    @Override
    public String getDescription() {
        return "Realizado en sucursal";
    }

    @Override
    public void realizarOperacion(Cliente cliente, float amount) {

    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Cajero;
    }
}



