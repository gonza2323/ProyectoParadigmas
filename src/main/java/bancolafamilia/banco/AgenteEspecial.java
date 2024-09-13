package bancolafamilia.banco;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class AgenteEspecial extends Empleado {

    //montoMaxEspecial: monto maximo que se puede aceptar para lavar plata
    public static final float montoMaxOpEspecial = 700000000;

    //el agente especial va a tener una cuenta cliente asociada a donde va a caer el dinero que va a lavar
    //tiene que tener una cuenta asociada pq deposito.realizarOperacion recibe un Cliente y el monto
    private Cliente ctaCliente;

    //clientesPendientes: aqui se almacenan los clienetes que hicieron la transferencia especial pero aun no han avisado el monto que quiere lavar - no se si es necesaria pero la dejo por las dudas!
    private LinkedList<Cliente> clientesPendientes;

    //activosEnProceso: es una lista con dcoumentos creados por el agente especial para mandarselos al cajero y para que el gerente pueda llevar el control de lo que se esta haciendo
    private LinkedList<DocumentoClienteEspecial> activosEnProceso;



    public AgenteEspecial(String nombre, int dni, String username, String password, Cliente ctaCliente) {
        super(nombre, dni, username, password);
        this.clientesPendientes = new LinkedList<>();
        this.activosEnProceso = new LinkedList<>();
        this.ctaCliente = ctaCliente;

    }

    @Override
    public void recieveSolicitud(Operacion operacion) {}

    protected void recieveTarea(Cliente cliente){
        clientesPendientes.add(cliente);
        cliente.setFlagSolicitud(true); //activamos el flag para que en la ventana de mensajes del cliente se encuentre la notifcaion pidiendo el monto que desea lavar -> es como que el agente especial le envia un mensaje al cliente al activar su flag

        //el agente se comunica con el cliente y genera un documento en el que se encuentra el cliente y el monto que quiere lavar

    }


    //Se sabe que el agente procesó la transaccion cuando devuelve la caja a la que tiene que ir el cliente
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

    public void verificarFondos(DocumentoClienteEspecial document){
        //Si llega a este metodo es pq el cajero le esta avisando que ya aprobo el deposito, entonces:

        //1. verifica que este el dinero en la cuenta
        if (this.ctaCliente.balance >= document.getAmount()){
            document.setInProcess(true);
            //1.1 va a iniciarTransaccion espeacial para pedir la simulacion al modula de lavado de dienro

        }

    }

    public DocumentoClienteEspecial solicitarInfoModuloEspecial(DocumentoClienteEspecial doc){
        ModuloEspecial modulo = new ModuloEspecial(doc.getAmount());

        //1. Le pide la info de la simulacion al modulo de lavado
        DocumentoTransaccionEspecial documentoSimulacion = modulo.sendDocumentoTransaccion();

        //2. Asocia el documento de simulaicon con el documento cliente especial para que sea un documento unico con los datos del cliente, monto a lavar, caja donde deposito, info proporcionada por el modulo de lavado sobre como lavar la plata
        doc.setDocumentoSimulacion(documentoSimulacion);
        return doc;

    }

//    public void programarTransferenciasNoRastreables(DocumentoClienteEspecial doc){
//
//        for (int i = 0; i<doc.getDocumentoSimulacion().getNumTransferenciasDiarias(); i++){
//
//
//    }



    public LinkedList<DocumentoClienteEspecial> getActivosEnProceso() {
        return activosEnProceso;
    }

    //Este getter le proporciona al cajero la cuenta cliente del agente especial para que sepa a donde va a depositar el dinero para el lavado.
    public Cliente getCtaCliente() {
        return ctaCliente;
    }


}










