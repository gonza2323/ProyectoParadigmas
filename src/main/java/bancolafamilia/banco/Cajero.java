package bancolafamilia.banco;

public class Cajero extends Empleado implements IOpBcoEmpleado{
    public Cajero(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }

    @Override
    public void recieveSolicitud(Operacion operacion) {

    }


    @Override
    public void aprobarOperacion(Operacion operacion) {

    }


}
