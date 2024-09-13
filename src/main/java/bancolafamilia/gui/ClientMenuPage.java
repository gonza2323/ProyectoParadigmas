package bancolafamilia.gui;


import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Cliente;
import bancolafamilia.banco.Operacion;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.table.Table;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Pattern;

import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

public class ClientMenuPage extends PageController<ClientMenuView>{
    private Cliente client;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public ClientMenuPage(Banco banco, WindowBasedTextGUI gui, Cliente client) {
        super(banco, new ClientMenuView(gui, client.getNombre()), gui);

        this.client = client;

        view.updateBalance(client.getBalance());
        view.bindTransferButton(() -> handleTransferButton());
        view.bindHistoryButton(() -> handleHistoryButton());
        view.bindAliasButton(() -> handleAliasButton());
        view.bindLoanButton(() -> handleLoanButton());
        view.bindBrokerButton(() -> handleBrokerButton());
        view.bindAdviceButton(() -> handleAdviceButton());
        view.bindExitButton(() -> handleExitButton());
    }

    private void handleTransferButton() {
        String alias = view.requestAlias();

        if (alias == null)
            return;

        Cliente recipient = banco.findClientByAlias(alias);

        if (recipient == null) {
            view.showNonExistantAliasError();
            return;
        }

        if (recipient == client) {
            view.showCantTransferToSelfError();
            return;
        }

        String motivo = view.requestMotivo();

        float amount;
        try {
            amount = Float.parseFloat(view.requestAmount());
        } catch (NumberFormatException e) {
            return;
        } catch (NullPointerException e) {
            return;
        }

        if (client.getBalance() < amount) {
            view.showInsufficientFundsError();
            return;
        }


        boolean success = banco.solicitudTransferencia(client, recipient, amount, motivo);

        if (success) {
            view.updateBalance(client.getBalance());
            view.showSuccessMsg();
        } else {
            view.showTransferError();
        }
    }

    private void handleHistoryButton() {
        //view.showHistory(client.getOperaciones());
        view.showHistory(banco.getOperacionesCliente(client));

    }

    private void handleAliasButton() {
        // TODO Auto-generated method stub
        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar CONSULTA ALIAS");
    }

    private void handleLoanButton() {
        // TODO Auto-generated method stub
        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar PRESTAMOS");
    }

    private void handleBrokerButton() {
        // TODO Auto-generated method stub
        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar BOLSA");
    }

    private void handleAdviceButton() {
        // TODO Auto-generated method stub
        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar ASESORAMIENTO");
    }

    private void handleExitButton() {
        CambiarPagina(new LoginPage(banco, gui));;
    }


}

//cuando la clase no tiene modificador de acceso, el acceso por defecto es el "acceso por paquete" o package private lo que quiere decir que la clase solo sera accesible dentro del mismo paquete. O sea, solo puede acceder a ella la clase clientMenuPage
class ClientMenuView extends PageView {
    
    private final String name;
    private final Label balanceIndicator;

    private final Button transferButton;
    private final Button historyButton;
    private final Button aliasButton;
    private final Button loanButton;
    private final Button brokerButton;
    private final Button adviceButton;
    private final Button exitButton;
    //private final Button changePasswordButton;
    

    public ClientMenuView(WindowBasedTextGUI gui, String name) {
        super(gui);
        this.name = name;
        this.balanceIndicator = new Label("");
        
        transferButton = new Button("Transferencia");
        historyButton = new Button("Movimientos");
        aliasButton = new Button("Consultar alias");
        loanButton = new Button("Préstamos");
        brokerButton = new Button("Operar en bolsa");
        adviceButton = new Button("Asesoramiento");
        //changePasswordButton = new Button("Cambiar contraseña");
        exitButton = new Button("Salir");
    }

