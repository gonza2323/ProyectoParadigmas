package bancolafamilia.banco;


public class Client extends User { //implements IOpBcoCliente

    private float balance;
    private float deuda;
    private String alias;
    public boolean isPremiumClient  = false; //esta variable me va a permitir saber que en el client menu page el agente especial le tiene que preguntar al cliente el monto que quiere lavar
     //en esta variable vamos a almacenar el monto que el cliente podria estar lavando
    public AgenteEspecial agenteEspecial; //el agente especial es intermediario entre cliente y banco

    public Client(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);

        this.balance = 0;
        this.deuda = 0;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setPremiumClient(boolean flagSolicitud) {
        this.isPremiumClient = flagSolicitud;
        //cuando cambia a false, el cliente ya ha indicado el monto que quiere lavar
    }

    public void setAgenteEspecial(Empleado agenteEspecial) {
        this.agenteEspecial = (AgenteEspecial) agenteEspecial;
    }

    public AgenteEspecial getAgenteEspecial() {
        return agenteEspecial;
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

    public float getBalance() { return balance; }
    public float getDebt() { return deuda; }
    public String getAlias() { return alias; }
}


