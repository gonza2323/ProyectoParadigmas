package bancolafamilia.gui;

import java.text.DecimalFormat;
import java.util.Arrays;

import com.googlecode.lanterna.TerminalSize;
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
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Gerente;

public class ManagerMenuPage extends PageController<ManagerMenuView>{
    private Gerente manager;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public ManagerMenuPage(Banco banco, WindowBasedTextGUI gui, Gerente manager) {
        super(banco, new ManagerMenuView(gui, manager.getNombre()), gui);

        this.manager = manager;

        // TODO: Implementar gets
        view.updateReserves(banco.getReservas());
        view.updateDeposits(banco.getReservas());
        view.updateLoans(banco.getReservas());
        view.updateBalance(banco.getReservas());

        view.bindHistoryButton(() -> handleHistoryButton());
        view.bindSuperviseLaunderButton(() -> handleSuperviseLaunderButton());
        view.bindExitButton(() -> handleExitButton());
    }

    private void handleHistoryButton() {
        // TODO Auto-generated method stub
        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar MOVIMIENTOS");
    }

    private void handleSuperviseLaunderButton() {
        // TODO Auto-generated method stub
        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar SUPERVISAR LAVADO");
    }

    private void handleExitButton() {
        CambiarPagina(new LoginPage(banco, gui));;
    }
}

class ManagerMenuView extends PageView {
    private final String name;

    private final Label reservesIndicator;
    private final Label depositsIndicator;
    private final Label loansIndicator;
    private final Label balanceIndicator;

    private final Button historyButton;
    private final Button superviseLaunderButton;
    private final Button exitButton;
    

    public ManagerMenuView(WindowBasedTextGUI gui, String name) {
        super(gui);
        
        this.name = name;
        this.reservesIndicator = new Label("");
        this.depositsIndicator = new Label("");
        this.loansIndicator = new Label("");
        this.balanceIndicator = new Label("");
        
        this.historyButton = new Button("Movimientos");
        this.superviseLaunderButton = new Button("Lavado");
        this.exitButton = new Button("Salir");
    }

    public void setupUI() {
        // Crea una ventana y le dice que se centre
        BasicWindow window = new BasicWindow("BANCO LA FAMILIA");
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN,
                                      Window.Hint.FIT_TERMINAL_WINDOW,
                                      Window.Hint.NO_DECORATIONS));

        // Panel
        Panel contentPanel = new Panel(new GridLayout(2));
        window.setComponent(contentPanel); // IMPORTANTE, si no, no se va a dibujar nada y termina el programa.
        
        // Configuramos la separación entre columnas y filas pa que quede lindo
        GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(1);
        gridLayout.setVerticalSpacing(1);

        // Layout
        LayoutData leftJustify = GridLayout.createLayoutData(
            GridLayout.Alignment.BEGINNING,
            GridLayout.Alignment.BEGINNING,
            true,
            false,
            2,
            1);

        // Mensaje de bienvenida
        Label welcomeMsg = new Label("Bienvenido: " + name);
        welcomeMsg.setLayoutData(leftJustify);                   // Ocupar 1 fila
        contentPanel.addComponent(welcomeMsg);

        // Reservas
        reservesIndicator.setLayoutData(leftJustify);                // Ocupar 1 fila
        contentPanel.addComponent(reservesIndicator); 

        // Depósitos
        depositsIndicator.setLayoutData(leftJustify);                   // Ocupar 1 fila
        contentPanel.addComponent(depositsIndicator); 

        // Préstamos
        loansIndicator.setLayoutData(leftJustify);                   // Ocupar 1 fila
        contentPanel.addComponent(loansIndicator); 

        // Balance
        balanceIndicator.setLayoutData(leftJustify);                   // Ocupar 1 fila
        contentPanel.addComponent(balanceIndicator);

        // Separador
        contentPanel.addComponent(
            new Separator(Direction.HORIZONTAL)
                .setLayoutData(
                    GridLayout.createHorizontallyFilledLayoutData(2)));
        
        
            
        // Botones
        contentPanel.addComponent(
            historyButton
                .setLayoutData(leftJustify));
        contentPanel.addComponent(
            superviseLaunderButton
                .setLayoutData(leftJustify));
        contentPanel.addComponent(
            exitButton
                .setLayoutData(leftJustify));

        
        gui.addWindowAndWait(window);
    }

    public void updateReserves(float reserves) {
        reservesIndicator.setText("$ " + new DecimalFormat("#.##").format(reserves));
    }
    
    public void updateDeposits(float deposits) {
        depositsIndicator.setText("$ " + new DecimalFormat("#.##").format(deposits));
    }

    public void updateLoans(float loans) {
        loansIndicator.setText("$ " + new DecimalFormat("#.##").format(loans));
    }
    
    public void updateBalance(float balance) {
        balanceIndicator.setText("$ " + new DecimalFormat("#.##").format(balance));
    }

    public void bindHistoryButton(Runnable action) {
        historyButton.addListener(historyButton -> action.run());
    }

    public void bindSuperviseLaunderButton(Runnable action) {
        superviseLaunderButton.addListener(loanButton -> action.run());
    }

    public void bindExitButton(Runnable action) {
        exitButton.addListener(exitButton -> action.run());
    }
}
