package bancolafamilia.banco;

import java.util.ArrayList;

public class Banco {

    private float reservas;
    private ArrayList<User> users;

    public User findUserByUsername(String username) {
        // TODO
        return new User("Pepe Argento", 32021325, "admin", "1234");
    }
}
