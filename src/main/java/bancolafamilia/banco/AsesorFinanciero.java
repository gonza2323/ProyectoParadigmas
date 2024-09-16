package bancolafamilia.banco;

import java.io.Serializable;

public class AsesorFinanciero extends Empleado implements Serializable {

    private static final long serialVersionUID = 1L;
    public AsesorFinanciero(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }
}
