package bancolafamilia.gui;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

abstract class View {
    protected final WindowBasedTextGUI gui;

    public View(WindowBasedTextGUI gui) {
        this.gui = gui;
    }
    
    public final void closeView() {
        for (Window window : gui.getWindows())
            window.close();
    }
}