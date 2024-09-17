package bancolafamilia.gui;

import bancolafamilia.TimeSimulation;
import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Client;
import bancolafamilia.banco.Empleado;
import bancolafamilia.banco.Operacion;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ActualStateMenuPage extends PageController<ActualStateMenuView> {

    public ActualStateMenuPage(Banco banco, WindowBasedTextGUI gui) {
        super(banco, new ActualStateMenuView(gui), gui);

        view.updateReserves(banco.getReservesTotal());
        view.updateDeposits(banco.getDepositsTotal());
        view.updateLoans(banco.getLoanedTotal());
        view.updateBalance(banco.getBalance());

        view.bindHistoryButton(() -> handleHistoryButton());
        view.bindShowClientsButton(() -> handleShowClientsButton());
        view.bindShowEmployeesButton(() -> handleShowEmployeesButton());
        view.bindExitButton(() -> handleExitButton());
    }

    private void handleHistoryButton() {
        view.showHistory(banco.getOperaciones());
    }

    private void handleShowClientsButton() {
        view.showClients(banco.getClients());
    }

    private void handleShowEmployeesButton() {
        view.showEmployees(banco.getEmployees());
    }

    private void handleExitButton() {
        CambiarPagina(new StartMenuPage(banco, gui));
        ;
    }
}

class ActualStateMenuView extends PageView {
    private final Label welcomeMessageLabel = new Label("ESTADO ACTUAL BANCO LA FAMILIA");

    private final Label reservesIndicator = new Label("");
    private final Label depositsIndicator = new Label("");
    private final Label loansIndicator = new Label("");
    private final Label balanceIndicator = new Label("");

    private final Button historyButton = new Button("Movimientos");
    private final Button showClientsButton = new Button("Ver clientes");
    private final Button showEmployeesButton = new Button("Ver empleados");
    private final Button exitButton = new Button("Salir");


    public ActualStateMenuView(WindowBasedTextGUI gui) {
        super(gui);
    }

    @Override
    public void setupUI() {
        // Crea una ventana y le dice que se centre
        mainWindow = new BasicWindow("BANCO LA FAMILIA");
        mainWindow.setHints(
                Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.FIT_TERMINAL_WINDOW, Window.Hint.NO_DECORATIONS));

        // Panel
        Panel panel = new Panel(new GridLayout(2).setHorizontalSpacing(1).setVerticalSpacing(1).setTopMarginSize(1)
                .setBottomMarginSize(1).setLeftMarginSize(2).setRightMarginSize(2));
        mainWindow.setComponent(panel); // IMPORTANTE, si no, no se va a dibujar nada y termina el programa.

