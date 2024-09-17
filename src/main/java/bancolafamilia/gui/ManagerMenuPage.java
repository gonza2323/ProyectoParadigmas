package bancolafamilia.gui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.*;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LayoutData;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.Separator;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import bancolafamilia.TimeSimulation;
import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Operacion;
import bancolafamilia.banco.Gerente;

public class ManagerMenuPage extends PageController<ManagerMenuView>{
    private Gerente manager;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public ManagerMenuPage(Banco banco, WindowBasedTextGUI gui, Gerente manager, TimeSimulation timeSim) {
        super(banco, new ManagerMenuView(gui), gui, timeSim);

        this.manager = manager;
        
        view.updateName(manager.getNombre());
        view.updateReserves(banco.getReservesTotal());
        view.updateDeposits(banco.getDepositsTotal());
        view.updateLoans(banco.getLoanedTotal());
        view.updateBalance(banco.getBalance());

        view.bindHistoryButton(() -> handleHistoryButton());
        view.bindPendingOperationsButton(() -> handlePendingOperationsButton());
        view.bindShowClientsButton(() -> handleShowClientsButton());
        view.bindShowEmployeesButton(() -> handleShowEmployeesButton());
        view.bindShowSpecialAgentsButton(() -> handleShowSpecialAgentsButton());
        view.bindShowDocumentsButton(() -> handleShowDocumentsButton());
        view.bindExitButton(() -> handleExitButton());

        view.bindSelectPendingOperation(handleSelectPendingOperation);
    }

    private void handleHistoryButton() {
        view.showHistory(banco.getOperaciones());
    }

    private void handlePendingOperationsButton() {
        List<Operacion> operations = new ArrayList<>(banco.getOperacionesPendientes());
        view.showPendingOperations(operations);
    }

    private Function<Operacion, Boolean> handleSelectPendingOperation = (Operacion operation) -> {
        Boolean shouldApprove = view.requestOperationAction();

        if (shouldApprove == null)
            return false;
        
        if (shouldApprove)
            banco.aprobarOperacionPendiente(operation);
        else
            banco.denegarOperacionPendiente(operation);
        return true;
    };

    private void handleShowClientsButton() {
        view.showClients(banco.getClients());
    }
    
    private void handleShowEmployeesButton() {
        view.showEmployees(banco.getEmployees());
    }

    private void handleShowSpecialAgentsButton() {
        return;
    }
    
    private void handleShowDocumentsButton() {
        return;
    }

    @Override
    public void update() {
        view.updateReserves(banco.getReservesTotal());
        view.updateDeposits(banco.getDepositsTotal());
        view.updateLoans(banco.getLoanedTotal());
        view.updateBalance(banco.getBalance());
    }

    private void handleExitButton() {
        CambiarPagina(new LoginPage(banco, gui, timeSim));;
    }
}
