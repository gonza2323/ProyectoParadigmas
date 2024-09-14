package bancolafamilia.banco;

import java.time.LocalDateTime;

public class Transferencia extends Operacion{

    public final Client recipient;
    public final String motivo;
    public static final float montoInmediata = 1000000; //monto minimo para transf inmediatas
    public static final float montoMax = 10000000; //uso static para la ctte pertenzeca a la clase transferencia (que solo haya una copia de la constante en la memoria) en lugar de que pertenezaca a cada instancia individual de la clase


    public Transferencia(LocalDateTime fecha, Client client, Client recipient, float monto, String motivo) {
        super(fecha, client, monto);
        this.recipient = recipient;
        this.motivo = motivo;
    }

    @Override
    public String getDescription() {
        return "Ordenante: " + getCliente().getNombre() + "\nBeneficiario: " + recipient.getNombre();
    }

    @Override
    public void realizarOperacion(Client client, float amount) {
        if (client == null){ //si client es null la transferencia es no rastreable y se descuenta el dinero de la cuenta del agente especial
            recipient.balance += amount;
            recipient.getAgenteEspecial().getCtaCliente().balance -= amount;
        } else {
            client.balance -= amount; //se hace la transferencia
            recipient.balance += amount;
        }

    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return employee instanceof Gerente;
    }

    public String getMotive() {
        return motivo;
    }
}
