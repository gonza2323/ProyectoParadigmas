package bancolafamilia.banco;

public class Empleado extends User {
    public Empleado(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }
}

class Cajero extends Empleado {
    public Cajero(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }
}

class Gerente extends Empleado {
    public Gerente(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }
}

class AgenteEspecial extends Empleado {
    public AgenteEspecial(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }
}