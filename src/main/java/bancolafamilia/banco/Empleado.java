package bancolafamilia.banco;

public abstract class Empleado extends User {
    public Empleado(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }
}
