package bancolafamilia.banco;

import java.time.LocalDateTime;

public class DepositoEspecial extends Deposito {

    public DepositoEspecial(LocalDateTime date, Client client, float amount, Cajero cajero) {
        super(date, client, amount, cajero);
    }

    @Override
    public OpStatus process(IOperationProcessor processor) {
        return processor.processOperation(this);
    }

    @Override
    public String getDescription() {
        return "Depósito especial";
    }
}
