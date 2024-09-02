package bancolafamilia.gui;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import bancolafamilia.banco.Banco;

public abstract class PaginaInterfaz<T extends View> {
    protected final Banco banco;
    protected final T view;
    protected final WindowBasedTextGUI gui;
    protected PaginaInterfaz<?> nextPage;
    
    public PaginaInterfaz(Banco banco, T view, WindowBasedTextGUI gui) {
        this.banco = banco;
        this.view = view;
        this.gui = gui;
        this.nextPage = null;
    }

    public final PaginaInterfaz<?> run() {
        procesar();
        view.closeView();
        return nextPage;
    };
    
    protected void procesar() { return; }

    protected final void CambiarPagina(PaginaInterfaz<?> nextPage) {
        this.nextPage = nextPage;
        view.closeView();
    }
}


