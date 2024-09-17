package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

public class Deposito extends Operacion {
    
    public static final float montoMax = 500000;
    
    private final Cajero cajeroResponsable;

    public Deposito(LocalDateTime date, Client client, float amount, Cajero cajero) {
        super(date, client, amount);
        this.cajeroResponsable = cajero;
    }

    @Override
    public String getDescription() {
        return "Realizado en caja Nro. " + cajeroResponsable.getCaja();
    }

    public int getCaja() {
        return cajeroResponsable.getCaja();
    }

    public Cajero getCajero() {
        return cajeroResponsable;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public OpStatus process(IOperationProcessor processor) {
        return processor.processOperation(this);
    }

    @Override
    public List<User> getParticipants() {
        return Arrays.asList(client, cajeroResponsable);
    }
}


