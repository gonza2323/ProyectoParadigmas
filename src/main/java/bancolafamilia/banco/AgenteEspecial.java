package bancolafamilia.banco;

public class AgenteEspecial extends Empleado {
    public AgenteEspecial(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }

    @Override
    public void receptSolicitud(Operacion operacion) {

    }


}

