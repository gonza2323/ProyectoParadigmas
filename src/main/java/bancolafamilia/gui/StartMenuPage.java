package bancolafamilia.gui;

import java.util.Arrays;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.GridLayout.Alignment;

import bancolafamilia.banco.*;


class StartMenuPage extends PageController<StartMenuView>{

    public StartMenuPage(Banco banco, WindowBasedTextGUI gui) {
        super(banco, new StartMenuView(gui), gui);
        
        view.bindBankLoginButton(() -> handleBankLoginButton());
        view.bindGoToBankButton(() -> handleGoToBankButton());
        view.bindShowBankStateButton(() -> handleShowBankStateButton());
        view.bindSimulateOpsButton(() -> handleSimulateOpsButton());
        view.bindExitButton(() -> handleCloseButton());
    }

    private void handleBankLoginButton() {
        CambiarPagina(new LoginPage(banco, gui));
    }

    private void handleGoToBankButton() {
        // TODO: Página ir al banco
        CambiarPagina(null);
    }

    private void handleShowBankStateButton() {
        // TODO: Página ir al banco
        CambiarPagina(null);
    }

    private void handleSimulateOpsButton() {
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
    private final Button showBankStateButton;
    private final Button simulateOpsButton;
    private final Button exitButton;

    public StartMenuView(WindowBasedTextGUI gui) {
        super(gui);

        this.bankLoginButton = new Button("Sistema Banco");
        this.goToBankButton = new Button("Ir al banco");
        this.showBankStateButton = new Button("Estado del banco");
        this.simulateOpsButton = new Button(" Simular movimientos ");
        this.exitButton = new Button("Salir");
    }

    public void startUI() {
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.CENTERED));

        Panel panel = new Panel();
        window.setComponent(panel);
        
        int horizontalMargin = 3;
        panel.setLayoutManager(
            new GridLayout(1)
                .setVerticalSpacing(1)
                .setLeftMarginSize(horizontalMargin)
                .setRightMarginSize(horizontalMargin));
        
        LayoutData fill = GridLayout.createHorizontallyFilledLayoutData();
        LayoutData center = GridLayout.createLayoutData(Alignment.CENTER, Alignment.CENTER);
        
        panel.addComponent(
            new Label("BANCO LA FAMILIA")
                .setLayoutData(center));
        
        panel.addComponent(
            new Label("Qué quiere hacer?")
                .setLayoutData(center));
        
        bankLoginButton.setLayoutData(fill).addTo(panel);
        goToBankButton.setLayoutData(fill).addTo(panel);
        showBankStateButton.setLayoutData(fill).addTo(panel);
        simulateOpsButton.setLayoutData(fill).addTo(panel);
        panel.addComponent(new EmptySpace());
        exitButton.setLayoutData(fill).addTo(panel);
        
        gui.addWindowAndWait(window);
    }

    public void bindBankLoginButton(Runnable action) {
        bankLoginButton.addListener(bankLoginButton -> action.run());
    }

    public void bindGoToBankButton(Runnable action) {
        goToBankButton.addListener(goToBankButton -> action.run());
    }

    public void bindShowBankStateButton(Runnable action) {
        showBankStateButton.addListener(showBankStateButton -> action.run());
    }

    public void bindSimulateOpsButton(Runnable action) {
        simulateOpsButton.addListener(simulateOpsButton -> action.run());
    }

    public void bindExitButton(Runnable action) {
        exitButton.addListener(closeButton -> action.run());
    }
}
