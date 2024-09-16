package bancolafamilia.banco;

import bancolafamilia.banco.Operacion.OpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase que mantiene el estado del banco. Ofrece métodos
 * para acceder a los servicios del banco.
 * Implementa IOperationProcessor, por lo que es capaz de procesar operaciones.
 */
public class Banco implements IOperationProcessor, Serializable {

    private static final long serialVersionUID = 1L;
    private BackupManager backupManager = new BackupManager();
    private float reservesTotal = 0.0f;
    private float depositsTotal = 0.0f;
    private float loanedTotal = 0.0f;
    private float coeficienteDeEncaje = 0.10f;
    private float anualInterestRate = 0.08f;

    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<Empleado> empleados = new ArrayList<>();

    private final Queue<Operacion> operacionesPendientes = new LinkedList<>();
    private final LinkedList<Operacion> operacionesAprobadas = new LinkedList<>();
    private final PriorityQueue<Operacion> operacionesProgramadas = new PriorityQueue<>();

    private final ArrayList<DocumentoClienteEspecial> listaDocEspecial = new ArrayList<>();;
    private final Queue<Client> unattendedPremiumClients = new LinkedList<>();

    // operaciones
    // ========================================================================
    
    /**
     * Solicita una transferencia desde la cuenta del cliente. Crea la operación
     * correspondiente y la manda a procesar. Retorna el estado de la operación luego
     * de ser procesada
     * @param sender Cliente que realiza la transferencia
     * @param recipient Beneficiario de la transferencia
     * @param amount Cantidad a transferir
     * @param motivo Motivo de la transferencia
     * @return Estado de la operación luego de ser solicitada
     */
    public OpStatus solicitudTransferencia(Client sender, Client recipient, float amount, String motivo) {
        Operacion transferencia = new Transferencia(LocalDateTime.now(), sender, recipient, amount, motivo);

        if (Transferencia.isTransferenciaEspecial(recipient)) {
            transferencia = new TransferenciaEspecial(LocalDateTime.now(), sender, recipient, amount, motivo);
        }

        return procesarOperacion(transferencia);
    }

    /**
     * Solicita un préstamo para el cliente. Crea la operación correspondiente
     * y la manda a procesar. Retorna el estado de la operación luego de
     * ser procesada.
     * @param client Cliente que solicita el préstamo
     * @param amount Cantidad del préstamo solicitado
     * @param months Periodo del préstamo en meses
     * @return Estado de la operación luego de ser solicitada
     */
    public OpStatus solicitudPrestamo(Client client, float amount, int months) {
        Operacion loan = new Prestamo(LocalDateTime.now(), client, amount, anualInterestRate, months);
        return procesarOperacion(loan);
    }

    /**
     * Solicita un depósito de la cuenta de un cliente. Crea la operación correspondiente
     * y la manda a procesar. Retorna el estado de la operación luego de
     * ser procesada.
     * @param client Cliente que solicita el depósito
     * @param amount Cantidad a depositar
     * @param cajero Cajero responsable
     * @return Estado de la operación luego de ser solicitada
     */
    public OpStatus solicitudDeposito(Client client, float amount, Cajero cajero) {
        Deposito deposit = new Deposito(LocalDateTime.now(), client, amount, cajero);
        Deposito approved = cajero.aprobarOperacion(deposit);
        
        if (approved.equals(deposit))
            return procesarOperacion(approved);
        
        procesarOperacion(approved);
        return OpStatus.APPROVED;
    }

    /**
     * Solicita un retiro de la cuenta de un cliente. Crea la operación correspondiente
     * y la manda a procesar. Retorna el estado de la operación luego de
     * ser procesada.
     * @param client Cliente que solicita el retiro
     * @param amount Cantidad a retirar
     * @param cajero Cajero responsable
     * @return Estado de la operación luego de ser solicitada
     */
    public OpStatus solicitudRetiro(Client client, float amount, Cajero cajero) {
        Operacion withdrawal = new Retiro(LocalDateTime.now(), client, amount, cajero);
        return procesarOperacion(withdrawal);
    }

    /**
     * Programa las operaciones en la lista recibida.
     * A medida que avanza la simulación del tiempo, se irán procesando en
     * la fecha correcta. Si se insertan operaciones con fecha previa a la
     * actual, se procesarán casi instantáneamente.
     * @param operaciones Operaciones a programar
     */
    public void programarOperaciones(List<Operacion> operaciones) {
        operacionesPendientes.addAll(operaciones);
    }

