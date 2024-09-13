package bancolafamilia.banco;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class AgenteEspecial extends Empleado {

    public static final float montoMaxOpEspecial = 600000000;

    private LinkedList<Cliente> clientesPendientes;
    private LinkedList<DocumentoCliente> activosEnProceso;
    //clientes en proceso - creo que esta lista y la anterior deberian ir en bco


    public AgenteEspecial(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
        this.clientesPendientes = new LinkedList<>();
        this.activosEnProceso = new LinkedList<>();

    }

    @Override
    public void recieveSolicitud(Operacion operacion) { //este metodo es solo para cuando se necesite que el agente especial apruebe una operacion
    }

    protected void recieveTarea(Cliente cliente){
        //hay que manejar el caso para el cual el cliente ya esta en la lista
        clientesPendientes.add(cliente); //en esta lista se van almacenando los clientes que quieren lavar
        //1. el cliente va recibir un mensaje del agente especial en el que le pide el monto que quiere lavar



         //el agente se comunica con el cliente y genera un documento en el que se encuentra el cliente y el monto que quiere lavar

    }

    public void registarDatosTransaccion(Cliente client, float amount){

        DocumentoCliente document = new DocumentoCliente(client, amount);
        clientesPendientes.remove(client);
        activosEnProceso.add(document); //esta linkedlist sirve para que el gerente sepa que tiene que supervisar
    }

    public void operar(ArrayList<Empleado> listaEmpleados){

    }



    private int manipularCajero(LinkedList<Cajero> cajeros){
        //selecciona aleatoriamente el cajero manipulado
        Random random = new Random();
        int randomIndex = random.nextInt();
        int randomElement = cajeros.get(randomIndex).getCaja();
        return randomElement;
    }













    public LinkedList<DocumentoCliente> getActivosEnProceso() {
        return activosEnProceso;
    }
}



    class DocumentoCliente {
        private Cliente client;
        private float amount;

        public DocumentoCliente(Cliente client, float amount) {
            this.client = client;
            this.amount = amount;

        }

        public float getAmount() {
            return amount;
        }

        public Cliente getClient() {
            return client;
        }






    }






