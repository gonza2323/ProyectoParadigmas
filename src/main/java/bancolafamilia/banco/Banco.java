package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import bancolafamilia.banco.Operacion.OpStatus;

/**
 * Clase que mantiene el estado del banco, y los métodos para que
 * la interfaz interactúe con él.
 */
public class Banco implements IOperationProcessor {

    private float reservesTotal;
    private float depositsTotal;
    private float loanedTotal;
    
    private float anualInterestRate;
    private float coeficienteDeEncaje;

    private final ArrayList<User> users;
    private final ArrayList<Empleado> empleados;

    private final Queue<Operacion> operacionesPendientes;
    private final LinkedList<Operacion> operacionesAprobadas;
    private final PriorityQueue<Operacion> operacionesProgramadas;

    private final ArrayList<DocumentoClienteEspecial> listaDocEspecial;
    private final Queue<Client> unattendedPremiumClients;


    public Banco() {
        reservesTotal = 0.0f;
        depositsTotal = 0.0f;
        loanedTotal = 0.0f;
        this.anualInterestRate = 0.08f;
        this.coeficienteDeEncaje = 0.10f;
        this.users = new ArrayList<User>();
        this.empleados = new ArrayList<>();
        this.operacionesPendientes = new LinkedList<>();
        this.operacionesAprobadas = new LinkedList<>();
        this.operacionesProgramadas = new PriorityQueue<>();
        this.listaDocEspecial = new ArrayList<>();
        this.unattendedPremiumClients = new LinkedList<>();
    }

    /**
     * Recibe la fecha actual de la simulación y procesa todas las operaciones
     * en la cola de "pendientes" que debería haber procesado antes de esa fecha
     * 
     * @param currenDateTime Tiempo actual de la simulación
     * @return Retorna si se procesó alguna operación programada
     */
    public boolean procesarOperacionesProgramadas(LocalDateTime currenDateTime) {
        Operacion earliestOperation = operacionesProgramadas.peek();
        boolean hasProcesedOperations = false;

        while (earliestOperation != null && earliestOperation.getDate().isBefore(currenDateTime)) {
            operacionesProgramadas.remove();
            procesarOperacion(earliestOperation);
            hasProcesedOperations = true;
        }

        return hasProcesedOperations;
    }

    private OpStatus procesarOperacion(Operacion operation) {
        
        if (operation.amount <= 0 || operation.status == OpStatus.DENIED) {
            operation.status = OpStatus.DENIED;
            return operation.status;
        }
        
        operation.status = operation.process(this);

        switch (operation.status) {
            case APPROVED:
                operacionesAprobadas.addFirst(operation);
                break;
            case MANUAL_APPROVAL_REQUIRED:
                operacionesPendientes.add(operation);
                break;
            default:
                break;
        }
        return operation.status;
    }

    @Override
    public OpStatus processOperation(Deposito deposit) {
        if (deposit.amount > Deposito.montoMax // 
            && deposit.status != OpStatus.APPROVED)
            return OpStatus.DENIED;
        
        this.reservesTotal += deposit.amount;
        this.depositsTotal += deposit.amount;

        deposit.client.addBalance(deposit.amount);
        return OpStatus.APPROVED;
    }

    @Override
    public OpStatus processOperation(DepositoEspecial deposit) {
        if (deposit.amount > Deposito.montoMax // 
            && deposit.status != OpStatus.APPROVED)
            return OpStatus.DENIED;
        
        this.reservesTotal += deposit.amount;
        this.depositsTotal += deposit.amount;

        deposit.client.addBalance(deposit.amount);
        return OpStatus.DENIED;
    }

    @Override
    public OpStatus processOperation(Retiro withdrawal) {
        if (withdrawal.amount > Retiro.montoMax
            || withdrawal.amount > withdrawal.client.getBalance())
            return OpStatus.DENIED;
        
        this.reservesTotal -= withdrawal.amount;
        this.depositsTotal -= withdrawal.amount;

        withdrawal.client.reduceBalance(withdrawal.amount);
        return OpStatus.DENIED;
    }

    @Override
    public OpStatus processOperation(Transferencia transfer) {
        if (transfer.amount > Transferencia.maxAmount)
            return OpStatus.DENIED;
        
        if (transfer.amount > Transferencia.maxAmountImmediate
            && transfer.status != OpStatus.APPROVED)
            return OpStatus.MANUAL_APPROVAL_REQUIRED;
        
        if (transfer.amount > transfer.client.getBalance())
            return OpStatus.DENIED;
        
        transfer.client.reduceBalance(transfer.amount);
        transfer.recipient.addBalance(transfer.amount);

        return OpStatus.APPROVED;
    }

    @Override
    public OpStatus processOperation(TransferenciaEspecial transfer) {
        if (transfer.amount > Transferencia.maxAmount
            || transfer.client.isPremiumClient() == true
            || clientHasPendingSpecialTransfer(transfer.client))
            return OpStatus.DENIED;
        
        if (transfer.status != OpStatus.APPROVED)
            return OpStatus.MANUAL_APPROVAL_REQUIRED;
        
        transfer.recipient.reduceBalance(transfer.amount);
        transfer.recipient.addBalance(transfer.amount);
        transfer.client.promoteToPremiumClient();
        unattendedPremiumClients.add(transfer.client);

        return OpStatus.DENIED;
    }

