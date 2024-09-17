package bancolafamilia.gui;

import java.lang.ModuleLayer.Controller;

import javax.swing.text.View;
import java.time.*;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import bancolafamilia.TimeSimulation;
import bancolafamilia.banco.Banco;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

/*
 * Para crear una página, se deben crear dos classes. Una que herede de
 * PageController<V extends View> que maneja solo la lógica de la página
 * (No se preocupa por cómo se ve, ni trabaja con la librería de gui);
 * y otra clase que hereda de View que es la que se encarga del diseño
*/

/**
 * Esta clase representa la lógica detrás de una página en particular
 * De esta clase tienen que heredar todas las páginas que uno cree.
 * Toma como tipo entre las <> a la clase de la vista correspondiente.
 * Las vistas heredan siempre de la clase View.
*/
public abstract class PageController<V extends PageView> {
    protected Banco banco;            // Toda página tiene una referencia al banco
    protected final V view;                 // Referencia a la vista asociada
    protected final WindowBasedTextGUI gui; // Referencia a la gui. No hay que tocarla en esta clase
                                            // pero se necesita para crear la siguiente página
    private PageController<?> nextPage;     // Guarda la próxima página a la que se tiene que pasar
                                            // es null si ya se tiene que salir del programa
                                            // Es privada. Si se quiere cambiar de página hay que
                                            // usar CambiarPagina(PageController<V> nuevaPagina)

    /**
     * Siempre requiere una referencia al banco, a la vista asociada, y a la gui
     * No hay que tocar la gui en esta clase, solo usarla para construir la siguiente pagina.
     */
    public PageController(Banco banco, V view, WindowBasedTextGUI gui) {
        this.banco = banco;
        this.view = view;
        this.gui = gui;
        this.nextPage = null;
    }

    // Método que se llama desde la clase Interfaz para rendeirzar la página
    public final PageController<?> run() {
        view.setupClockUI();
        updateTime();
        view.setupUI();  // Renderizar la view

        controllerSetup();

        view.render();
        view.closeView();// Cerrar la página actual
        return nextPage; // Retornamos la siguiente página
    };
    
    // Método para indicar que se debe cambiar de página.
    // Toma una nueva página como argumento.
    protected final void CambiarPagina(PageController<?> nextPage) {
        this.nextPage = nextPage;
        view.closeView(); // Cierra la página actual
    }

    public void updateTime() {
        view.updateClock(TimeSimulation.getTime());
    }

    public void update() { };
    public void controllerSetup() { };
}
