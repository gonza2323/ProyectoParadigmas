package bancolafamilia.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.ComboBox;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Cliente;


/*
 * Maneja toda la interfaz. Tiene un bucle que va dibujando cada página
 * En todo momento hay una página actual que se está renderizando
 * Cuando se debe pasar de página, la página actual retorna la siguiente
 * a la que se debe pasar, y la clase interfaz la cambia y renderiza
*/
public class Interfaz {

    private final Screen screen;            // pantalla
    private final WindowBasedTextGUI gui;   // gui
    
    private PageController<?> paginaActual; // pagina actual

    public Interfaz(Banco banco) throws IOException {
        this.screen = new DefaultTerminalFactory().createScreen();
        this.gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));
        
        // Acá se configura la página inicial, para debuggear más rápido se puede cambiar
        // por la que uno esté armando en ese momento.
        // this.paginaActual = new LoginPage(banco, gui);
        Cliente prueba = new Cliente("Armando", 54213856, "armando", "1234");
        banco.addUser(prueba);
        prueba.setAlias("que.es.eso");
        banco.depositFunds(prueba, 7500);
        this.paginaActual = new ClientMenuPage(banco, gui, prueba);
    }

    // Inicializa la pantalla y empieza el loop de la interfaz
    public void start() throws IOException {
        screen.startScreen();
        guiLoop();
    }

    // Loop. Ejecuta la página inicial. Al terminar de ejecutarse una página,
    // la misma retorna la siguiente página que se debe ejecutar.
    // El loop hace esto hasta que una página retorne null, y ahí termina el programa.
    private void guiLoop() throws IOException {
        while (paginaActual != null) {
            PageController<?> nuevaPagina = paginaActual.run();
            this.paginaActual = nuevaPagina;
        }
        stop();
    }

    private void stop() throws IOException {
        screen.stopScreen();
    }
}