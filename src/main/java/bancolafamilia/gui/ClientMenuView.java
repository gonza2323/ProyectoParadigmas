package bancolafamilia.gui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.bundle.LanternaThemes;
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
    
    private final Label welcomeMessageLabel = new Label("Bienvenido [nombre]");
    private final Label balanceIndicator = new Label("");
    private final Label deudaIndicator = new Label("");

    private final Button transferButton = new Button("Transferencia");
    private final Button historyButton = new Button("Movimientos");
    private final Button aliasButton = new Button("Consultar alias");
    private final Button loanButton = new Button("Préstamos");
    private final Button brokerButton = new Button("Operar en bolsa");
    private final Button adviceButton = new Button("Asesoramiento");
    private final Button notificationsButton = new Button("Notificaciones");
    private final Button exitButton = new Button("Salir");
    //private final Button changePasswordButton;
    

    public ClientMenuView(WindowBasedTextGUI gui) {
        super(gui);
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
                .setVerticalSpacing(1)
                .setTopMarginSize(1)
                .setLeftMarginSize(1)
                .setRightMarginSize(1));
        window.setComponent(panel); // IMPORTANTE, si no, no se va a dibujar nada y termina el programa.
        
        // configuración layout
        LayoutData horizontalFill = GridLayout.createHorizontallyFilledLayoutData(2);
        
        // Mensaje de bienvenida
        welcomeMessageLabel.setLayoutData(horizontalFill);
        panel.addComponent(welcomeMessageLabel);

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
            notificationsButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            exitButton
                .setLayoutData(leftJustify));

        // Agregar ventana a la gui
        gui.addWindowAndWait(window);
    }

    public void showHistory(List<Operacion> operaciones){

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
            String fecha = op.getDate().format(formatter);
            String description = op.getDescription();
            String monto = decimalFormat.format(op.getAmount());

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

    public void updateName(String name) {
        welcomeMessageLabel.setText("Bienvenido: " + name);
    }

    public void updateBalance(float balance) {
        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);
        balanceIndicator.setText("Saldo: " + decimalFormat.format(balance));
    }

    public void updateDeuda(float deuda) {
        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);
        deudaIndicator.setText("Deuda: " + decimalFormat.format(deuda));
    }

    public void updateNotificationsButton(boolean hasNewNotifications) {
        if (hasNewNotifications) {
            notificationsButton.setTheme(LanternaThemes.getRegisteredTheme("conqueror"));
        } else {
            notificationsButton.setTheme(LanternaThemes.getRegisteredTheme("defrost"));
        }
    }

    public void showNotifications(ArrayList<String> notifications) {
        BasicWindow window = new BasicWindow("Notificaciones");
        window.setHints(
            Arrays.asList(
                Window.Hint.CENTERED,
                Window.Hint.FIT_TERMINAL_WINDOW));
        window.setCloseWindowWithEscape(true);
        
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        window.setComponent(panel);
        
        for (String notification : notifications) {
            panel.addComponent(new Label(notification)
                .setLabelWidth(40));
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
        showMessageDialog("", "Transferencia exitosa");
    }

    public void showUserAlias(String alias) {
        showMessageDialog("", "Su alias es:\n" + alias);
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
        showErrorDialog("No tiene acceso a préstamos tan grandes");
    }

    public void showLoanTooSmallError() {
        showErrorDialog("El préstamo debe ser de al menos $1,000");
    }

    public void showLoanError() {
        showErrorDialog("Se produjo un error");
    }

    public void showLoanSuccessMsg() {
        showMessageDialog("SOLICITUD DE PRÉSTAMO", "Se le ha otorgado el préstamo solicitado");
    }

    public void showPendingApprovalMsg() {
        showMessageDialog("TRANSFERENCIA", "Su transferencia quedó a la espera de ser aprobada por el banco");
    }

    public void showNonExistantAliasError() {
        showErrorDialog("Alias inexistente");
    }

    public void showCantTransferToSelfError() {
        showErrorDialog("No puede transferirse a sí mismo");
    }

    public void showInsufficientFundsError() {
        showErrorDialog("Fondos insuficientes");
    }

    public void showTransferError() {
        showErrorDialog("Transferencia denegada");
    }

    public void bindTransferButton(Runnable action) {
        transferButton.addListener(b -> action.run());
    }

    public void bindHistoryButton(Runnable action) {
        historyButton.addListener(b -> action.run());
    }

    public void bindAliasButton(Runnable action) {
        aliasButton.addListener(b -> action.run());
    }

    public void bindLoanButton(Runnable action) {
        loanButton.addListener(b -> action.run());
    }

    public void bindBrokerButton(Runnable action) {
        brokerButton.addListener(b -> action.run());
    }

    public void bindAdviceButton(Runnable action) {
        adviceButton.addListener(b -> action.run());
    }

    public void bindNotificationsButton(Runnable action) {
        notificationsButton.addListener(b -> action.run());
    }

    public void bindExitButton(Runnable action) {
        exitButton.addListener(b -> action.run());
    }
}