    public void startUI() {
        // Crea una ventana y le dice que se centre
        BasicWindow window = new BasicWindow("BANCO LA FAMILIA");
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN,
                                      Window.Hint.FIT_TERMINAL_WINDOW,
                                      Window.Hint.NO_DECORATIONS));

        // Panel
        Panel panel = new Panel(
            new GridLayout(2)
                .setHorizontalSpacing(1)
                .setVerticalSpacing(1));
        window.setComponent(panel); // IMPORTANTE, si no, no se va a dibujar nada y termina el programa.
        
        // configuración layout
        LayoutData horizontalFill = GridLayout.createHorizontallyFilledLayoutData(2);
        
        // Mensaje de bienvenida
        Label welcomeMsg = new Label("Bienvenido: " + name);
        welcomeMsg.setLayoutData(horizontalFill);
        panel.addComponent(welcomeMsg);

        // Balance
        balanceIndicator.setLayoutData(horizontalFill);
        panel.addComponent(balanceIndicator);

        // Añadimos linea separadora horizontal
        panel.addComponent(
            new Separator(Direction.HORIZONTAL)
                .setLayoutData(horizontalFill));
        
        // layout botones
        LayoutData leftJustify = GridLayout.createLayoutData(
            GridLayout.Alignment.BEGINNING,
            GridLayout.Alignment.BEGINNING,
            true,
            false,
            2,
            1);

        // Añadimos los botones creados en el constructor
        panel.addComponent(
            transferButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            historyButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            aliasButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            loanButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            brokerButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            adviceButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            exitButton
                .setLayoutData(leftJustify));

        // Agregar ventana a la gui
        gui.addWindowAndWait(window);
    }

    public void showHistory(LinkedList<Operacion> operaciones){

        BasicWindow window = new BasicWindow("Historial de Operaciones");
        window.setHints(
            Arrays.asList(
                Window.Hint.CENTERED,
                Window.Hint.FIT_TERMINAL_WINDOW));
        window.setCloseWindowWithEscape(true);
        
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        window.setComponent(panel);
        
        Table<Object> table = new Table<>("Fecha", "Tipo", "Monto", "Detalle");
        panel.addComponent(table);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);

        for (Operacion op : operaciones) {
            String clase = op.getClass().getSimpleName();
            String fecha = op.getFecha().format(formatter);
            String description = op.getDescription();
            String monto = decimalFormat.format(op.getMonto());

            table.getTableModel().addRow(fecha, clase, monto, description);
        }


        panel.addComponent(new Button("Cerrar",
            new Runnable() {
                @Override
                public void run() {
                    window.close();
                }
        }).setLayoutData(
            GridLayout.createLayoutData(
                GridLayout.Alignment.END,
                GridLayout.Alignment.CENTER))
        );

        gui.addWindowAndWait(window);
    }

    public void updateBalance(float balance) {
        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);
        balanceIndicator.setText(decimalFormat.format(balance));
    }

    public String requestAlias() {
        return new TextInputDialogBuilder()
            .setTitle("TRANSFERENCIA")
            .setDescription("Ingrese el alias del destinatario")
            .setValidationPattern(Pattern.compile("^[a-zA-Z]+(\\.[a-zA-Z]+)*$"), "Ingrese un alias válido")
            .build()
            .showDialog(gui);
    }

    public String requestMotivo() {
        return new TextInputDialogBuilder()
                .setTitle("Descripcion Transferencia")
                .setDescription("Ingrese una breve descripción")
                .setValidationPattern(Pattern.compile("^([a-zA-Z]+\\s?){1,5}$"), "Maximo 5 palabras")
                .build()
                .showDialog(gui);
    }    

    public String requestAmount() {
        return new TextInputDialogBuilder()
            .setTitle("TRANSFERENCIA")
            .setDescription("Ingrese el monto a transferir")
            .setValidationPattern(Pattern.compile("^\\d+(\\.\\d+)?$"), "Ingrese un monto válido")
            .build()
            .showDialog(gui);
    }

    public void showNonExistantAliasError() {
        showError("Alias inexistente");
    }

    public void showCantTransferToSelfError() {
        showError("No puede transferirse a sí mismo");
    }

    public void showInsufficientFundsError() {
        showError("Fondos insuficientes");
    }

    public void showTransferError() {
        showError("Transferencia denegada");
    }

    public void showSuccessMsg() {
        new MessageDialogBuilder()
            .setTitle("")
            .setText("Transferencia exitosa")
            .build()
            .showDialog(gui);
    }
    
    private void showError(String errorMsg) {
        new MessageDialogBuilder()
            .setTitle("ERROR")
            .setText(errorMsg) //antes: "Se produjo un error desconocido"
            .build()
            .showDialog(gui);
    }

    public void bindTransferButton(Runnable action) {
        transferButton.addListener(transferButton -> action.run());
    }

    public void bindHistoryButton(Runnable action) {
        historyButton.addListener(historyButton -> action.run());
    }

    public void bindAliasButton(Runnable action) {
        aliasButton.addListener(aliasButton -> action.run());
    }

    public void bindLoanButton(Runnable action) {
        loanButton.addListener(loanButton -> action.run());
    }

    public void bindBrokerButton(Runnable action) {
        brokerButton.addListener(brokerButton -> action.run());
    }

    public void bindAdviceButton(Runnable action) {
        adviceButton.addListener(adviceButton -> action.run());
    }

    public void bindExitButton(Runnable action) {
        exitButton.addListener(closeButton -> action.run());
    }
}
