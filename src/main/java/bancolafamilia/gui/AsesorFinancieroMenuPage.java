package bancolafamilia.gui;

import bancolafamilia.banco.AsesorFinanciero;
import bancolafamilia.banco.Banco;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

public class AsesorFinancieroMenuPage extends PageController<AsesorFinancieroMenuView>{
    private AsesorFinanciero asesor;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public AsesorFinancieroMenuPage(Banco banco, WindowBasedTextGUI gui, AsesorFinanciero asesor) {
        super(banco, new AsesorFinancieroMenuView(gui, asesor.getNombre()), gui);

        this.asesor = asesor;
    }
}

class AsesorFinancieroMenuView extends PageView {
    private final String name;

    public AsesorFinancieroMenuView(WindowBasedTextGUI gui, String name) {
        super(gui);
        this.name = name;
    }

    public void startUI() {
        
    }
}
