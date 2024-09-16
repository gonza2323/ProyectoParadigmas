package bancolafamilia.gui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;

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
    protected Window mainWindow = new BasicWindow();            // ventana principal de esta página
    private final Window clockBarWindow = new BasicWindow();
    
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
        clockBarWindow.setHints(
            Arrays.asList(Window.Hint.FIT_TERMINAL_WINDOW,
                          Window.Hint.NO_DECORATIONS,
                          Window.Hint.NO_POST_RENDERING,
                          Window.Hint.NO_FOCUS,
                          Window.Hint.FIXED_SIZE,
                          Window.Hint.FIXED_POSITION));
        clockBarWindow.setFixedSize(new TerminalSize(400, 1));
        clockBarWindow.setPosition(new TerminalPosition(0, 0));
        
        Panel panel = new Panel(new GridLayout(1));
        currentTimeLabel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        clockBarWindow.setComponent(panel);
    
        panel.addComponent(currentTimeLabel);
        gui.addWindow(clockBarWindow);
    }

    public final void updateClock(LocalDateTime newTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        currentTimeLabel.setText(formatter.format(newTime));
    }

    public final void render() {
        gui.addWindowAndWait(mainWindow);
    }
}