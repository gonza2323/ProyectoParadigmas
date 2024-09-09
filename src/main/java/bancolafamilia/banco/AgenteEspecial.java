package bancolafamilia.banco;

import java.util.LinkedList;
import java.util.Queue;

public class AgenteEspecial extends Empleado {
    private Queue<Cliente> listaClientes;


    public AgenteEspecial(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
        this.listaClientes = new LinkedList<>();
    }

    @Override
    public void recieveSolicitud(Operacion operacion) { //este metodo es solo para cuando se necesite que el agente especial apruebe una operacion
    }

    protected void recieveTarea(Cliente cliente){
        listaClientes.add(cliente);

    }








}