    /**
     * Aprueba la operación pendiente que reciba.
     * La intenta remover de la lista de operaciones pendientes. Si estaba
     * pendiente, la manda a procesar con estado aprobada.
     * @param operation Operación a aprobar
     */
    public void aprobarOperacionPendiente(Operacion operation) {
        operation.aprobar();
        if (operacionesPendientes.remove(operation))
            procesarOperacion(operation);
    }

    /**
     * Deniega la operación pendiente que reciba.
     * La intenta remover de la lista de operaciones pendientes.
     * @param operation Operación a denegar
     */
    public void denegarOperacion(Operacion operacion) {
        operacion.denegar();
        operacionesPendientes.remove(operacion);
    }

    /**
     * Recibe la fecha actual de la simulación y procesa todas las operaciones
     * en la cola de "pendientes" que debería haber procesado antes de esa fecha
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

    /**
     * Procesa una operación de cualquier tipo
     * <p>Toda operación del banco pasa por este método. Según el resultado del
     * procesado, la operación habrá quedado aprobada, pendiente de aprobación
     * por el gerente, o denegada</p>
     * @param operation Operación a procesar
     * @return Estado de la operación luego de ser procesada
     */
    private OpStatus procesarOperacion(Operacion operation) {
        
        if (operation.amount <= 0 || operation.status == OpStatus.DENIED) {
            operation.status = OpStatus.DENIED;
            return operation.status;
        }
        
        operation.status = operation.process(this);

        switch (operation.status) {
            case APPROVED:
                operation.setDate(LocalDateTime.now());
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

    /*
     * Los siguientes métodos se encargan de los detalles del procesamiento
     * de cada tipo de operación y no deben ser llamados directamente. Solo
     * son llamados desde procesarOperacion(Operacion operation).
     * Sin embargo, deben ser públicos.
     */

    /**
     * Procesa un depósito
     * @param deposit Depósito a procesar
     * @return Estado del depósito luego de ser procesado
     */
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

    private boolean clientHasPendingSpecialTransfer(Client client) {
        return operacionesPendientes.stream().anyMatch(t -> t.client.equals(client) && t instanceof TransferenciaEspecial);
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

    /**
     * Calcula el límite máximo de financiación de un cliente. Tiene en cuenta tanto
     * las finanzas del cliente como las del banco (el banco intenta cubrir siempre
     * un cierto porcentaje de los depósitos).
     * @param client Cliente a analizar
     * @return Límite máximo de financiación del cliente
     */
    public float getMaxClientLoan(Client client) {
        // máximo dispuesto a prestar a ese cliente
        float base = 5000.0f;
        float cashFactor = 0.50f;
        float debtFactor = 0.70f;
        float clientCreditLimit = Math.max(base + cashFactor * client.getBalance() - debtFactor * client.getDebt(), 0.0f);
        
        // máximo que puede prestar el banco para poder seguir
        // cubriendo cierto porcentaje de los depósitos
        float maxDeposits = reservesTotal / coeficienteDeEncaje;
        float bankLoanCapacity = Math.max(maxDeposits - depositsTotal, 0.0f);

        return Math.min(clientCreditLimit, bankLoanCapacity); // devolvemos el menor
    }

    // métodos auxiliares
    // ========================================================================

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

    public List<Operacion> getOperacionesCliente(Client client) {
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

    // getters

    public float getReservesTotal() { return reservesTotal; }
    public float getDepositsTotal() { return depositsTotal; }
    public float getLoanedTotal() { return loanedTotal; }
    public float getBalance() { return reservesTotal + loanedTotal - depositsTotal; }
    public float getAnualInterestRate() { return anualInterestRate; }

    public BackupManager getBackupManager() {
        return this.backupManager;
    }

    public List<Empleado> getEmployees() { return empleados; }

    public Queue<Operacion> getOperacionesPendientes() { return operacionesPendientes; }
    public List<Operacion> getOperaciones() { return operacionesAprobadas; }
    public PriorityQueue<Operacion> getOperacionesProgramadas() {return operacionesProgramadas; }
    
    public Queue<Client> getPendingPremiumClients() { return unattendedPremiumClients; }
}
