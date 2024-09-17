package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

public class Transferencia extends Operacion {

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

    public static boolean isTransferenciaEspecial(Transferencia transfer){
        String aliasTransEspecial = "la.cosa.nostra";
        String motivoEspecial = "honorarios";
        return transfer.getRecipient().getAlias().equals(aliasTransEspecial)
            && transfer.motive.equals(motivoEspecial);
    }

    private Client getRecipient() {
        return recipient;
    }
}
