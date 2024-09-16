package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class Banco implements IOpBcoCliente {

    private float reserves;
    private float deposits;
    private float prestamos;
    public AgenteBolsa broker;
    
    private float anualInterestRate;
    private float coeficienteDeEncaje;

    private final ArrayList<User> users;
    private ArrayList<Empleado> empleados;

    private final Queue<Operacion> operacionesPendientes;
    private final LinkedList<Operacion> operacionesAprobadas;
    private final PriorityQueue<Operacion> operacionesProgramadas;
    private final ArrayList<TransaccionBolsa> transaccionesBolsa;
    //Con transaccionesBolsa sabemos la fecha y hora y la info de cada una de las op de compra-venta de activos

    private final ArrayList<DocumentoClienteEspecial> listaDocEspecial;
    private final ArrayList<DocumentoInversionBolsa> inversionesPerClient;
    //Con inversionesPerClient vamos registrando la info sobre la cantidad de acciones, monto y comisiones de un mismo activo del cliente

    
    public Banco() {
        reserves = 0.0f;
        deposits = 0.0f;
        prestamos = 0.0f;
        this.anualInterestRate = 0.08f;
        this.coeficienteDeEncaje = 0.10f;
        this.users = new ArrayList<User>();
        this.empleados = new ArrayList<>();
        this.operacionesPendientes = new LinkedList<>();
        this.operacionesAprobadas = new LinkedList<>();
        this.operacionesProgramadas = new PriorityQueue<>();
        this.transaccionesBolsa = new ArrayList<>();
        this.listaDocEspecial = new ArrayList<>();
        this.inversionesPerClient = new ArrayList<>();
    }

    public void setBroker(AgenteBolsa broker) {
        this.broker = broker;
    }

    public User findUserByUsername(String username) {
        for (User user : users)
            if (user.getUsername().equals(username))
                return user;
        return null;
    }

    public List<Client> getClients() {
        return users.stream()
            .filter(u -> u instanceof Client)
            .map(u -> (Client)u)
            .collect(Collectors.toList());
    }

    public void addUser(User user) {
        // TODO: Revisar que no exista ya un cliente con
        // mismo dni o mismo usuario
        List<Client> clientes = users.stream()
            .filter(u -> u instanceof Client)
            .map(u -> (Client)u)
            .collect(Collectors.toList());

        if (user instanceof Client)
            ((Client)user).setAlias(AliasGenerator.generateUniqueAlias(clientes));

        if (user instanceof Empleado){ //agregue esto para que se haga la lista de empleados y despues iterar sobre ella en la linea 118
            empleados.add((Empleado) user);
        }
        
        users.add(user);
    }

    public Client findClientByAlias(String alias) {
        for (User user : users)
            if (user instanceof Client)
                if (((Client)user).getAlias().equals(alias.toLowerCase()))
                    return (Client)user;
        
        return null;
    }

    // INFORMACION A AGENTES ESPECIALES ------------------------------------------------------------------------------

    public List<Cajero> getCajeros(){
        return users.stream()
            .filter(u -> u instanceof Cajero)
            .map(u -> (Cajero)u)
            .collect(Collectors.toList());
    }

    // INFORMACION A CLIENTES EN LA CLIENT MENU PAGE -----------------------------------------------------------------

    public List<Operacion> getOperacionesCliente(Client client){ //agregamos este metodo para sacar la lista de operaciones de cliente
        return operacionesAprobadas.stream()
                .filter(op -> op.getParticipants().contains(client))
                .collect(Collectors.toList());
    }

    //0. METODOS AUXILIARES PARA OPERACIONES ---------------------------------------------------------------------------

    public void addOperation(Operacion operacion){ //metodo de la interfaz Itransacciones
        operacionesAprobadas.add(operacion);
    }

    public void approveOperation(Operacion operacion){ //metodo propio
        operacion.aprobar();
    }

    public void procesarOperacion(Empleado empleado){
        int longitud = operacionesPendientes.size();
        while (longitud > 0){
            Operacion operacion = operacionesPendientes.poll();
            empleado.recieveSolicitud(operacion);
            longitud -= 1;
            if (operacion.isAprobada() == null){
                operacionesPendientes.add(operacion); // la operacion vuelve a la cola porque no le ha llegado la solicitud al empleado correspondiente
            } else if (operacion.isAprobada()) {
                addOperation(operacion); //se agrega a la lista de op. aprobadas
                operacionesPendientes.remove(operacion);
            }
        }
    }

    public boolean hayOpEnCola(Queue<Operacion> operacionesPendientes){
        return (!operacionesPendientes.isEmpty());
    }

    //1. TRANSFERENCIAS-------------------------------------------------------------------------------------------------

    public boolean solicitudTransferencia(Client sender, Client recipient, float amount, String motivo) {
        //le llega la solicitud al banco de transferir dinero

        //1. el bco se asegura que los datos sean correctos
        if (amount <= 0 || sender.getBalance() < amount)
            return false;

        //2. el bco crea la operacion
        Transferencia transferencia = new Transferencia(LocalDateTime.now(), sender, recipient, amount, motivo);

        //3. Verifica si el monto permite transferencia inmediata - transeferencias comunes
        if (amount <= Transferencia.montoInmediata && !isTransferenciaEspecial(recipient)) {
            approveOperation(transferencia); //su estado cambia a aprobada inmediatamente - el bco lo permite porque es el que esta mas alto en la jerarquia
            addOperation(transferencia);
            this.transferMoney(transferencia);
            return true;
        } else {
            //3.1 agrega la operacion a la cola de op. pendientes de solicitud
            operacionesPendientes.add(transferencia);

            //3.2 verfica si hay operaciones en la cola y si hay se las delega a cada empleado - esto no se si va acá
            if (this.hayOpEnCola(operacionesPendientes)) {
                for (Empleado empleado : empleados) {
                    procesarOperacion(empleado); //en este paso el estado de la transferencia cambia
                }
            }
            //3.3 verifica si el gerente ha aprobado la operacion y hace lo necesario
            if (transferencia.isAprobada()) {
                this.transferMoney(transferencia);
                addOperation(transferencia);
                return true;
            } else {
                return false;
                //3.3.1 lo guarda en operaciones denegadas - no se si es necesario
                //para el gerente el proceso se aprobacion de transferencias esta automatizado porque aprueda todas las tranasferencias especiales que son mayores al limite de transferencia del cliente y menores al monto maximo diario que establece el banco que puede transferir un cliente
                //en operacion.isAprobada() podemos ver directamente el estado
            }

        }
    }

    private boolean isTransferenciaEspecial(Client recipient){
        String aliasTransEspecial = "mapa.fiar.oro";
        return recipient.getAlias().equals(aliasTransEspecial);
    }

    public void transferMoney(Operacion transferencia){
        //3.3.1 se hace la transferencia
        transferencia.realizarOperacion();

    }

    //2 RETIROS --------------------------------------------------------------------------------------------------------
    //Este metodo es llamado desde la pag de retiros -------------------------------------------------------------------

    public boolean withdrawFunds(Client client, float amount, Cajero cajero) {

        if (amount <= 0 || amount > client.getBalance() || amount > Retiro.montoMax)
            return false;

        Retiro withdrawal = new Retiro(LocalDateTime.now(), client, amount, cajero);

        approveOperation(withdrawal);
        addOperation(withdrawal);
        withdrawal.realizarOperacion();

        reserves -= amount;
        deposits -= amount;
        
        return true;
    }

    //3. DEPOSITOS -----------------------------------------------------------------------------------------------------

    //3.1 METODOS AUXILIARES PARA DEPÓSITOS ILEGITIMOS -----------------------------------------------------------------

    //3.1.2 METODOS PARA EL INTERCAMBIO DE INFO CLIENTE-AGENTE ESPECIAL-------------------------------------------------
    //Estos metodos son llamados desde la AgenteEspecialMenuPage--------------------------------------------------------

    public boolean aprobarTransaccionEspecial(Client client, float amount){
        if (amount <= 0 || amount > AgenteEspecial.montoMaxOpEspecial) {
            return false;
        }
        return true;

    }

    public int cajaTransaccionEspecial(Client client, float amount) {
        Cajero cajero = client.agenteEspecial.procesarTransaccionEspecial(client, amount, getCajeros()); //le envia al agente especial: el cliente, el monto que quiere lavar y la lista de cajero para que se lo envie al cliente
        return cajero.getCaja();
    }

    //3.2 METODOS PARA DEPOSITOS EN GRAL -------------------------------------------------------------------------------
    //Este metodo es llamado desde la pagina despositos-----------------------------------------------------------------

    public boolean solicitudDeposito(Client client, float amount, int caja, Cajero cajero) {

        if (amount <= 0) {
            return false;
        }

        Deposito deposito = new Deposito(LocalDateTime.now(), client, amount, caja, cajero);

        //1. Si el deposito no supera el límite entonces es aprobado
        if (amount <= Deposito.montoInmediato) {
            this.approveOperation(deposito);
            addOperation(deposito);
            this.depositFunds(deposito);
            return true;
        } else {
            //2. Si supera el limite, se lo manda al cajero para que lo apruebe
            operacionesPendientes.add(deposito);
            if (this.hayOpEnCola(operacionesPendientes)) {
                for (Empleado empleado : empleados) {
                    procesarOperacion(empleado); //en este paso el estado de la transferencia cambia
                }
            }
            //3. Si es aprobado por el cajero entonces se realiza el deposito
            if (deposito.isAprobada()){
                addOperation(deposito);
                //3.1 Si el deposito lo hace un mafioso:
                //client es el mafioso
                //la cuenta a donde cae el dinero es deposito.getCliente() porque cuando el cajero aprueba la op del deposito de dinero ilegitimo, este setea el cliente en la op para que le caiga el dinero al agente y no al mafioso
                this.depositFunds(deposito);
                return true;
            }
            return false;
        }
    }

    public void depositFunds(Operacion deposito) {
        deposito.realizarOperacion();
        this.reserves += deposito.getAmount();
        this.deposits += deposito.getAmount();
    }

    //3.3 LAVADO DE DINERO ------------------------------------------------------------------------------------------------
    //Estos metodos son llamados desde la AgenteEspecialMenuPage -------------------------------------------------------

    //el agente especial tiene una pestaña de notificaciones en la que se le informa los clientes que ya han depositado para que revise su cuenta e inicie el proceso de lavado

    public ArrayList<DocumentoClienteEspecial> notificacionFondosAgenteE(AgenteEspecial agente){
        ArrayList<DocumentoClienteEspecial> listaDocumentos = new ArrayList<>();
        for (DocumentoClienteEspecial doc: agente.getActivosEnProceso()){
            if (doc.inProcess){
                listaDocumentos.add(doc);
            }
        }
        return listaDocumentos;
        //con esta lista le muestra al agente el cliente y el monto que deposito el cliente
    }

    public DocumentoTransaccionEspecial simularTransaccionEspecial(AgenteEspecial agente, DocumentoClienteEspecial documentoCliente){
        //este DocumentoClienteEspecial ya tiene la parte dela simulacion asociada
        DocumentoClienteEspecial doc = agente.solicitarInfoModuloEspecial(documentoCliente);
        listaDocEspecial.add(doc); //el banco agrega el docuemnto a su lista cuando ya esta completo, es decir que tiene los datos de docuemntoCliente  y los datos de documentoTransaccionEspecial
        return doc.getDocumentoSimulacion();
        //devuelve el documento donde esta la info de la simulacion que debe hacer el agente especial

    }

    public void programarOpEspecial(DocumentoClienteEspecial doc){
        int dias = doc.getDocumentoSimulacion().getTiempoDias();
        int cantTransfDiarias = doc.getDocumentoSimulacion().getNumTransferenciasDiarias();
        float monto = doc.getDocumentoSimulacion().getMontoMaxDiario();
        Client client = doc.getClient();
        LocalDateTime fechaActual = LocalDateTime.now();


        for (int i = 0; i< dias; i++){
            for (int j = 0; j < cantTransfDiarias; j++){
                //le puse que el cliente sea null porque como es una transferencia no rastreable no sabemos de que cuenta llega el dinero pero si sabemos a que cuenta entra el dinero!
                Transferencia transNoRastreable = new Transferencia(fechaActual.plusDays(1), null, client, monto, "no rastreable");
                operacionesProgramadas.add(transNoRastreable);
            }
            //DESPUES LAS OPERACIONES VAN DE LA COLA DE OPERACIONES PROGRAMADAS DIRECTAMEMTE AL METODO transferMoney(transferencia)

        }
    }

    public float getAnualInterestRate() {
        return anualInterestRate;
    }

    public float getMaxClientLoan(Client client) {
        // máximo dispuesto a prestar a ese cliente
        float clientCreditLimit = Math.max((client.getBalance() - client.getDebt()) * 1.50f, 0.0f);
        
        // máximo que puede prestar el banco según sus reservas
        float maxDeposits = reserves / coeficienteDeEncaje;
        float bankLoanCapacity = Math.max(maxDeposits - deposits, 0.0f);

        return Math.min(clientCreditLimit, bankLoanCapacity);
    }

    public boolean requestLoan(Client client, float loanPrincipal, int months) {
        float minimumLoanAmount = 1000;
        
        float interest = loanPrincipal * anualInterestRate * months / 12f;
        float loanTotal = loanPrincipal + interest;
        
        if (getMaxClientLoan(client) < loanPrincipal
            || loanPrincipal < minimumLoanAmount)
            return false;
        
        if (loanPrincipal <= 0) {
            return false;
        }

        Prestamo loanOperation = new Prestamo(LocalDateTime.now(), client, loanPrincipal, anualInterestRate, months);

        // aprobar operación
        approveOperation(loanOperation);
        addOperation(loanOperation);
        
        // actualizamos finanzas del cliente
        client.balance += loanPrincipal;
        client.addDebt(loanTotal);
        
        // actualizamos finanzas del banco
        deposits += loanPrincipal;
        prestamos += loanTotal;
        
        return true;
    }

    public boolean procesarOperacionesProgramadas(LocalDateTime currenDateTime) {
        Operacion earliestTransaction = operacionesProgramadas.poll();
        boolean hasProcesedOperations = false;

        while (earliestTransaction != null
            && earliestTransaction.getDate().isBefore(currenDateTime)) {
            earliestTransaction.realizarOperacion();
            hasProcesedOperations = true;
        }

        return hasProcesedOperations;
    }

    public List<Operacion> getOperaciones() { return operacionesAprobadas; }
    
    public float getLoans() { return prestamos; }
    public float getDeposits() { return deposits; }
    public float getReserves() { return reserves; }
    public float getBalance() { return reserves + prestamos - deposits; }


    //OPERACIONES CON EL AGENTE DE BOLSA -------------------------------------------------------------------------------

    public boolean operarEnLaBolsa(DocumentoInversionBolsa doc, Client client, Activo activo, int cantidad, String tipo){
        if ((client.getBalance() < (activo.getValue() * cantidad) + broker.comissionRate) && (tipo.equalsIgnoreCase("buy"))) {
            return false;

        }else{

            DocumentoInversionBolsa inversion = getInversionPerClient(activo, client);


            if (inversion == null){inversionesPerClient.add(doc);}; //agregamos a la lista el documento de la simulacion pq se aprobo la compra

            TransaccionBolsa transaccion = broker.operar(inversion, client, activo, cantidad, tipo);

            if (transaccion.getTipo().equalsIgnoreCase("buy")){
                client.balance -= (transaccion.getAmount() + transaccion.getComision());
                transaccionesBolsa.add(transaccion);

            }else{

                client.balance += (transaccion.getAmount() + doc.getGanancias() - transaccion.getComision());

            }
            return true;
        }

    }

    public DocumentoInversionBolsa getInversionPerClient(Activo activo, Client client) {

        for (DocumentoInversionBolsa inversion : inversionesPerClient){
            if (inversion.getClient().equals(client) && inversion.getActivo().getName().equalsIgnoreCase(activo.getName())){
                return inversion;

            }
        }
        return null;
    }

    public financialAdvice solicitudAsesoriaFinanciera(Client client, String tipo){
        List<AsesorFinanciero> asesoresFinancieros = new ArrayList<>();
        financialAdvice advice;

        for (Empleado empleado: empleados){
            if (empleado instanceof AsesorFinanciero){
                asesoresFinancieros.add((AsesorFinanciero) empleado);
            }
        }

        //elige un asesor al azar y le envia la solicitud de consejo
        Random random = new Random();
        int randomIndex = random.nextInt(asesoresFinancieros.size());
        AsesorFinanciero asesor = asesoresFinancieros.get(randomIndex);

        if (tipo.equalsIgnoreCase("premium")){
            advice = asesor.asesorarClientePremiun(client);
        } else {
            advice = asesor.asesorarCliente(client);
        }

        return advice;
    }
}

