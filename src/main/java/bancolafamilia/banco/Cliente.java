package bancolafamilia.banco;


import java.util.LinkedList;
import java.util.Queue;

public class Cliente extends User implements IOpBcoCliente {

    public float balance;
    private String alias;
    private Queue<Operacion> operaciones;

    public Cliente(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
        this.operaciones = new LinkedList<>();
    }

    public float getBalance() {
        return balance;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void agregarOperacion(Operacion operacion){
        operaciones.add(operacion);

    }
}
