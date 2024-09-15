package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.util.List;


public abstract class Operacion implements Comparable<Operacion> {

    private static int nextId = 1;

    protected final int id;
    protected final LocalDateTime date; //este método es protected porque necesitamos un setter para el tributo cliente de deposito
    protected Client client; //solo los clientes realizan estas operaciones
    protected final float amount;
    protected OpStatus status;

    public enum OpStatus {
        APPROVED,
        DENIED,
        MANUAL_APPROVAL_REQUIRED,
    }

    public Operacion(LocalDateTime date, Client client, float amount) {
        this.id = Operacion.nextId++;
        this.date = date;
        this.client = client;
        this.amount = amount;
    }

    public void aprobar(){
        this.status = OpStatus.APPROVED;
    }

    public void denegar(){
        this.status = OpStatus.DENIED;
    }
    
    public abstract String getDescription();
    public abstract OpStatus process(IOperationProcessor processor);
    public abstract boolean isAprobadaPor(Empleado employee);

    @Override
    public int compareTo(Operacion other) {
        return this.date.compareTo(other.date);
    }
    
    public List<User> getParticipants() {
        return List.of(client);
    }

    public LocalDateTime getDate() { return date; }
    public Client getCliente() { return client; }
    public float getAmount() { return amount; }
    public OpStatus isAprobada(){ return status; }

    public int getId() { return id; }
}
