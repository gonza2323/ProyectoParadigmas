package bancolafamilia.banco;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bancolafamilia.TimeSimulation;


/**
 * Permite simular y ejecutar operaciones de lavado de dinero
 */
public class ModuloDeRecirculacionFinanciera implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Banco banco;

    private final static float maxDailyAmount = 5000000;
    private final static float maxAmountPerTransfer = Transferencia.maxAmountImmediate;
    
    private final float amount;

    public ModuloDeRecirculacionFinanciera(float montoAOperar, Banco banco) {
        this.banco = banco;
        this.amount = montoAOperar;
    }

    //1. Calucla el tiempo minimo en dias en que se puede lavar la plata
    private int calcularPlazoEnDias() {
        float plazoOperacion = amount / maxDailyAmount;
        //math.ceil redondea hacia arriba el resultado de plazoOp
        return (int) Math.ceil(plazoOperacion);
    }

    private int calculateTotalTransfers() {
        int daysAtFullCapacity = (int) Math.floor(amount / maxDailyAmount);
        float remainingMoney = amount / maxDailyAmount - daysAtFullCapacity * maxDailyAmount;
        int remainingTransfers = (int) Math.ceil(remainingMoney / maxAmountPerTransfer);
        
        return daysAtFullCapacity * calculateMaxDailyTransfers() + remainingTransfers;
    }

    private int calculateMaxDailyTransfers() {
        return (int) Math.ceil(maxDailyAmount / maxAmountPerTransfer);
    }

    public SimulacionDeRecirculacion simulate(){
        return new SimulacionDeRecirculacion(
            amount,
            calcularPlazoEnDias(),
            calculateMaxDailyTransfers(),
            maxDailyAmount,
            calculateTotalTransfers()
        );
    }

    public SimulacionDeRecirculacion execute(Client client, AgenteEspecial agent) {
        int daysAtFullCapacity = (int) Math.floor(amount / maxDailyAmount);
        float remainingMoneyOnLastDay = amount % maxDailyAmount;

        int amountFullTransfersOnFullDays = (int) Math.floor(maxDailyAmount / maxAmountPerTransfer);
        float remainingMoneyOnFullDays = maxDailyAmount % maxAmountPerTransfer;
        
        int fullCapacityTransfersLastDay = (int) Math.floor(remainingMoneyOnLastDay / maxAmountPerTransfer);
        float lastRemainingMoney = remainingMoneyOnLastDay % maxAmountPerTransfer;

        List<Operacion> operaciones = new LinkedList<Operacion>();
        
        LocalDateTime date = TimeSimulation.getTime().plusDays(1);
        String motivo = "Dividendos";
        
        for (int i = 0; i < daysAtFullCapacity; i++) {
            for (int j = 0; j < amountFullTransfersOnFullDays; j++) {
                Transferencia transfer = new TransferenciaInternacional(date, agent.getCtaCliente(), client, maxAmountPerTransfer, motivo);
                operaciones.add(transfer);
            }
            if (remainingMoneyOnFullDays > 0) {
                Transferencia transfer = new TransferenciaInternacional(date, agent.getCtaCliente(), client, remainingMoneyOnFullDays, motivo);
                operaciones.add(transfer);
            }
            date = date.plusDays(1);
        }

        if (remainingMoneyOnLastDay > 0) {
            for (int i = 0; i < fullCapacityTransfersLastDay; i++) {
                Transferencia transfer = new TransferenciaInternacional(date, agent.getCtaCliente(), client, maxAmountPerTransfer, motivo);
                operaciones.add(transfer);
            }
            if (lastRemainingMoney > 0) {
                Transferencia transfer = new TransferenciaInternacional(date, agent.getCtaCliente(), client, lastRemainingMoney, motivo);
                operaciones.add(transfer);
            }
        }

        banco.programarOperaciones(operaciones);
        return simulate();
    }
}
