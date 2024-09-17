package bancolafamilia.banco;

import java.io.Serializable;

public interface IOperationProcessor extends Serializable {
    public Operacion.OpStatus processOperation(Deposito deposit);
    public Operacion.OpStatus processOperation(DepositoEspecial deposit);
    public Operacion.OpStatus processOperation(Retiro retiro);
    public Operacion.OpStatus processOperation(Transferencia transfer);
    public Operacion.OpStatus processOperation(TransferenciaEspecial transfer);
    public Operacion.OpStatus processOperation(TransferenciaInternacional transfer);
    public Operacion.OpStatus processOperation(Prestamo loan);
}
