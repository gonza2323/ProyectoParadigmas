package bancolafamilia.banco;

public class Cajero extends Empleado {
    public Cajero(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }

    @Override
    public void receptSolicitud(Operacion operacion) {

    }


}
