package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;



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

    // INFORMACION A AGENTES ESPECIALES ------------------------------------------------------------------------------

    public ArrayList<Cajero> cajerosOperativos(ArrayList<Empleado> empleados ){
        ArrayList<Cajero> cajerosOperativos = new ArrayList<>();

        for(Empleado empleado: empleados){
            if (empleado instanceof Cajero){
                cajerosOperativos.add((Cajero) empleado);
            }
        }

        return cajerosOperativos;

    }

    // INFORMACION A CLIENTES EN LA CLIENT MENU PAGE -----------------------------------------------------------------

    public LinkedList<Operacion> getOperacionesCliente(Cliente client){ //agregamos este metodo para sacar la lista de operaciones de cliente

        return operacionesAprobadas.stream()
                .filter(op -> op.getCliente().getNombre().equals(client.getNombre()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    //0. METODOS AUXILIARES PARA OPERACIONES ---------------------------------------------------------------------------

    public void agregarOperacion(Operacion operacion, Queue<Operacion> list){ //metodo de la interfaz Itransacciones
        list.add(operacion);
    }

    public void aprobarOperacion(Operacion operacion){ //metodo propio
        operacion.aprobar();
    }

    public void procesarOperacion(Empleado empleado){
        int longitud = operacionesPendientes.size();
        while (longitud > 0){
            Operacion operacion = operacionesPendientes.poll();
            empleado.recieveSolicitud(operacion);
            longitud -= 1;
            if (operacion.isAprobada() == null){
                agregarOperacion(operacion, operacionesPendientes); // la operacion vuelve a la cola porque no le ha llegado la solicitud al empleado correspondiente
            } else if (operacion.isAprobada()) {
                agregarOperacion(operacion, operacionesAprobadas); //se agrega a la lista de op. aprobadas
                operacionesPendientes.remove(operacion);

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
        Transferencia transferencia = new Transferencia(LocalDateTime.now(), sender, recipient, amount, motivo);

        //3. Verifica si el monto permite transferencia inmediata - transeferencias comunes
        if (amount <= Transferencia.montoInmediata && !isTransferenciaEspecial(recipient)) {
            aprobarOperacion(transferencia); //su estado cambia a aprobada inmediatamente - el bco lo permite porque es el que esta mas alto en la jerarquia
            agregarOperacion(transferencia, operacionesAprobadas);
            this.transferMoney(sender, recipient, amount, transferencia);
            return true;
        } else {
            //3.1 agrega la operacion a la cola de op. pendientes de solicitud
            agregarOperacion(transferencia, operacionesPendientes);

            //3.2 verfica si hay operaciones en la cola y si hay se las delega a cada empleado - esto no se si va acá
            if (this.hayOpEnCola(operacionesPendientes)) {
                for (Empleado empleado : empleados) {
                    procesarOperacion(empleado); //en este paso el estado de la transferencia cambia
                }
            }
            //3.3 verifica si el gerente ha aprobado la operacion y hace lo necesario
            if (transferencia.isAprobada()) {
                this.transferMoney(sender, recipient, amount, transferencia);
                agregarOperacion(transferencia, operacionesAprobadas);
                return true;
            } else {
                return false;
                //3.3.1 lo guarda en operaciones denegadas - no se si es necesario
                //para el gerente el proceso se aprobacion de transferencias esta automatizado porque aprueda todas las tranasferencias especiales que son mayores al limite de transferencia del cliente y menores al monto maximo diario que establece el banco que puede transferir un cliente
                //en operacion.isAprobada() podemos ver directamente el estado
            }

        }
    }

    private boolean isTransferenciaEspecial(Cliente recipient){
        String aliasTransEspecial = "mapa.fiar.oro";
        if (recipient.getAlias().equals(aliasTransEspecial)){
            return true;
        }else{
            return false;
        }

    }

    public void transferMoney(Cliente sender, Cliente recipient, float amount, Operacion transferencia){
        //3.3.1 se hace la transferencia
        transferencia.realizarOperacion(sender, amount);
        //se regista en los movimientos de los dos clientes (sender y recipient)
//        sender.agregarOperacion(transferencia);
//        recipient.agregarOperacion(transferencia);

    }

    //2 RETIROS --------------------------------------------------------------------------------------------------------

    public boolean withdrawFunds(Cliente client, float amount){
        //1. verifica que el cliente tenga saldo +
        if (amount < 0 || client.getBalance() < amount)
            return false;

        //2. crea la operacion
        Retiro retiro = new Retiro(LocalDateTime.now(), client, amount);

        //3. Se aprueban los retiros menores al monto diario establecido por el bco
        if (retiro.getMonto() < Retiro.montoMax) {
            aprobarOperacion(retiro);
            agregarOperacion(retiro, operacionesAprobadas);
            retiro.realizarOperacion(client, amount);
            return true;
        }
        return false; //el monto que desea retira supera el monto max
    }

    //3. DEPOSITOS -----------------------------------------------------------------------------------------------------

    //3.1 METODOS AUXILIARES PARA DEPÓSITOS ILEGITIMOS ----------------------------------------------------------------

    public boolean aprobarTransaccionEspecial(Cliente client, float amount){
        if (amount <= 0 || amount > AgenteEspecial.montoMaxOpEspecial) {
            return false;
        }

        client.setFlagSolicitud(false); //desactivamos el flag para que en la ventana de mensajes del cliente no haya ninguna notificacion
        return true;

    }

    public int cajaTransaccionEspecial(Cliente client, float amount) {

        Cajero cajero = client.agenteEspecial.procesarTransaccionEspecial(client, amount, cajerosOperativos(empleados)); //le envia al agente especial: el cliente, el monto que quiere lavar y la lista de cajero para que se lo envie al cliente
        return cajero.getCaja();

    }

    //3.2 METODOS PARA DEPOSITOS LEGALES -------------------------------------------------------------------------------


    public boolean solicitudDeposito(Cliente client, float amount, int caja) {

        if (amount <= 0) {
            return false;
        }

        Deposito deposito = new Deposito(LocalDateTime.now(), client, amount, caja);

        if (amount <= Deposito.montoInmediato) {
            this.aprobarOperacion(deposito);
            agregarOperacion(deposito, operacionesAprobadas);
            this.depositFunds(client, amount, deposito);
            return false;
        } else {
            agregarOperacion(deposito, operacionesPendientes);
            if (this.hayOpEnCola(operacionesPendientes)) {
                for (Empleado empleado : empleados) {
                    procesarOperacion(empleado); //en este paso el estado de la transferencia cambia
                }
            }

            if (deposito.isAprobada()){
                agregarOperacion(deposito, operacionesAprobadas);
                this.depositFunds(client, amount, deposito);
                return true;
            }
            return false;
        }
    }

    public void depositFunds(Cliente client, float amount, Operacion deposito) {
        deposito.realizarOperacion(client,amount);
        this.reservas += amount;
        this.depositos += amount;
    }
    //hay que actualizar el app.java porque esta usando depositFunds



//    public float getReservas() {
//        return reservas;
//    }



}
