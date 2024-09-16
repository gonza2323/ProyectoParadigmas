package bancolafamilia.banco;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class AgenteEspecial extends Empleado {

    //montoMaxEspecial: monto maximo que se puede aceptar para lavar plata
    public static final float montoMaxOpEspecial = 300000000;

    //el agente especial va a tener una cuenta cliente asociada a donde va a caer el dinero que va a lavar
    //tiene que tener una cuenta asociada pq deposito.realizarOperacion recibe un Cliente y el monto
    private Client ctaCliente;

    //clientesPendientes: aqui se almacenan los clienetes que hicieron la transferencia especial pero aun no han avisado el monto que quiere lavar - no se si es necesaria pero la dejo por las dudas!
    private LinkedList<Client> clientesPendientes;
    private ArrayList<Client> assignedClients = new ArrayList();

    //activosEnProceso: es una lista con dcoumentos creados por el agente especial para mandarselos al cajero y para que el gerente pueda llevar el control de lo que se esta haciendo
    private LinkedList<DocumentoClienteEspecial> activosEnProceso;



    public AgenteEspecial(String nombre, int dni, String username, String password, Client ctaCliente) {
        super(nombre, dni, username, password);
        this.clientesPendientes = new LinkedList<>();
        this.activosEnProceso = new LinkedList<>();
        this.ctaCliente = ctaCliente;
    }


    protected void recieveTarea(Client cliente){
        clientesPendientes.add(cliente);
        cliente.promoteToPremiumClient(); //activamos el flag para que en la ventana de mensajes del cliente se encuentre la notifcaion pidiendo el monto que desea lavar -> es como que el agente especial le envia un mensaje al cliente al activar su flag

        //el agente se comunica con el cliente y genera un documento en el que se encuentra el cliente y el monto que quiere lavar

    }

    // public void verificarFondos(DocumentoClienteEspecial document){
    //     //Si llega a este metodo es pq el cajero le esta avisando que ya aprobo el deposito, entonces:

    //     //1. verifica que este el dinero en la cuenta
    //     if (this.ctaCliente.getBalance() >= document.getAmount()){
    //         document.setInProcess(true);
    //         //1.1 va a iniciarTransaccion espeacial para pedir la simulacion al modula de lavado de dienro

    //     }

    // }

    // public DocumentoClienteEspecial solicitarInfoModuloEspecial(DocumentoClienteEspecial doc){
    //     ModuloEspecial modulo = new ModuloEspecial(doc.getAmount());

    //     //1. Le pide la info de la simulacion al modulo de lavado
    //     DocumentoTransaccionEspecial documentoSimulacion = modulo.sendDocumentoTransaccion();

    //     //2. Asocia el documento de simulaicon con el documento cliente especial para que sea un documento unico con los datos del cliente, monto a lavar, caja donde deposito, info proporcionada por el modulo de lavado sobre como lavar la plata
    //     doc.setDocumentoSimulacion(documentoSimulacion);
    //     return doc;

    // }

    public void acceptClient(Client client, Queue<Client> pendingPremiumClients, List<Cajero> cajeros) {
        int nroCaja = assignTellerToPremiumClient(client, cajeros);
        assignedClients.add(client);
        pendingPremiumClients.remove(client);
        client.sendNotification("Utilice la caja Nro. " + nroCaja);
    }

    private int assignTellerToPremiumClient(Client client, List<Cajero> cajeros) {
        Random random = new Random();
        int randomIndex = random.nextInt(cajeros.size());
        Cajero cajero = cajeros.get(randomIndex);

        DocumentoClienteEspecial document = new DocumentoClienteEspecial(client, this);
        cajero.recieveDocument(document);

        return cajero.getCaja();
    }

    public Client getCtaCliente() { return ctaCliente; }
    public float getBalance() { return ctaCliente.getBalance(); }
    public LinkedList<DocumentoClienteEspecial> getActivosEnProceso() { return activosEnProceso; }
    public ArrayList<Client> getAssignedClients() { return assignedClients; }
}










