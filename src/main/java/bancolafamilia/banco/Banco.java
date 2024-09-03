package bancolafamilia.banco;

import java.util.ArrayList;
import java.util.Optional;

public class Banco {

    private float reservas;
    private ArrayList<User> users;

    public Banco() {
        reservas = 0.0f;
        this.users = new ArrayList<User>();
    }

    public User findUserByUsername(String username) {
        for (User user : users)
            if (user.getUsername().equals(username))
                return user;
        return null;
    }

    public void addUser(User user) {
        // TODO: Revisar que no exista ya un cliente con
        // mismo dni o mismo usuario
        
        users.add(user);
    }
}
