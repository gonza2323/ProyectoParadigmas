package bancolafamilia.gui;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;


/**
 * Clase asbtracta que representa la vista de una página
 * Todas las vistas deben heredar de ella
 * Las subclases deben implementar el método startUI(), que
 * dibuja la interfaz (crear ventanas, botones, etc.)
*/
public abstract class PageView {
    
    // Referencia a la gui
    // Se necesita para crear ventanas, botones, etc.
    protected final WindowBasedTextGUI gui;

    // Constructor
    public PageView(WindowBasedTextGUI gui) {
        this.gui = gui;
    }

    // Método que hay que implementar en las subclases
    // de View, en el cuál dibujamos la interfaz
    public abstract void startUI();
    
    // Cierra la página, la usa la clase abstracta PageController
    // No hace falta utilizarlo    
    public final void closeView() {
        for (Window window : gui.getWindows())
            window.close();
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
}