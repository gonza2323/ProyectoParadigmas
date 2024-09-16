package bancolafamilia.banco;

import java.io.Serializable;

public abstract class Empleado extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    public Empleado(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }
}
