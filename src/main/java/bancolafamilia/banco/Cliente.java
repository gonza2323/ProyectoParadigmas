package bancolafamilia.banco;

import java.util.*;

public class Cliente extends User {

    private float balance;
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

    public void depositFunds(float amount) {
        if (amount < 0.0f)
            return;
        
        balance += amount;
    }

    public void withdrawFunds(float amount) {
        if (amount < 0.0f)
            return;
        
        balance -= amount;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
