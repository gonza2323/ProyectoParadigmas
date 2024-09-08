package bancolafamilia.banco;


public class Cliente extends User {

    public float balance;
    private String alias;

    public Cliente(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }

    public float getBalance() {
        return balance;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
