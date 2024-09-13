package bancolafamilia.banco;

import java.time.LocalDateTime;

public class Transferencia extends Operacion{

    public Cliente recipient;
    public String motivo;
    public static final float montoInmediata = 1000000; //monto minimo para transf inmediatas
    public static final float montoMax = 10000000; //uso static para la ctte pertenzeca a la clase transferencia (que solo haya una copia de la constante en la memoria) en lugar de que pertenezaca a cada instancia individual de la clase


    public Transferencia(LocalDateTime fecha, Cliente client, Cliente recipient, float monto, String motivo) {
        super(fecha, client, monto);
        this.recipient = recipient;
        this.motivo = motivo;

    }

    @Override
    public void realizarOperacion(Cliente client, float amount) {
        client.balance -= amount; //se hace la transferencia
        recipient.balance += amount;
    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Gerente;
    }
}
