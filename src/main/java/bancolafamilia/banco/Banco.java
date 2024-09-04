package bancolafamilia.banco;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        
        List<Cliente> clientes = users.stream()
            .filter(u -> u instanceof Cliente) // Predicate to filter elements
            .map(u -> (Cliente)u)
            .collect(Collectors.toList());

        if (user instanceof Cliente)
            ((Cliente)user).setAlias(AliasGenerator.generateUniqueAlias(clientes));
        
        users.add(user);
    }

    public Cliente findClientByAlias(String alias) {
        for (User user : users)
            if (user instanceof Cliente)
                if (((Cliente)user).getAlias().equals(alias.toLowerCase()))
                    return (Cliente)user;
        
        return null;
    }

    public boolean transferMoney(Cliente sender, Cliente recipient, float amount) {
        
        if (amount < 0 || sender.getBalance() < amount)
            return false;
        
        sender.withdrawFunds(amount);
        recipient.depositFunds(amount);
        return true;
    }
}
