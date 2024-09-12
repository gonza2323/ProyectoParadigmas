package bancolafamilia.gui;

import java.lang.ModuleLayer.Controller;
import java.time.LocalDateTime;

import javax.swing.text.View;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import bancolafamilia.banco.Banco;

/*
 * Para crear una página, se deben crear dos classes. Una que herede de
 * PageController<V extends View> que manjea solo la lógica de la página
 * (No se preocupa por cómo se ve, ni trabaja con la libreria de gui);
 * y otra clase que hereda de View que es la que se encarga del diseño
*/

/*
 * Esta clase representa la lógica detrás de una página en particular
 * De esta clase tienen que heredar todas las páginas que uno cree.
 * Toma como argumento entre las <> a la clase de la vista correspondiente.
 * Las vistas heredan siempre de la clase View.
*/
public abstract class PageController<V extends PageView> {
    protected final Banco banco;            // Toda página tiene una referencia al banco
    protected final V view;                 // Referencia a la vista asociada
    protected final WindowBasedTextGUI gui; // Referencia a la gui. No hay que tocarla en esta clase
                                            // pero se necesita para crear la siguiente página
    private PageController<?> nextPage;     // Guarda la próxima página a la que se tiene que pasar
                                            // es null si ya se tiene que salir del programa
                                            // Es privada. Si se quiere cambiar de página hay que
                                            // usar CambiarPagina(PageController<V> nuevaPagina)

    
    // Siempre requiere una referencia al banco, a la vista asociada, y a la gui
    // No hay que tocar la gui en esta clase, solo usarla para construir la siguiente pagina.
    public PageController(Banco banco, V view, WindowBasedTextGUI gui) {
        this.banco = banco;
        this.view = view;
        this.gui = gui;
        this.nextPage = null;
    }

    // Método que se llama desde la clase Interfaz para renderizar la página
    public final PageController<?> run() {
        
        view.setup();  // Inicializar la view
        view.updateTimeBar(LocalDateTime.now());
        
        setupController();

        view.mainloop();

        view.closeView();// Cerrar la página actual
        return nextPage; // Retornamos la siguiente página
    };
    
    // Método para indicar que se debe cambiar de página.
    // Toma una nueva página como argumento.
    protected final void CambiarPagina(PageController<?> nextPage) {
        this.nextPage = nextPage;
        view.closeView(); // Cierra la página actual
    }

    protected void setupController() { }
}
