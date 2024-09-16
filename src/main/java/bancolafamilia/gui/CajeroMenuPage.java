package bancolafamilia.gui;

import java.util.Arrays;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Separator;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import bancolafamilia.TimeSimulation;
import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Cajero;

public class CajeroMenuPage extends PageController<CajeroMenuView>{
    private Cajero cajero;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public CajeroMenuPage(Banco banco, WindowBasedTextGUI gui, Cajero cajero, TimeSimulation timeSim) {
        super(banco, new CajeroMenuView(gui, cajero.getNombre()), gui, timeSim);

        this.cajero = cajero;
    }
}

class CajeroMenuView extends PageView {
    private final String name;

    public CajeroMenuView(WindowBasedTextGUI gui, String name) {
        super(gui);
        this.name = name;
    }

    public void setupUI() {
        
    }
}
