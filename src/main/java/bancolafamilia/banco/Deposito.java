package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

public class Deposito extends Operacion {
    
    public static final float montoMax = 500000;
    
    private final Cajero cajeroResponsable;
    public int caja;
    public boolean flag = false;

    public Deposito(LocalDateTime date, Client client, float amount, int caja, Cajero cajero) {
        super(date, client, amount);
        this.caja = caja;
        this.cajeroResponsable = cajero;
    }

    @Override
    public String getDescription() {
        return "Realizado en sucursal";
    }

    public int getCaja() {
        return caja;
    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Cajero;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
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


