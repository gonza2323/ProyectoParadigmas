package bancolafamilia.banco;

public class Cliente extends User {

    private float balance;

    public Cliente(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }

    public float getBalance() {
        return balance;
    }
}
