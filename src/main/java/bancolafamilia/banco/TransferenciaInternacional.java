package bancolafamilia.banco;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TransferenciaInternacional extends Transferencia implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final float maxAmountImmediate = 500000; // monto máximo transferencia inmediata
    public static final float maxAmount = 10000000; // monto máximo legal

    public TransferenciaInternacional(LocalDateTime fecha, Client client, Client recipient, float monto, String motivo) {
        super(fecha, client, recipient, monto, motivo);
    }

    @Override
    public String getDescription() {
        return "Ordenante: " + getCliente().getNombre()+ "\nBeneficiario: " + recipient.getNombre();
    }

    @Override
    public OpStatus process(IOperationProcessor processor) {
        return processor.processOperation(this);
    }
}
