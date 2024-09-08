package bancolafamilia.banco;

public class AsesorFinanciero extends Empleado {
    public AsesorFinanciero(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }

    @Override
    public void receptSolicitud(Operacion operacion) {

    }
}
