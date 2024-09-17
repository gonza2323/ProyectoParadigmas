package bancolafamilia.gui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;

import bancolafamilia.TimeSimulation;

import java.time.format.DateTimeFormatter;
import java.time.*;
import java.util.Arrays;


/**
 * Clase asbtracta que representa la vista de una página
 * Todas las vistas deben heredar de ella
 * Las subclases deben implementar el método startUI(), que
 * dibuja la interfaz (crear ventanas, botones, etc.)
*/
public abstract class PageView {

    protected final Label currentTimeLabel = new Label("[Current Time]");
    protected BasicWindow mainWindow = new BasicWindow();            // ventana principal de esta página
    protected Panel clockBarPanel = new Panel();
    
    // Referencia a la gui
    // Se necesita para crear ventanas, botones, etc.
    protected final WindowBasedTextGUI gui;

    // Constructor
    public PageView(WindowBasedTextGUI gui) {
        this.gui = gui;
    }

    // Método que hay que implementar en las subclases
    // de View, en el cuál dibujamos la interfaz
    public abstract void setupUI();
    
    // Cierra la página, la usa la clase abstracta PageController
    // No hace falta utilizarlo    
    public final void closeView() {
        while (!gui.getWindows().isEmpty()) {
            gui.getWindows().iterator().next().close();
        }
    }

    protected void showMessageDialog(String title, String msg) {
        new MessageDialogBuilder()
            .setTitle(title)
            .setText(msg)
            .build()
            .showDialog(gui);
    }
    
    protected void showErrorDialog(String errorMsg) {
        showMessageDialog("ERROR", errorMsg);
    }

    protected void showError() {
        showMessageDialog("ERROR", "Se produjo un error");
    }

    public void setupClockUI() {
        clockBarPanel = new Panel(new GridLayout(1));
        clockBarPanel.addComponent(currentTimeLabel);
        clockBarPanel.setPreferredSize(new TerminalSize(400, 1));

        currentTimeLabel.setLayoutData(GridLayout
            .createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                false));
    }

    public final void updateClock(LocalDateTime newTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        currentTimeLabel.setText(formatter.format(newTime) + "  "
                                + TimeSimulation.getInstance().getTimeMultiplierStr() + "  "
                                + "Use \"a\" y \"d\" para controlar la velocidad, \"p\" para pausar");
    }

    public final void render() {
        gui.addWindowAndWait(mainWindow);
    }
}