package bancolafamilia.banco;

public abstract class Empleado extends User {
    public Empleado(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }

    public abstract void receptSolicitud(Operacion operacion);
    //este metodo no se si esta bien pero no se como implementar aprobarOperacion solo en las subclases gerente y cajero que son los unicos que se requiere que aprueben operaciones
    //si hago que implemente la interfaz ITransacciones vana a tener que usar todos los metodos y hay metodos en la interfaz que no deben poder usar los empleados




}
