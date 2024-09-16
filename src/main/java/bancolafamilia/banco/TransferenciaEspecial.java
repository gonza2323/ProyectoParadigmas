package bancolafamilia.banco;

import java.io.Serializable;
import java.time.LocalDateTime;


public class TransferenciaEspecial extends Transferencia implements Serializable {
    private static final long serialVersionUID = 1L;
    public TransferenciaEspecial(LocalDateTime date, Client client, Client recipient, float amount, String motive) {
        super(date, client, recipient, amount, motive);
    }

    @Override
    public OpStatus process(IOperationProcessor processor) {
        return processor.processOperation(this);
    }

    @Override
    public String getDescription() {
        return "Transferencia especial";
    }
}
