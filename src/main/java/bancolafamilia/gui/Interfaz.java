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

public class Interfaz {

    private final Screen screen;
    private final WindowBasedTextGUI gui;
    
    private PaginaInterfaz<?> paginaActual;

    public Interfaz(Banco banco) throws IOException {
        this.screen = new DefaultTerminalFactory().createScreen();
        this.gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));
        this.paginaActual = new LoginPage(banco, gui);
    }

    public void start() throws IOException {
        screen.startScreen();
        guiLoop();
    }

    private void guiLoop() throws IOException {
        while (paginaActual != null) {
            PaginaInterfaz<?> nuevaPagina = paginaActual.run();
            this.paginaActual = nuevaPagina;
        }
        stop();
    }

    private void stop() throws IOException {
        screen.stopScreen();
    }
}
