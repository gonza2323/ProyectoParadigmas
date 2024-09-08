package bancolafamilia.banco;

import java.util.Date;


public abstract class Operacion {

    private Date fecha;
    private Cliente client; //solo los clientes realizan estas operaciones
    private float monto;
    private Boolean aprobada; //es un tipo de objeto envuelto para boolean (tipo de referencia que me permite asignarle como valor inicial null a isAprobada) porque necesito saber cuando la operacion es aprobada, denegada o no se ha visto todavia la solicitud

    public Operacion(Date fecha, Cliente client, float monto) {
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

/*class Transferencia extends Operacion{

    public Transferencia(Date fecha, Cliente client, float monto, String estado) {
        super(fecha, client, monto, estado);
    }
}*/

class Transferencia extends Operacion{

    private Cliente recipient;
    private String motivo;
    public static final float montoMax = 10000000; //uso static para la ctte pertenzeca a la clase transferencia (que solo haya una copia de la constante en la memoria) en lugar de que pertenezaca a cada instancia individual de la clase


    public Transferencia(Date fecha, Cliente client, Cliente recipient, float monto, String motivo) {
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

    public Prestamo(Date fecha, Cliente client, float monto, String estado) {
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

    public Deposito(Date fecha, Cliente client, float monto, String estado) {
        super(fecha, client, monto);
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

    public Retiro(Date fecha, Cliente client, float monto, String estado) {
        super(fecha, client, monto);
    }

    @Override
    public void realizarOperacion(Cliente cliente, float amount) {

    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Cajero;
    }
}