        // Layout
        LayoutData leftJustifyNoFill = GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING,
                GridLayout.Alignment.CENTER, true, false, 1, 1);

        LayoutData rightJustifyNoFill = GridLayout.createLayoutData(GridLayout.Alignment.END,
                GridLayout.Alignment.CENTER, true, false, 1, 1);

        LayoutData leftJustifyWithFill = GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING,
                GridLayout.Alignment.BEGINNING, true, false, 2, 1);

        LayoutData horizontalFill = GridLayout.createHorizontallyFilledLayoutData(2);

        // Mensaje de bienvenida
        welcomeMessageLabel.setLayoutData(leftJustifyWithFill); // Ocupar 1 fila
        panel.addComponent(welcomeMessageLabel);

        // Reservas
        reservesIndicator.setLayoutData(leftJustifyWithFill); // Ocupar 1 fila
        panel.addComponent(reservesIndicator);

        // Depósitos
        depositsIndicator.setLayoutData(leftJustifyWithFill); // Ocupar 1 fila
        panel.addComponent(depositsIndicator);

        // Préstamos
        loansIndicator.setLayoutData(leftJustifyWithFill); // Ocupar 1 fila
        panel.addComponent(loansIndicator);

        // Balance
        balanceIndicator.setLayoutData(leftJustifyWithFill); // Ocupar 1 fila
        panel.addComponent(balanceIndicator);

        // Separador
        panel.addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(horizontalFill));

        // Botones
        panel.addComponent(historyButton.setLayoutData(leftJustifyWithFill));
        panel.addComponent(showClientsButton.setLayoutData(leftJustifyWithFill));
        panel.addComponent(showEmployeesButton.setLayoutData(leftJustifyWithFill));
        panel.addComponent(exitButton.setLayoutData(leftJustifyWithFill));
    }

    public void showHistory(List<Operacion> operaciones) {

        BasicWindow window = new BasicWindow("Historial de Operaciones");
        window.setHints(Arrays.asList(Window.Hint.CENTERED, Window.Hint.FIT_TERMINAL_WINDOW));
        window.setCloseWindowWithEscape(true);

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        window.setComponent(panel);

        Table<Object> table = new Table<>("ID", "Fecha", "Tipo", "Monto", "Detalle");
        panel.addComponent(table);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);

        for (Operacion op : operaciones) {
            String id = Integer.toString(op.getId());
            String clase = op.getClass().getSimpleName();
            String fecha = op.getDate().format(formatter);
            String description = op.getDescription();
            String monto = decimalFormat.format(op.getAmount());

            table.getTableModel().addRow(id, fecha, clase, monto, description);
        }

        panel.addComponent(new Button("Cerrar", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));

        gui.addWindowAndWait(window);
    }

    public void showClients(List<Client> clients) {
        BasicWindow window = new BasicWindow("CLIENTES");
        window.setHints(Arrays.asList(Window.Hint.CENTERED, Window.Hint.FIT_TERMINAL_WINDOW));
        window.setCloseWindowWithEscape(true);

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        window.setComponent(panel);

        Table<Object> table = new Table<>("Nombre", "DNI", "Usuario", "Alias", "Balance", "Deuda", "Cliente Premium");
        panel.addComponent(table);

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

        for (Client c : clients) {
            String name = c.getNombre();
            String dni = numberFormat.format(c.getDNI());
            String username = c.getUsername();
            String alias = c.getAlias();
            String balance = currencyFormatter.format(c.getBalance());
            String debt = currencyFormatter.format(c.getDebt());
            String isPremium = c.isPremiumClient() ? "Sí" : "No";

            table.getTableModel().addRow(name, dni, username, alias, balance, debt, isPremium);
        }

        panel.addComponent(new Button("Cerrar", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));

        gui.addWindowAndWait(window);
    }

    public void showEmployees(List<Empleado> employees) {
        BasicWindow window = new BasicWindow("EMPLEADOS");
        window.setHints(Arrays.asList(Window.Hint.CENTERED, Window.Hint.FIT_TERMINAL_WINDOW));
        window.setCloseWindowWithEscape(true);

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        window.setComponent(panel);

        Table<Object> table = new Table<>("Nombre", "Tipo", "DNI", "usuario");
        panel.addComponent(table);

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

        for (Empleado e : employees) {
            String nombre = e.getNombre();
            String rol = e.getClass().getSimpleName();
            String dni = numberFormat.format(e.getDNI());
            String usuario = e.getUsername();

            table.getTableModel().addRow(nombre, rol, dni, usuario);
        }

        panel.addComponent(new Button("Cerrar", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));

        gui.addWindowAndWait(window);
    }

    public void updateReserves(float reserves) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        reservesIndicator.setText("Reservas " + currencyFormatter.format(reserves));
    }

    public void updateDeposits(float deposits) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        depositsIndicator.setText("Depósitos " + currencyFormatter.format(deposits));
    }

    public void updateLoans(float loans) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        loansIndicator.setText("Préstamos " + currencyFormatter.format(loans));
    }

    public void updateBalance(float balance) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        balanceIndicator.setText("Balance " + currencyFormatter.format(balance));
    }

    public void bindHistoryButton(Runnable action) {
        historyButton.addListener(b -> action.run());
    }

    public void bindShowClientsButton(Runnable action) {
        showClientsButton.addListener(b -> action.run());
    }

    public void bindShowEmployeesButton(Runnable action) {
        showEmployeesButton.addListener(b -> action.run());
    }

    public void bindExitButton(Runnable action) {
        exitButton.addListener(b -> action.run());
    }
}
