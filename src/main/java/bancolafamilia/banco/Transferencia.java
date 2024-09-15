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

    // @Override
    // public void realizarOperacion() {
    //     if (client == null){ //si client es null la transferencia es no rastreable y se descuenta el dinero de la cuenta del agente especial
    //         recipient.balance += amount;
    //         recipient.getAgenteEspecial().getCtaCliente().balance -= amount;
    //     } else {
    //         client.balance -= amount; //se hace la transferencia
    //         recipient.balance += amount;
    //     }
    // }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Gerente;
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

    public static boolean isTransferenciaEspecial(Client recipient){
        String aliasTransEspecial = "la.cosa.nostra";
        return recipient.getAlias().equals(aliasTransEspecial);
    }
}
