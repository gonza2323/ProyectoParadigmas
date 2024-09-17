package bancolafamilia.banco;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class AgenteEspecial extends Empleado implements Serializable {

    private static final long serialVersionUID = 1L;
    private Banco banco;

    //montoMaxEspecial: monto maximo que se puede aceptar para lavar plata
    public static final float montoMaxOpEspecial = 300000000;

    //el agente especial va a tener una cuenta cliente asociada a donde va a caer el dinero que va a lavar
    //tiene que tener una cuenta asociada pq deposito.realizarOperacion recibe un Cliente y el monto
    private Client ctaCliente;

    private ArrayList<Client> assignedClients = new ArrayList();

    //activosEnProceso: es una lista con dcoumentos creados por el agente especial para mandarselos al cajero y para que el gerente pueda llevar el control de lo que se esta haciendo
    private final LinkedList<DocumentoClienteEspecial> tareasPendientes = new LinkedList<>();
    private final LinkedList<SimulacionDeRecirculacion> tareasFinalizadas = new LinkedList<>();


    public AgenteEspecial(String nombre, int dni, String username, String password, Banco banco) {
        super(nombre, dni, username, password);
        this.banco = banco;
        this.ctaCliente = new Client("Corleone S.A.", 0, "", "");
    }

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

    public void addNewDocument(DocumentoClienteEspecial document) {
        tareasPendientes.add(document);
    }

    public void executeTask(DocumentoClienteEspecial task) {
        var modulo = new ModuloDeRecirculacionFinanciera(task.getAmount(), banco);
        var operations = modulo.execute(task.getClient(), this);
        tareasPendientes.remove(task);
        tareasFinalizadas.add(operations);
    }

    public SimulacionDeRecirculacion simulate(DocumentoClienteEspecial task) {
        var modulo = new ModuloDeRecirculacionFinanciera(task.getAmount(), banco);
        return modulo.simulate();
    }

    public Client getCtaCliente() { return ctaCliente; }
    public float getBalance() { return ctaCliente.getBalance(); }
    public LinkedList<DocumentoClienteEspecial> getTareasPendientes() { return tareasPendientes; }
    public ArrayList<Client> getAssignedClients() { return assignedClients; }
}
