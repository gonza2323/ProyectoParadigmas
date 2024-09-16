package bancolafamilia.banco;

import java.io.Serializable;

public interface IOpBcoEmpleado extends Serializable {

    static final long serialVersionUID = 1L;
    void aprobarOperacion(Operacion operacion);
}
