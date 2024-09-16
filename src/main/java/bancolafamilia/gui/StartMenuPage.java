package bancolafamilia.gui;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.stream.Collectors;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.GridLayout.Alignment;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;

import java.util.regex.Pattern;

import bancolafamilia.Simulation;
import bancolafamilia.TimeSimulation;
import bancolafamilia.banco.*;
import bancolafamilia.banco.Operacion.OpStatus;


class StartMenuPage extends PageController<StartMenuView>{

    public StartMenuPage(Banco banco, WindowBasedTextGUI gui, TimeSimulation timeSim) {
        super(banco, new StartMenuView(gui), gui, timeSim);
        
        view.bindBankLoginButton(() -> handleBankLoginButton());
        view.bindGoToBankButton(() -> handleGoToBankButton());
        view.bindShowBankStateButton(() -> handleShowBankStateButton());
        view.bindSimulateOpsButton(() -> handleSimulateOpsButton());
        view.bindExitButton(() -> handleExitButton());
    }

    private void handleBankLoginButton() {
        CambiarPagina(new LoginPage(banco, gui, timeSim));
    }

    private void handleGoToBankButton() {
        List<Client> clients = banco.getClients();
        
        Client client = view.requestClient(clients);

        if (client == null)
            return;
        
        List<Cajero> cajeros = banco.getCajeros();
        List<Integer> cajas = cajeros.stream().map(c -> c.getCaja()).collect(Collectors.toList());
        int caja = view.requestCaja(cajas);
        
        if (caja == -1) {
            return;
        }

        OPERATION_TYPE operationType = view.requestOperationType();

        if (operationType == OPERATION_TYPE.INVALID)
            return;

        String amountStr = null;
        if (operationType == OPERATION_TYPE.DEPOSIT)
            amountStr = view.requestDepositAmount();
        else if (operationType == OPERATION_TYPE.WITHDRAWAL)
            amountStr = view.requestWithdrawalAmount(client.getBalance());
        
        float amount;
        try {
            amount = Float.parseFloat(amountStr);
        } catch (NumberFormatException e) {
            view.showInvalidAmountError();
            return;
        } catch (NullPointerException e) {
            return;
        }

        if (amount <= 0) {
            view.showInvalidAmountError();
            return;
        }

        if (operationType == OPERATION_TYPE.WITHDRAWAL
            && amount > client.getBalance()) {
            view.showInsufficientFundsError();
            return;
        }

        Cajero cajero = cajeros.stream()
                            .filter(c -> c.getCaja() == caja)
                            .findFirst().get();

        Operacion.OpStatus status = null;
        if (operationType == OPERATION_TYPE.DEPOSIT)
            status = banco.solicitudDeposito(client, amount, cajero);
        else if (operationType == OPERATION_TYPE.WITHDRAWAL)
            status = banco.solicitudRetiro(client, amount, cajero);

        if (status == OpStatus.DENIED) {
            view.showTooBigOperationError();
            return;
        }
        
        view.showSuccessMsg();
    }

    private void handleShowBankStateButton() {
        // TODO: Página ir al banco
        CambiarPagina(new StartMenuPage(banco, gui, timeSim));
    }

    private void handleSimulateOpsButton() {

        int numberOfOps;
        try {
            numberOfOps = Integer.parseInt(view.requestNumberOfOps());
        } catch (NumberFormatException e) {
            view.showError();
            return;
        } catch (NullPointerException e) {
            return;
        }
        
        if (numberOfOps < 5 || numberOfOps > 1000) {
            view.showOutOfRangeOpsError();
        }

        Simulation sim = new Simulation(banco, timeSim);
        sim.createSimulation(numberOfOps, 10);
        sim.start();
        // CambiarPagina(new BankStatePage(banco, gui, timeSim)); // TODO
    }

    public void handleExitButton() {
        boolean shouldExit = view.showConfirmExitDialog();

        if (shouldExit)
            CambiarPagina(null);
    }
}


class StartMenuView extends PageView {

    private final Button bankLoginButton;
    private final Button goToBankButton;
    private final Button showBankStateButton;
    private final Button simulateOpsButton;
    private final Button exitButton;

    public StartMenuView(WindowBasedTextGUI gui) {
        super(gui);

        this.bankLoginButton = new Button("Sistema Banco");
        this.goToBankButton = new Button("Ir al banco");
        this.showBankStateButton = new Button("Estado del banco");
        this.simulateOpsButton = new Button(" Simular movimientos ");
        this.exitButton = new Button("Salir");
    }

    public void setupUI() {
        mainWindow.setHints(Arrays.asList(Window.Hint.CENTERED));

        Panel panel = new Panel();
        mainWindow.setComponent(panel);
        
        int horizontalMargin = 3;
        panel.setLayoutManager(
            new GridLayout(1)
                .setVerticalSpacing(1)
                .setLeftMarginSize(horizontalMargin)
                .setRightMarginSize(horizontalMargin));
        
        LayoutData fill = GridLayout.createHorizontallyFilledLayoutData();
        LayoutData center = GridLayout.createLayoutData(Alignment.CENTER, Alignment.CENTER);
        
        panel.addComponent(
            new Label("BANCO LA FAMILIA")
                .setLayoutData(center));
        
        panel.addComponent(
            new Label("Qué quiere hacer?")
                .setLayoutData(center));
        
        bankLoginButton.setLayoutData(fill).addTo(panel);
        goToBankButton.setLayoutData(fill).addTo(panel);
        showBankStateButton.setLayoutData(fill).addTo(panel);
        simulateOpsButton.setLayoutData(fill).addTo(panel);
        panel.addComponent(new EmptySpace());
        exitButton.setLayoutData(fill).addTo(panel);
    }

