package bancolafamilia.gui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Pattern;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LayoutData;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Separator;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.table.Table;

import bancolafamilia.banco.Operacion;

public class ClientMenuView extends PageView {
    
    private final String name;
    private final Label balanceIndicator;
    private final Label deudaIndicator;

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
        this.deudaIndicator = new Label("");
        
        transferButton = new Button("Transferencia");
        historyButton = new Button("Movimientos");
        aliasButton = new Button("Consultar alias");
        loanButton = new Button("Préstamos");
        brokerButton = new Button("Operar en bolsa");
        adviceButton = new Button("Asesoramiento");
        exitButton = new Button("Salir");
    }

    public void startUI() {
        // crea una ventana y le dice que se centre
        BasicWindow window = new BasicWindow("BANCO LA FAMILIA");
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN,
                                      Window.Hint.FIT_TERMINAL_WINDOW,
                                      Window.Hint.NO_DECORATIONS));

        // panel
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

        // Balance y deuda
        balanceIndicator.setLayoutData(horizontalFill);
        panel.addComponent(balanceIndicator);

        deudaIndicator.setLayoutData(horizontalFill);
        panel.addComponent(deudaIndicator);

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
        balanceIndicator.setText("Saldo: " + decimalFormat.format(balance));
    }

    public void updateDeuda(float deuda) {
        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);
        deudaIndicator.setText("Deuda: " + decimalFormat.format(deuda));
    }

    public String requestAlias() {
        return new TextInputDialogBuilder()
            .setTitle("TRANSFERENCIA")
            .setDescription("Ingrese el alias del destinatario")
            .setValidationPattern(Pattern.compile("^[a-zA-Z]+\\.[a-zA-Z]+\\.[a-zA-Z]+$"), "Ingrese un alias válido")
            .build()
            .showDialog(gui);
    }

    public String requestMotivo() {
        return new TextInputDialogBuilder()
                .setTitle("TRANSFERENCIA")
                .setDescription("Ingrese una breve descripción")
                .setValidationPattern(Pattern.compile("^([a-zA-Z]+\\s?){1,5}$"), "Máximo 5 palabras")
                .build()
                .showDialog(gui);
    }    

    public String requestTransferAmount() {
        return new TextInputDialogBuilder()
            .setTitle("TRANSFERENCIA")
            .setDescription("Ingrese el monto a transferir")
            .setValidationPattern(Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$"), "Ingrese un monto válido")
            .build()
            .showDialog(gui);
    }

    public void showTransferSuccessMsg() {
        new MessageDialogBuilder()
            .setTitle("")
            .setText("Transferencia exitosa")
            .build()
            .showDialog(gui);
    }

    public void showUserAlias(String alias) {
        new MessageDialogBuilder()
            .setTitle("")
            .setText("Su alias es:\n" + alias)
            .build()
            .showDialog(gui);
    }

    public String requestLoanAmount(float maxLoanAmount, float anualInterestRate) {
        NumberFormat currencyFormater = NumberFormat.getCurrencyInstance(Locale.US);
        DecimalFormat percentageFormater = new DecimalFormat("0.00%");

        return new TextInputDialogBuilder()
            .setTitle("SOLICITUD DE PRÉSTAMO")
            .setDescription("Tasa de interés anual: " + percentageFormater.format(anualInterestRate)
                        + "\nFinanciación máxima disponible: " + currencyFormater.format(maxLoanAmount))
            .setValidationPattern(Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$"), "Ingrese un monto válido")
            .build()
            .showDialog(gui);
    }

    public String requestLoanMonths() {
        return new TextInputDialogBuilder()
            .setTitle("SOLICITUD DE PRÉSTAMO")
            .setDescription("Ingrese periodo del préstamo en meses (mínimo 1)")
            .setValidationPattern(Pattern.compile("^[1-9][0-9]*$"), "Ingrese una cantidad de meses válida")
            .build()
            .showDialog(gui);
    }

    public void showNotEnoughCreditError() {
        new MessageDialogBuilder()
            .setTitle("ERROR")
            .setText("No tiene acceso a préstamos tan grandes")
            .build()
            .showDialog(gui);
    }

    public void showLoanTooSmallError() {
        new MessageDialogBuilder()
            .setTitle("ERROR")
            .setText("El préstamo debe ser de al menos $1,000")
            .build()
            .showDialog(gui);
    }

    public void showLoanError() {
        new MessageDialogBuilder()
            .setTitle("ERROR")
            .setText("Se produjo un error")
            .build()
            .showDialog(gui);
    }

    public void showLoanSuccessMsg() {
        new MessageDialogBuilder()
            .setTitle("SOLICITUD DE PRÉSTAMO")
            .setText("Se le ha otorgado el préstamo solicitado")
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

    private void showError(String errorMsg) {
        new MessageDialogBuilder()
            .setTitle("ERROR")
            .setText(errorMsg)
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
