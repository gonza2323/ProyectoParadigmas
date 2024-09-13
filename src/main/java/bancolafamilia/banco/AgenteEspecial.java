package bancolafamilia.banco;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class AgenteEspecial extends Empleado {

    //montoMaxEspecial: monto maximo que se puede aceptar para lavar plata
    public static final float montoMaxOpEspecial = 700000000;

    //clientesPendientes: aqui se almacenan los clienetes que hicieron la transferencia especial pero aun no han avisado el monto que quiere lavar - no se si es necesaria pero la dejo por las dudas!
    private LinkedList<Cliente> clientesPendientes;

    //activosEnProceso: es una lista con dcoumentos creados por el agente especial para mandarselos al cajero y para que el gerente pueda llevar el control de lo que se esta haciendo
    private LinkedList<DocumentoClienteEspecial> activosEnProceso;



    public AgenteEspecial(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
        this.clientesPendientes = new LinkedList<>();
        this.activosEnProceso = new LinkedList<>();

    }

    @Override
    public void recieveSolicitud(Operacion operacion) {}

    protected void recieveTarea(Cliente cliente){
        clientesPendientes.add(cliente);
        //el agente se comunica con el cliente y genera un documento en el que se encuentra el cliente y el monto que quiere lavar

    }


    //Se sabe que el agente proceso la transaccion cuando devuelve la caja a la que tiene que ir el cliente
    public Cajero procesarTransaccionEspecial(Cliente client, float amount, ArrayList<Cajero> cajerosDisponibles){
        //1. cuando el bco llama a este metodo es porque ya ha aprobado el monto a lavar que solicito el cliente

        //2. El agente manipula al cajero
        Cajero cajero = manipularCajero(cajerosDisponibles);

        //3. crea el documento en el registra: cliente, monto y la caja a la que va a ir.
        DocumentoClienteEspecial especialDocument = new DocumentoClienteEspecial(client, amount, cajero.getCaja());

        //4. le envia el documento al cajero para que sepa que cliente va a ir y el monto que va a depositar
        sendDocumentCajero(cajero,especialDocument);

        //5. saca al cliente de la lista de pendientes porque se comunicaron
        clientesPendientes.remove(client);

        //6. registra el documento en la lista de los activos que esta lavando - esta lista tambien sirve para que el gerente sepa los movimientos que tiene que supervisar
        activosEnProceso.add(especialDocument);
        return cajero;
    }

    public Cajero manipularCajero(ArrayList<Cajero> cajerosDisponibles){ //aleatoriamente se selcciona un cajero de la lista de cajerosdisponibles
        Random random = new Random();
        int randomIndex = random.nextInt(cajerosDisponibles.size());
        Cajero cajero = cajerosDisponibles.get(randomIndex);
        return cajero;
    }

    public void sendDocumentCajero(Cajero cajero, DocumentoClienteEspecial document){
        cajero.recieveDocument(document);
    }

    public LinkedList<DocumentoClienteEspecial> getActivosEnProceso() {
        return activosEnProceso;
    }
}










