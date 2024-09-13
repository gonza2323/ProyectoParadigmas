package bancolafamilia.banco;

public class Cajero extends Empleado implements IOpBcoEmpleado{

    public int numCaja; //cada cajero va a tener asignado un numero de caja fijo

    public Cajero(String nombre, int dni, String username, String password, int numCaja) {
        super(nombre, dni, username, password);
        this.numCaja = numCaja;
    }

    @Override
    public void recieveSolicitud(Operacion operacion) {

    }

    public int getCaja() {
        return numCaja;
    }

    public void setNumCaja(int numCaja) {
        this.numCaja = numCaja;
    }

    @Override
    public void aprobarOperacion(Operacion operacion) {

    }


}