    @Override
    public OpStatus processOperation(Prestamo loan) {
        
        float principal = loan.amount;
        float interest = principal * anualInterestRate * loan.getMonths() / 12f;
        float loanTotal = principal + interest;

        if (loan.amount < Prestamo.minimumLoanAmount
            || loan.amount > Prestamo.maxAmount
            || getMaxClientLoan(loan.client) < loan.amount)
            return OpStatus.DENIED;
        
        this.depositsTotal += principal;
        this.loanedTotal += loanTotal;

        loan.client.addBalance(principal);;
        loan.client.addDebt(loanTotal);
        
        return OpStatus.APPROVED;
    }

    public void aprobarOperacionPendiente(Operacion operacion) {
        operacion.aprobar();
        if (operacionesPendientes.remove(operacion))
            procesarOperacion(operacion);
    }

    public void denegarOperacion(Operacion operacion) {
        operacion.denegar();
        operacionesPendientes.remove(operacion);
    }

    public OpStatus solicitudTransferencia(Client sender, Client recipient, float amount, String motivo) {
        Operacion transferencia = new Transferencia(LocalDateTime.now(), sender, recipient, amount, motivo);

        if (Transferencia.isTransferenciaEspecial(recipient)) {
            transferencia = new TransferenciaEspecial(LocalDateTime.now(), sender, recipient, amount, motivo);
        }

        return procesarOperacion(transferencia);
    }

    public OpStatus solicitudPrestamo(Client client, float amount, int months) {
        Operacion loan = new Prestamo(LocalDateTime.now(), client, amount, anualInterestRate, months);
        return procesarOperacion(loan);
    }

    public OpStatus solicitudDeposito(Client client, float amount, Cajero cajero) {
        Deposito deposit = new Deposito(LocalDateTime.now(), client, amount, cajero);
        Deposito approved = cajero.aprobarOperacion(deposit);
        
        if (approved.equals(deposit))
            return procesarOperacion(approved);
        
        procesarOperacion(approved);
        return OpStatus.APPROVED;
    }

    public OpStatus solicitudRetiro(Client client, float amount, Cajero cajero) {
        Operacion withdrawal = new Retiro(LocalDateTime.now(), client, amount, cajero);
        return procesarOperacion(withdrawal);
    }

    //3. DEPOSITOS -----------------------------------------------------------------------------------------------------

    //3.1 METODOS AUXILIARES PARA DEPÓSITOS ILEGITIMOS -----------------------------------------------------------------

    //3.1.2 METODOS PARA EL INTERCAMBIO DE INFO CLIENTE-AGENTE ESPECIAL-------------------------------------------------
    //Estos metodos son llamados desde la AgenteEspecialMenuPage--------------------------------------------------------


    //3.2 METODOS PARA DEPOSITOS EN GRAL -------------------------------------------------------------------------------
    //Este metodo es llamado desde la pagina despositos-----------------------------------------------------------------


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

    // public DocumentoTransaccionEspecial simularTransaccionEspecial(AgenteEspecial agente, DocumentoClienteEspecial documentoCliente){
    //     //este DocumentoClienteEspecial ya tiene la parte dela simulacion asociada
    //     DocumentoClienteEspecial doc = agente.solicitarInfoModuloEspecial(documentoCliente);
    //     listaDocEspecial.add(doc); //el banco agrega el docuemnto a su lista cuando ya esta completo, es decir que tiene los datos de docuemntoCliente  y los datos de documentoTransaccionEspecial
    //     return doc.getDocumentoSimulacion();
    //     //devuelve el documento donde esta la info de la simulacion que debe hacer el agente especial

    // }

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

    public float getMaxClientLoan(Client client) {
        // máximo dispuesto a prestar a ese cliente
        float clientCreditLimit = Math.max((client.getBalance() - 1.10f * client.getDebt()) * 2.00f, 0.0f);
        
        // máximo que puede prestar el banco para poder seguir
        // cubriendo cierto porcentaje de los depósitos
        float maxDeposits = reservesTotal / coeficienteDeEncaje;
        float bankLoanCapacity = Math.max(maxDeposits - depositsTotal, 0.0f);

        return Math.min(clientCreditLimit, bankLoanCapacity); // devolvemos el menor
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

    public List<Cajero> getCajeros(){
        return users.stream()
            .filter(u -> u instanceof Cajero)
            .map(u -> (Cajero)u)
            .collect(Collectors.toList());
    }

    // INFORMACION A CLIENTES EN LA CLIENT MENU PAGE -----------------------------------------------------------------

    public List<Operacion> getOperacionesCliente(Client client) { //agregamos este metodo para sacar la lista de operaciones de cliente
        return operacionesAprobadas.stream()
                .filter(op -> op.getParticipants().contains(client))
                .collect(Collectors.toList());
    }

    public Client findClientByAlias(String alias) {
        for (User user : users)
            if (user instanceof Client)
                if (((Client)user).getAlias().equals(alias.toLowerCase()))
                    return (Client)user;
        return null;
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

    private boolean clientHasPendingSpecialTransfer(Client client) {
        return operacionesPendientes.stream().anyMatch(t -> t.client.equals(client) && t instanceof TransferenciaEspecial);
    }

    public List<Operacion> getOperaciones() { return operacionesAprobadas; }
    public Queue<Operacion> getOperacionesPendientes() { return operacionesPendientes; }
    public PriorityQueue<Operacion> getOperacionesProgramadas() {return operacionesProgramadas; }
    
    public float getReservesTotal() { return reservesTotal; }
    public float getLoanedTotal() { return loanedTotal; }
    public float getDepositsTotal() { return depositsTotal; }
    public float getBalance() { return reservesTotal + loanedTotal - depositsTotal; }
    public List<Empleado> getEmployees() { return empleados; }

    public float getAnualInterestRate() { return anualInterestRate; }
    public Queue<Client> getPendingPremiumClients() { return unattendedPremiumClients; }
}
