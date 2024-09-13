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



