package bancolafamilia.gui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
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
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Operacion;
import bancolafamilia.banco.Gerente;

public class ManagerMenuPage extends PageController<ManagerMenuView>{
    private Gerente manager;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public ManagerMenuPage(Banco banco, WindowBasedTextGUI gui, Gerente manager) {
        super(banco, new ManagerMenuView(gui), gui);

        this.manager = manager;
        
        view.updateName(manager.getNombre());
        view.updateReserves(banco.getReservesTotal());
        view.updateDeposits(banco.getDepositsTotal());
        view.updateLoans(banco.getLoanedTotal());
        view.updateBalance(banco.getBalance());

        view.bindHistoryButton(() -> handleHistoryButton());
        view.bindSuperviseLaunderButton(() -> handleReviewTransactionsButton());
        view.bindExitButton(() -> handleExitButton());
    }

    private void handleHistoryButton() {
        // TODO Auto-generated method stub
        view.showHistory(banco.getOperaciones());
    }

    private void handleReviewTransactionsButton() {
        // TODO Auto-generated method stub
        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar SUPERVISAR LAVADO");
    }

    private void handleExitButton() {
        CambiarPagina(new LoginPage(banco, gui));;
    }
}

class ManagerMenuView extends PageView {
    private final Label welcomeMessageLabel;
    
    private final Label reservesIndicator;
    private final Label depositsIndicator;
    private final Label loansIndicator;
    private final Label balanceIndicator;

    private final Button historyButton;
    private final Button reviewTransactionsButton;
    private final Button exitButton;
    

    public ManagerMenuView(WindowBasedTextGUI gui) {
        super(gui);
        
        this.welcomeMessageLabel = new Label("");
        this.reservesIndicator = new Label("");
        this.depositsIndicator = new Label("");
        this.loansIndicator = new Label("");
        this.balanceIndicator = new Label("");
        
        this.historyButton = new Button("Movimientos");
        this.reviewTransactionsButton = new Button("Operaciones pendientes");
        this.exitButton = new Button("Salir");
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
                .setVerticalSpacing(1)
                .setTopMarginSize(1)
                .setBottomMarginSize(1)
                .setLeftMarginSize(2)
                .setRightMarginSize(2));
        window.setComponent(panel); // IMPORTANTE, si no, no se va a dibujar nada y termina el programa.
        
        // Layout
        LayoutData leftJustifyNoFill = GridLayout.createLayoutData(
            GridLayout.Alignment.BEGINNING,
            GridLayout.Alignment.CENTER,
            true,
            false,
            1,
            1);
        
        LayoutData rightJustifyNoFill = GridLayout.createLayoutData(
            GridLayout.Alignment.END,
            GridLayout.Alignment.CENTER,
            true,
            false,
            1,
            1);
        
        LayoutData leftJustifyWithFill = GridLayout.createLayoutData(
            GridLayout.Alignment.BEGINNING,
            GridLayout.Alignment.BEGINNING,
            true,
            false,
            2,
            1);
        
        LayoutData horizontalFill = GridLayout.createHorizontallyFilledLayoutData(2);
        
        // Mensaje de bienvenida
        welcomeMessageLabel.setLayoutData(leftJustifyWithFill);                   // Ocupar 1 fila
        panel.addComponent(welcomeMessageLabel);

        // Reservas
        reservesIndicator.setLayoutData(leftJustifyNoFill);                // Ocupar 1 fila
        panel.addComponent(reservesIndicator); 

        // Depósitos
        depositsIndicator.setLayoutData(rightJustifyNoFill);                   // Ocupar 1 fila
        panel.addComponent(depositsIndicator); 

        // Préstamos
        loansIndicator.setLayoutData(leftJustifyWithFill);                   // Ocupar 1 fila
        panel.addComponent(loansIndicator); 

        // Balance
        balanceIndicator.setLayoutData(leftJustifyWithFill);                   // Ocupar 1 fila
        panel.addComponent(balanceIndicator);

        // Separador
        panel.addComponent(
            new Separator(Direction.HORIZONTAL)
                .setLayoutData(horizontalFill));
        
        // Botones
        panel.addComponent(
            historyButton
                .setLayoutData(leftJustifyWithFill));
        panel.addComponent(
            reviewTransactionsButton
                .setLayoutData(leftJustifyWithFill));
        panel.addComponent(
            exitButton
                .setLayoutData(leftJustifyWithFill));

        
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

    public void updateReserves(float reserves) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        reservesIndicator.setText("Reservas " + currencyFormatter.format(reserves));
    }
    
    public void updateDeposits(float deposits) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        depositsIndicator.setText(currencyFormatter.format(deposits) + ": Depósitos");
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
        historyButton.addListener(historyButton -> action.run());
    }

    public void bindSuperviseLaunderButton(Runnable action) {
        reviewTransactionsButton.addListener(loanButton -> action.run());
    }

    public void bindExitButton(Runnable action) {
        exitButton.addListener(exitButton -> action.run());
    }
}
