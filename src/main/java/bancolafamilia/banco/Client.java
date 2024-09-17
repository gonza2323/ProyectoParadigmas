package bancolafamilia.banco;

import java.io.Serializable;
import java.util.ArrayList;

public class Client extends User implements Serializable { //implements IOpBcoCliente

    private static final long serialVersionUID = 1L;
    private float balance;
    private float deuda;
    private String alias;
    public Portfolio portfolioActivos;
    public boolean flagSolicitud  = false; //esta variable me va a permitir saber que en el client menu page el agente especial le tiene que preguntar al cliente el monto que quiere lavar
     //en esta variable vamos a almacenar el monto que el cliente podria estar lavando
    public boolean flagRiesgoInverison = false;
    public AgenteEspecial agenteEspecial; //el agente especial es intermediario entre cliente y banco
    private boolean isPremiumClient = false; // una vez que es premium, el banco ya no acepta transacciones especiales de él
    private boolean hasNewNotifications = false;

    private ArrayList<String> notifications = new ArrayList<>();

    public Client(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);

        this.balance = 0;
        this.deuda = 0;
        this.portfolioActivos = new Portfolio();
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void addBalance(float amount) {
        balance += amount;
    }

    public void reduceBalance(float amount) {
        balance -= amount;
    }

    public void addDebt(float additionalDebt) {
        deuda += additionalDebt;
    }

    public Portfolio getPortfolioActivos() {
        return portfolioActivos;
    }

    public static Client createTempSpecialClient() {
        Client special = new Client("Banco La Familia", 0, "", "");
        special.setAlias("la.cosa.nostra");
        return special;
    }

    public boolean isPremiumClient() { return isPremiumClient; }

    public float getBalance() { return balance; }
    public float getDebt() { return deuda; }
    public boolean hasNewNotifications() { return hasNewNotifications; }
    public String getAlias() { return alias; }
    public ArrayList<String> getNotifications() { return notifications; }

    public void sendNotification(String msg) {
        notifications.add(msg);
        hasNewNotifications = true;
    }

    public void promoteToPremiumClient() {
        isPremiumClient = true;
    }

    public void markNotificationsRead() {
        hasNewNotifications = false;
    }

    public class createSpecialClient {
    }
}


