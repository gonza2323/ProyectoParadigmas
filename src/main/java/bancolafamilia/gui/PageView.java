package bancolafamilia.gui;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LayoutData;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;


/*
 * Clase asbtracta que representa la vista de una página
 * Todas las vistas deben heredar de ella
 * Las subclases deben implementar el método startUI(), que
 * dibuja la interfaz (crear ventanas, botones, etc.)
*/
public abstract class PageView {
    
    protected final Window mainWindow;
    private final Window timeBarWindow;
    private final Label timeLabel;
    
    // Referencia a la gui
    // Se necesita para crear ventanas, botones, etc.
    protected final WindowBasedTextGUI gui;

    // Constructor
    public PageView(WindowBasedTextGUI gui) {
        this.gui = gui;
        this.mainWindow = new BasicWindow();
        this.timeBarWindow = new BasicWindow();
        this.timeLabel = new Label("asd");
    }

    public final void setup() {
        setupTimeBar();
        setupUI();
    }
    
    // Método que hay que implementar en las subclases
    // de View, en el cuál dibujamos la interfaz
    public abstract void setupUI();


    public final void mainloop() {
        gui.addWindow(mainWindow);
        gui.addWindow(timeBarWindow);
        gui.waitForWindowToClose(timeBarWindow);
    }
    
    
    // Cierra la página, la usa la clase abstracta PageController
    // No hace falta utilizarlo    
    public final void closeView() {
        while (!gui.getWindows().isEmpty()) {
            gui.getWindows().iterator().next().close();
        }
    }

    private void setupTimeBar() {
        timeBarWindow.setHints(Arrays.asList(
            Window.Hint.FIT_TERMINAL_WINDOW,
            Window.Hint.FIXED_POSITION,
            Window.Hint.NO_DECORATIONS,
            Window.Hint.NO_FOCUS,
            Window.Hint.NO_POST_RENDERING));
        
        timeBarWindow.setPosition(new TerminalPosition(0, 0));
        
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(3));
        panel.setPreferredSize(new TerminalSize(1000, 1));

        LayoutData leftFill = GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER, true, false);
        LayoutData centerFill = GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false);
        LayoutData rightFill = GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER, true, false);
        panel.addComponent(new EmptySpace().setLayoutData(leftFill));
        panel.addComponent(timeLabel);
        panel.addComponent(new Label("Presione F10 para avanzar el tiempo").setLayoutData(rightFill));
        
        timeBarWindow.setComponent(panel);
    }

    public final void updateTimeBar(LocalDateTime time) {
        timeLabel.setText(time.toString());
    }
}