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

import bancolafamilia.banco.Banco;
import bancolafamilia.banco.AgenteEspecial;

public class AgenteEspecialMenuPage extends PageController<AgenteEspecialMenuView>{
    private AgenteEspecial agente;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public AgenteEspecialMenuPage(Banco banco, WindowBasedTextGUI gui, AgenteEspecial agente) {
        super(banco, new AgenteEspecialMenuView(gui, agente.getNombre()), gui);

        this.agente = agente;
    }
}

class AgenteEspecialMenuView extends PageView {
    private final String name;

    public AgenteEspecialMenuView(WindowBasedTextGUI gui, String name) {
        super(gui);
        this.name = name;
    }

    public void setupUI() {
        
    }
}
