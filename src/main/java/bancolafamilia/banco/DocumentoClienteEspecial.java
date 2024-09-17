package bancolafamilia.banco;
import java.io.Serializable;

/**
 * Describe la relación entre un cliente mafioso y su agente especial designado.
 * 
 * Al realizar un depósito, si el cajero correspondiente posee un documento
 * que asocie al cliente con un agente especial, el depósito será redirigido
 * a la cuenta del agente especial.
 * 
 * Al hacer esto, se crea un nuevo documento, que ahora incluye el monto a lavar
 */
public class DocumentoClienteEspecial implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int nextId = 1;

    private final int id;
    private final Client client;
    private final AgenteEspecial agent;
    private float amount = 0;

    public DocumentoClienteEspecial(Client client, AgenteEspecial agent) {
        this.id = nextId++;
        this.client = client;
        this.agent = agent;
    }

    // setters
    public void setAmount(float amount) { this.amount = amount; }

    // getters
    public int getId() { return id; }
    public Client getClient() { return client; }
    public AgenteEspecial getAgenteEspecial() { return agent; }
    public float getAmount() { return amount; }
}