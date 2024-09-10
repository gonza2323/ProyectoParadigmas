package bancolafamilia.gui;

import java.util.Arrays;
import java.util.Optional;

import javax.swing.Action;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Button.Listener;
import com.googlecode.lanterna.gui2.GridLayout.Alignment;
import com.googlecode.lanterna.gui2.LinearLayout.GrowPolicy;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LayoutData;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Separator;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Cliente;
import bancolafamilia.banco.Gerente;
import bancolafamilia.banco.User;
import bancolafamilia.banco.AgenteEspecial;
import bancolafamilia.banco.AsesorFinanciero;
import bancolafamilia.banco.Cajero;


class StartMenuPage extends PageController<StartMenuView>{

    public StartMenuPage(Banco banco, WindowBasedTextGUI gui) {
        super(banco, new StartMenuView(gui), gui);
        
        view.bindBankLoginButton(() -> handleBankLoginButton());
        view.bindGoToBankButton(() -> handleGoToBankButton());
        view.bindExitButton(() -> handleCloseButton());
    }

    private void handleBankLoginButton() {
        CambiarPagina(new LoginPage(banco, gui));
    }

    private void handleGoToBankButton() {
        // TODO: Página ir al banco
        CambiarPagina(null);
    }

    public void handleCloseButton() {
        CambiarPagina(null);
    }
}


class StartMenuView extends PageView {

    private final Button bankLoginButton;
    private final Button goToBankButton;
    private final Button exitButton;

    public StartMenuView(WindowBasedTextGUI gui) {
        super(gui);

        this.bankLoginButton = new Button("Sistema Banco");
        this.goToBankButton = new Button("Ir al banco");
        this.exitButton = new Button("Salir");
    }

    public void startUI() {
        BasicWindow window = new BasicWindow("BANCO LA FAMILIA");
        window.setHints(Arrays.asList(Window.Hint.CENTERED));

        Panel contentPanel = new Panel();
        contentPanel.setPreferredSize(new TerminalSize(30, 10));
        window.setComponent(contentPanel);
        
        ((LinearLayout)contentPanel.getLayoutManager()).setSpacing(1);
        
        contentPanel.addComponent(
            new Label("Qué quiere hacer?")
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center)));
        contentPanel.addComponent(bankLoginButton);
        contentPanel.addComponent(goToBankButton);
        contentPanel.addComponent(exitButton);
        
        
        bankLoginButton.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        goToBankButton.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        exitButton.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        gui.addWindowAndWait(window);
    }

    public void bindBankLoginButton(Runnable action) {
        bankLoginButton.addListener(bankLoginButton -> action.run());
    }

    public void bindGoToBankButton(Runnable action) {
        goToBankButton.addListener(goToBankButton -> action.run());
    }

    public void bindExitButton(Runnable action) {
        exitButton.addListener(closeButton -> action.run());
    }
}
