package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.util.List;


public abstract class Operacion implements Comparable<Operacion>{

    protected final LocalDateTime date; //este metodo es protetced porque necesitamos un setter para el tributo cleinte de deposito
    protected Client client; //solo los clientes realizan estas operaciones
    protected final float amount;
    protected Boolean aprobada; //es un tipo de objeto envuelto para boolean (tipo de referencia que me permite asignarle como valor inicial null a isAprobada) porque necesito saber cuando la operacion es aprobada, denegada o no se ha visto todavia la solicitud


    public Operacion(LocalDateTime date, Client client, float amount) {
        this.date = date;
        this.client = client;
        this.amount = amount;
    }

    public void aprobar(){
        this.aprobada = true;
    }

    public void denegar(){
        this.aprobada = false;
    }
    
    public abstract String getDescription();
    public abstract void realizarOperacion();
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
    public Boolean isAprobada(){ return aprobada; }
}
