package bancolafamilia.banco;

import java.time.LocalDateTime;


public abstract class Operacion {

    private final LocalDateTime fecha;
    //este metodo es protetced porque necesitamos un setter para el tributo cleinte de deposito
    protected Client client; //solo los clientes realizan estas operaciones
    private final float monto;
    private Boolean aprobada; //es un tipo de objeto envuelto para boolean (tipo de referencia que me permite asignarle como valor inicial null a isAprobada) porque necesito saber cuando la operacion es aprobada, denegada o no se ha visto todavia la solicitud


    public Operacion(LocalDateTime fecha, Client client, float monto) {
        this.fecha = fecha;
        this.client = client;
        this.monto = monto;
    }

    public float getMonto() {
        return monto;
    }

    public Client getCliente() {
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

    public abstract void realizarOperacion(Client cliente, float amount);

    public abstract boolean isAprobadaPor(Empleado employee);



}
