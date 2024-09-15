package bancolafamilia.banco;

import java.time.LocalDateTime;


public class TransferenciaEspecial extends Transferencia {
    public TransferenciaEspecial(LocalDateTime date, Client client, Client recipient, float amount, String motive) {
        super(date, client, recipient, amount, motive);
    }

    @Override
    public OpStatus process(IOperationProcessor processor) {
        return processor.processOperation(this);
    }
}
