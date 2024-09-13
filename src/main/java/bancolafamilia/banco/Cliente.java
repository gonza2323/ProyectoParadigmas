package bancolafamilia.banco;


public class Cliente extends User { //implements IOpBcoCliente

    public float balance;
    public float deuda;
    private String alias;
    public boolean flagSolicitud  = false; //esta variable me va a permitir saber que en el client menu page el agente especial le tiene que preguntar al cliente el monto que quiere lavar
     //en esta variable vamos a almacenar el monto que el cliente podria estar lavando
    public AgenteEspecial agenteEspecial; //el agente especial es intermediario entre cliente y banco

    public Cliente(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);

        this.balance = 0;
        this.deuda = 0;
    }

    public float getBalance() {
        return balance;
    }

    public float getDeuda() {
        return deuda;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setFlagSolicitud(boolean flagSolicitud) {
        this.flagSolicitud = flagSolicitud;
        //cuando cambia a false, el cliente ya ha indicado el monto que quiere lavar
    }

    public void setAgenteEspecial(Empleado agenteEspecial) {
        this.agenteEspecial = (AgenteEspecial) agenteEspecial;
    }
}


