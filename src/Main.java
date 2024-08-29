import javax.swing.plaf.IconUIResource;

public class Main {
    public static void main(String[] args) {
        Banco banco = new Banco();

        banco.aniadirEmpleado(new Gerente());
        banco.aniadirEmpleado(new Cajero());
        banco.aniadirEmpleado(new AgenteEspecial());

        System.out.println("Qué desea hacer?");
        System.out.println("[1] Simular operaciones");
        System.out.println("[2] Login Cliente");
        System.out.println("Qué desea hacer?");
    }
}

class Interfaz {
    Estado estado;


}