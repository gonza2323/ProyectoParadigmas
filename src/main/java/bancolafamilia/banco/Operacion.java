package bancolafamilia.banco;

import java.util.Date;


public abstract class Operacion {

    private Date fecha;
    private Cliente client; //solo los clientes realizan estas operaciones
    private float monto;
    private String estado;

    public Operacion(Date fecha, Cliente client, float monto, String estado) {
        this.fecha = fecha;
        this.client = client;
        this.monto = monto;
        this.estado = estado;
    }

    public float getMonto() {
        return monto;
    }

    public String getEstado() {
        return estado;
    }

    public Cliente getCliente() {
        return client;
    }

}

/*class Transferencia extends Operacion{

    public Transferencia(Date fecha, Cliente client, float monto, String estado) {
        super(fecha, client, monto, estado);
    }
}*/

class Transferencia extends Operacion{
    private Cliente recipient;

    public Transferencia(Date fecha, Cliente client, Cliente recipient, float monto, String estado) {
        super(fecha, client, monto, estado);
    }


}


class Prestamo extends Operacion{

    public Prestamo(Date fecha, Cliente client, float monto, String estado) {
        super(fecha, client, monto, estado);
    }
}

class Deposito extends Operacion{

    public Deposito(Date fecha, Cliente client, float monto, String estado) {
        super(fecha, client, monto, estado);
    }
}

class Retiro extends Operacion{

    public Retiro(Date fecha, Cliente client, float monto, String estado) {
        super(fecha, client, monto, estado);
    }
}



