package bancolafamilia.banco;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Transferencia extends Operacion implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final float maxAmountImmediate = 500000; // monto máximo transferencia inmediata
    public static final float maxAmount = 10000000; // monto máximo legal

    public final Client recipient;
    public final String motive;

    public Transferencia(LocalDateTime fecha, Client client, Client recipient, float monto, String motivo) {
        super(fecha, client, monto);
        this.recipient = recipient;
        this.motive = motivo;
    }

    @Override
    public String getDescription() {
        return "Ordenante: " + getCliente().getNombre()+ "\nBeneficiario: " + recipient.getNombre();
    }

    @Override
    public OpStatus process(IOperationProcessor processor) {
        return processor.processOperation(this);
    }

    @Override
    public List<User> getParticipants() {
        return Arrays.asList(client, recipient);
    }

    public String getMotive() { return motive; }

    public static boolean isSpecialAlias(String alias){
        String specialAlias = "la.cosa.nostra";
        return alias.equals(specialAlias);
    }

    public boolean isSpecialTransfer(){
        String motivoEspecial = "honorarios";
        return motive.equals(motivoEspecial) && isSpecialAlias(recipient.getAlias());
    }

    public Client getRecipient() {
        return recipient;
    }
}
