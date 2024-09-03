package bancolafamilia.gui;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import bancolafamilia.banco.Banco;

public class UserMenuPage extends PageController<UserMenuView>{
    public UserMenuPage(Banco banco, WindowBasedTextGUI gui) {
        super(banco, new UserMenuView(gui), gui);
    }
}

class UserMenuView extends PageView {
    public UserMenuView(WindowBasedTextGUI gui) {
        super(gui);
    }

    public void startUI() {

    }
}
