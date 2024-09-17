package bancolafamilia.gui;

import bancolafamilia.TimeSimulation;
import bancolafamilia.banco.AsesorFinanciero;
import bancolafamilia.banco.Banco;
import bancolafamilia.banco.financialAdvice;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class AsesorFinancieroMenuPage extends PageController<AsesorFinancieroMenuView>{
    private AsesorFinanciero asesor;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public AsesorFinancieroMenuPage(Banco banco, WindowBasedTextGUI gui, AsesorFinanciero asesor) {
        super(banco, new AsesorFinancieroMenuView(gui), gui);

        this.asesor = asesor;

        view.updateName(asesor.getNombre());

        view.bindHistoryButton(() -> handleHistoryButton());
        view.bindExitButton(() -> handleExitButton());
    }

    private void handleHistoryButton(){
        ArrayList<financialAdvice> asesoriaBrindada = asesor.getAdviceRecord();
        view.showAdviceHistory(asesoriaBrindada);
    }

    private void handleExitButton() {
        CambiarPagina(new LoginPage(banco, gui));;
    }
}

class AsesorFinancieroMenuView extends PageView {
    private final Label welcomeMessageLabel;

    private final Button historyButton;
    private final Button exitButton;

    public AsesorFinancieroMenuView(WindowBasedTextGUI gui) {
        super(gui);
        this.welcomeMessageLabel = new Label("");
        this.historyButton = new Button("Historial de Asesorias");
        this.exitButton = new Button("Salir");

    }


        
    public void setupUI() {
        // Crea una ventana y le dice que se centre
        mainWindow = new BasicWindow("BANCO LA FAMILIA");
        mainWindow.setHints(Arrays.asList(Window.Hint.FULL_SCREEN,
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
        mainWindow.setComponent(panel); // IMPORTANTE, si no, no se va a dibujar nada y termina el programa.

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

        Label welcomeMsg = new Label("Cargo: Asesor Financiero");
        welcomeMsg.setLayoutData(horizontalFill);
        panel.addComponent(welcomeMsg);

        // Separador
        panel.addComponent(
                new Separator(Direction.HORIZONTAL)
                        .setLayoutData(horizontalFill));

        // Botones
        panel.addComponent(
                historyButton
                        .setLayoutData(leftJustifyWithFill));
        panel.addComponent(
                exitButton
                        .setLayoutData(leftJustifyWithFill));
    }

    public void showAdviceHistory(ArrayList<financialAdvice> asesoriaBrindada) {

        BasicWindow window = new BasicWindow("Historial de Asesorias");
        window.setHints(
                Arrays.asList(
                        Window.Hint.CENTERED,
                        Window.Hint.FIT_TERMINAL_WINDOW));
        window.setCloseWindowWithEscape(true);

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        window.setComponent(panel);

        Table<Object> table = new Table<>("Fecha", "Cliente", "Tipo", "Detalle");
        panel.addComponent(table);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);

        for (financialAdvice advice : asesoriaBrindada) {

            String fecha = advice.getFecha().format(formatter);

            table.getTableModel().addRow(new Object[]{fecha,advice.getClient().getNombre(),advice.getTipo(),advice.getAdvice() });
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

    public void bindHistoryButton(Runnable action) {
        historyButton.addListener(historyButton -> action.run());
    }

    public void bindExitButton(Runnable action) {
        exitButton.addListener(exitButton -> action.run());
    }

}
