package bancolafamilia.banco;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Date;


public class Banco {

    private float reservas;
    private float depositos;
    private final ArrayList<User> users;
    private final Map<Class<? extends Operacion>, List<Operacion>> mapaOperaciones; //Map es una estructura que asocia claves con valores en este caso la key es la subclase de la clase operacion y el value es una lista de instancias de la subclase de la clase operacion - con esto va a ser mas facil buscar operaciones


    public Banco() {
        reservas = 0.0f;
        depositos = 0.0f;
        this.users = new ArrayList<User>();
        this.mapaOperaciones = new HashMap<>();
        crearMapaOperaciones();


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
            .filter(u -> u instanceof Cliente)
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

        sender.balance -= amount;
        recipient.balance += amount;

        Transferencia transferencia = new Transferencia(new Date(), sender, recipient, amount, "done");

        mapaOperaciones.get(Transferencia.class).add(transferencia); //me falta aplicar el metodo para las otras operaciones y para buscarlas

        return true;
    }

    public boolean depositFunds(Cliente client, float amount) {
        if (amount < 0)
            return false;
        
        client.balance += amount;
        this.reservas += amount;
        this.depositos += amount;

        return true;
    }

    public float getReservas() {
        return reservas;
    }

    public void crearMapaOperaciones(){
        mapaOperaciones.put(Transferencia.class, new ArrayList<>());
        mapaOperaciones.put(Deposito.class, new ArrayList<>());
        mapaOperaciones.put(Retiro.class, new ArrayList<>());
        mapaOperaciones.put(Prestamo.class, new ArrayList<>());


    }
}
