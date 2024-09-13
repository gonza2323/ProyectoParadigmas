package bancolafamilia.banco;

import java.time.LocalDateTime;


public abstract class Operacion {

    private LocalDateTime fecha;
    private Cliente client; //solo los clientes realizan estas operaciones
    private float monto;
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

    public abstract void realizarOperacion(Cliente cliente, float amount);

    public abstract boolean isAprobadaPor(Empleado employee);

}

//la constante montoMax se usa en todas las subclases pero no se define en la clase abstracta porque su valor varia para cada una de las subclases

class Transferencia extends Operacion{

    public Cliente recipient;
    public String motivo;
    public static final float montoInmediata = 1000000; //monto minimo para transf inmediatas
    public static final float montoMax = 10000000; //uso static para la ctte pertenzeca a la clase transferencia (que solo haya una copia de la constante en la memoria) en lugar de que pertenezaca a cada instancia individual de la clase


    public Transferencia(LocalDateTime fecha, Cliente client, Cliente recipient, float monto, String motivo) {
        super(fecha, client, monto);
        this.recipient = recipient;
        this.motivo = motivo;

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
}


class Prestamo extends Operacion{

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

class Deposito extends Operacion{
    public int caja;
    public static final float montoInmediato = 170000000;
    public static final float montoMax = 300000000;

    public Deposito(LocalDateTime fecha, Cliente client, float monto, int caja) {

        super(fecha, client, monto);
        this.caja = caja;
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

class Retiro extends Operacion{

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