    public int requestCaja(List<Integer> cajas) {
        ActionListDialogBuilder builder = new ActionListDialogBuilder();

        final Integer[] selectedCaja = { -1 };

        for (Integer caja : cajas) {
            builder.addAction(caja.toString(), () -> {selectedCaja[0] = caja;});
        }

        builder
            .setTitle("A qué caja quiere ir?")
            .build()
            .showDialog(gui);

        return selectedCaja[0];
    }

    public Client requestClient(List<Client> clients) {
        ActionListDialogBuilder builder = new ActionListDialogBuilder();

        final Client[] selectedClient = { null };

        for (Client client : clients) {
            builder.addAction(client.getNombre(), () -> {selectedClient[0] = client;});
        }

        builder
            .setTitle("Qué cliente irá al banco?")
            .build()
            .showDialog(gui);

        return selectedClient[0];
    }

    public OPERATION_TYPE requestOperationType() {
        ActionListDialogBuilder builder = new ActionListDialogBuilder();

        final OPERATION_TYPE[] selectedOperation = { OPERATION_TYPE.INVALID };

        builder.addAction("Depósito", () -> {selectedOperation[0] = OPERATION_TYPE.DEPOSIT;});
        builder.addAction("Retiro", () -> {selectedOperation[0] = OPERATION_TYPE.WITHDRAWAL;});

        builder
            .setTitle("Qué operación desea realizar?")
            .build()
            .showDialog(gui);

        return selectedOperation[0];
    }

    public String requestWithdrawalAmount(float currentBalance) {
        NumberFormat currencyFormater = NumberFormat.getCurrencyInstance(Locale.US);
        
        return new TextInputDialogBuilder()
            .setTitle("RETIROS")
            .setDescription("Ingrese el monto a retirar"
                        +"\nSu balance actual es: " + currencyFormater.format(currentBalance))
            .setValidationPattern(Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$"), "Ingrese un monto válido")
            .build()
            .showDialog(gui);
    }

    public String requestDepositAmount() {
        return new TextInputDialogBuilder()
            .setTitle("DEPÓSITOS")
            .setDescription("Ingrese el monto a depositar")
            .setValidationPattern(Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$"), "Ingrese un monto válido")
            .build()
            .showDialog(gui);
    }

    public boolean showConfirmExitDialog() {
        var response = new MessageDialogBuilder()
            .setTitle("SALIR")
            .setText("Está seguro de que desea salir?")
            .addButton(MessageDialogButton.No)
            .addButton(MessageDialogButton.Yes)
            .build()
            .showDialog(gui);
        
        switch (response) {
            case Yes:
                return true;
            case No:
                return false;
            default:
                return false;
        }
    }

    public String requestNumberOfOps() {
        return new TextInputDialogBuilder()
            .setTitle("SIMULAR OPERACIONES")
            .setDescription("Cuántas operaciones desea simular? (Entre 5 y 1000)")
            .setValidationPattern(Pattern.compile("^[1-9][0-9]*$"), "Ingrese una cantidad de meses válida")
            .build()
            .showDialog(gui);
    }

    public void showOutOfRangeOpsError() {
        showErrorDialog("Ingrese entre 5 y 1000 operaciones");
    }


    public void showInvalidAmountError() {
        showErrorDialog("Monto inválido");
    }

    public void showTooBigOperationError() {
        showErrorDialog("El cajero dice:"
                    +"\nNo le conviene depositar/retirar tanto dinero."
                    +"\nLas autoridades sospecharán de usted!"
                    +"\nConsulte con el asesor financiero para evitar sospechas.");
    }

    public void showSuccessMsg() {
        showMessageDialog(null, "OPERACIÓN EXITOSA");
    }

    public void showInsufficientFundsError() {
        showErrorDialog("Fondos insuficientes");
    }

    public void bindBankLoginButton(Runnable action) {
        bankLoginButton.addListener(bankLoginButton -> action.run());
    }

    public void bindGoToBankButton(Runnable action) {
        goToBankButton.addListener(goToBankButton -> action.run());
    }

    public void bindShowBankStateButton(Runnable action) {
        showBankStateButton.addListener(showBankStateButton -> action.run());
    }

    public void bindSimulateOpsButton(Runnable action) {
        simulateOpsButton.addListener(simulateOpsButton -> action.run());
    }

    public void bindExitButton(Runnable action) {
        exitButton.addListener(closeButton -> action.run());
    }
}

enum OPERATION_TYPE {
    DEPOSIT,
    WITHDRAWAL,
    INVALID
}
