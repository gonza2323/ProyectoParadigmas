package bancolafamilia.banco;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Date;


public class Banco implements IOpBcoCliente {

    private float reservas;
    private float depositos;
    private final ArrayList<User> users;
    private ArrayList<Empleado> empleados;
    private final Queue<Operacion> operacionesPendientes;
    private final Queue<Operacion> operacionesAprobadas;
    //private  Queue<Operacion> operacionesDenegadas; no se si es util

    public Banco() {
        reservas = 0.0f;
        depositos = 0.0f;
        this.users = new ArrayList<User>();
        this.empleados = new ArrayList<>();
        this.operacionesPendientes = new LinkedList<>();
        this.operacionesAprobadas = new LinkedList<>();




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

        if (user instanceof Empleado){ //agregue esto para que se haga la lista de empleados y despues iterar sobre ella en la linea 118
            empleados.add((Empleado) user);
        }
        
        users.add(user);
    }

    public Cliente findClientByAlias(String alias) {
        for (User user : users)
            if (user instanceof Cliente)
                if (((Cliente)user).getAlias().equals(alias.toLowerCase()))
                    return (Cliente)user;
        
        return null;
    }

    //0. METODOS AUXILIARES PARA OPERACIONES ---------------------------------------------------------------------------

    public void agregarOperacion(Operacion operacion){ //metodo de la interfaz Itransacciones
        operacionesPendientes.add(operacion);
    }

    public void aprobarOperacion(Operacion operacion){ //metodo propio
        operacion.aprobar();
    }

    public void procesarOperacion(Empleado empleado){
        while (!operacionesPendientes.isEmpty()){
            Operacion operacion = operacionesPendientes.poll();
            empleado.receptSolicitud(operacion);

            if (operacion.isAprobada() == null){
                operacionesPendientes.add(operacion); // la operacion vuelve a la cola porque no le ha llegado la solicitud al empleado correspondiente
            } else if (operacion.isAprobada()) {
                operacionesAprobadas.add(operacion); //se agrega a la lista de op. aprobadas
            }


        }
    }

    public boolean hayOpEnCola(Queue<Operacion> operacionesPendientes){
        return (!operacionesPendientes.isEmpty());
    }

    //1. TRANSFERENCIAS-------------------------------------------------------------------------------------------------

    public boolean solicitudTransferencia(Cliente sender, Cliente recipient, float amount, String motivo) {
        //le llega la solicitud al banco de transferir dinero

        //1. el bco se asegura que los datos sean correctos
        if (amount < 0 || sender.getBalance() < amount)
            return false;

        //2. el bco crea la operacion
        Transferencia transferencia = new Transferencia(new Date(), sender, recipient, amount, motivo);

        //3. Verifica si el monto permite transferencia inmediata - transeferencias comunes
        if (amount <= 1000000) {
            aprobarOperacion(transferencia); //su estado cambia a aprobada inmediatamente - el bco lo permite porque es el que esta mas alto en la jerarquia
            this.transferMoney(sender, recipient, amount, transferencia);
            return true;
        } else {
            //3.1 agrega la operacion a la cola de op. pendientes de solicitud
            this.agregarOperacion(transferencia);

            //3.2 verfica si hay operaciones en la cola y si hay se las delega a cada empleado - esto no se si va acá
            if (this.hayOpEnCola(operacionesPendientes)) {
                for (Empleado empleado : empleados) {
                    procesarOperacion(empleado); //en este paso el estado de la transferencia cambia
                }
            }
            //3.3 verifica si el gerente ha aprobado la operacion y hace lo necesario
            if (transferencia.isAprobada()) {
                this.transferMoney(sender, recipient, amount, transferencia);
                return true;
            } else {
                return false;
                //3.3.1 lo guarda en operaciones denegadas - no se si es necesario
                //para el gerente el proceso se aprobacion de transferencias esta automatizado porque aprueda todas las tranasferencias especiales que son mayores al limite de transferencia del cliente y menores al monto maximo diario que establece el banco que puede transferir un cliente
                //en operacion.isAprobada() podemos ver directamente el estado
            }

        }
    }


    public void transferMoney(Cliente sender, Cliente recipient, float amount, Operacion transferencia){
        //3.3.1 se hace la transferencia
        transferencia.realizarOperacion(sender, amount);
        //se regista en los movimientos de los dos clientes (sender y recipient)
        sender.agregarOperacion(transferencia);
        recipient.agregarOperacion(transferencia);

    }

    //2. DEPOSITOS -----------------------------------------------------------------------------------------------------

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


}
