package bancolafamilia.gui;

import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Cliente;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ClientMenuPage extends PageController<ClientMenuView>{
    private Cliente client;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public ClientMenuPage(Banco banco, WindowBasedTextGUI gui, Cliente client) {
        super(banco, new ClientMenuView(gui, client.getNombre()), gui);

        this.client = client;

        view.updateBalance(client.getBalance());
        //PageController recibe la genericidad V que hereda de PageView y ahí se define la variable "view" del tipo V
        //por lo tanto todos los metodos que usa view estan en la ClientMenuView que hereda de Pageview
        view.bindTransferButton(() -> handleTransferButton());
        view.bindHistoryButton(() -> handleHistoryButton());
        view.bindLoanButton(() -> handleLoanButton());
        view.bindAdviceButton(() -> handleAdviceButton());
        view.bindExitButton(() -> handleExitButton());
    }

    private void handleTransferButton() {
        String alias = view.requestAlias();

        if (alias == null)
            return;

        Cliente recipient = banco.findClientByAlias(alias);

        if (recipient == null) {
            view.showNonExistantAliasError();
            return;
        }

        if (recipient == client) {
            view.showCantTransferToSelfError();
            return;
        }

        String motivo = view.requestMotivo();

        float amount;
        try {
            amount = Float.parseFloat(view.requestAmount());
        } catch (NumberFormatException e) {
            return;
        } catch (NullPointerException e) {
            return;
        }

        if (client.getBalance() < amount) {
            view.showInsufficientFundsError();
            return;
        }

        boolean success = banco.solicitudTransferencia(client, recipient, amount, motivo);

        if (success) {
            view.updateBalance(client.getBalance());
            view.showSuccessMsg();
        } else {
            view.showTransferError();
        }
    }

    private void handleHistoryButton() {
        // TODO Auto-generated method stub
        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar MOVIMIENTOS");
    }

    private void handleLoanButton() {
        // TODO Auto-generated method stub
        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar PRESTAMOS");
    }

    private void handleAdviceButton() {
        // TODO Auto-generated method stub
        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar ASESORAMIENTO");
    }

    private void handleExitButton() {
        CambiarPagina(new LoginPage(banco, gui));;
    }
}

class ClientMenuView extends PageView {
    
    private final String name;
    private final Label balanceIndicator;

    private final Button transferButton;
    private final Button historyButton;
    private final Button loanButton;
    private final Button adviceButton;
    private final Button exitButton;
    

    public ClientMenuView(WindowBasedTextGUI gui, String name) {
        super(gui);
        this.name = name;
        this.balanceIndicator = new Label("");
        
        transferButton = new Button("Transferencia");
        historyButton = new Button("Movimientos");
        loanButton = new Button("Préstamos");
        adviceButton = new Button("Asesoramiento");
        exitButton = new Button("Salir");
    }

    public void showCantTransferToSelfError() {
        new MessageDialogBuilder()
            .setTitle("ERROR")
            .setText("No puede transferirse a sí mismo")
            .build()
            .showDialog(gui);
    }

    public void showInsufficientFundsError() {
        new MessageDialogBuilder()
            .setTitle("ERROR")
            .setText("Fondos insuficientes")
            .build()
            .showDialog(gui);
    }

    public String requestAlias() {
        return new TextInputDialogBuilder()
            .setTitle("TRANSFERENCIA")
            .setDescription("Ingrese el alias del destinatario")
            .setValidationPattern(Pattern.compile("^[a-zA-Z]+(\\.[a-zA-Z]+)*$"), "Ingrese un alias válido")
            .build()
            .showDialog(gui);
    }

    public String requestMotivo() {
        return new TextInputDialogBuilder()
                .setTitle("Descripcion Transferencia")
                .setDescription("Ingrese una breve descripción")
                .setValidationPattern(Pattern.compile("^([a-zA-Z]+\\s?){1,5}$"), "Maximo 5 palabras")
                .build()
                .showDialog(gui);
    }

    public void showNonExistantAliasError() {
        new MessageDialogBuilder()
            .setTitle("ERROR")
            .setText("Alias inexistente")
            .build()
            .showDialog(gui);
    }

    public String requestAmount() {
        return new TextInputDialogBuilder()
            .setTitle("TRANSFERENCIA")
            .setDescription("Ingrese el monto a transferir")
            .setValidationPattern(Pattern.compile("^\\d+(\\.\\d+)?$"), "Ingrese un monto válido")
            .build()
            .showDialog(gui);
    }

    public void showSuccessMsg() {
        new MessageDialogBuilder()
            .setTitle("")
            .setText("Transferencia exitosa")
            .build()
            .showDialog(gui);
    }

    public void updateBalance(float balance) {
        balanceIndicator.setText("$ " + new DecimalFormat("#.##").format(balance));
    }

    public void showTransferError() {
        // TODO Auto-generated method stub
        new MessageDialogBuilder()
            .setTitle("ERROR")
            .setText("Transferencia Denegada")
            .build()
            .showDialog(gui);
    }

    public void startUI() {
        // Crea una ventana y le dice que se centre
        BasicWindow window = new BasicWindow("BANCO LA FAMILIA");
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN,
                                      Window.Hint.FIT_TERMINAL_WINDOW,
                                      Window.Hint.NO_DECORATIONS));

        // Panel
        Panel contentPanel = new Panel(new GridLayout(2));
        window.setComponent(contentPanel); // IMPORTANTE, si no, no se va a dibujar nada y termina el programa.
        
        // Configuramos la separación entre columnas y filas pa que quede lindo
        GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(1);
        gridLayout.setVerticalSpacing(1);
        
        // Mensaje de bienvenida
        Label welcomeMsg = new Label("Bienvenido: " + name);
        welcomeMsg.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.BEGINNING,     // Alinear izquierda
                GridLayout.Alignment.BEGINNING,     // Alinear arriba
                true,      // Expandirse lo que pueda horizontalmente
                false,       // Expandirse lo que pueda verticalmente
                2,                   // Ocupar 2 columnas
                1));                   // Ocupar 1 fila
        contentPanel.addComponent(welcomeMsg);           // Añadir el componente al panel

        // Balance
        balanceIndicator.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.BEGINNING,     // Alinear izquierda
                GridLayout.Alignment.BEGINNING,     // Alinear arriba
                true,      // Expandirse lo que pueda horizontalmente
                false,       // Expandirse lo que pueda verticalmente
                2,                   // Ocupar 2 columnas
                1));                   // Ocupar 1 fila
        contentPanel.addComponent(balanceIndicator);           // Añadir el componente al panel

        // Añadimos linea separadora horizontal
        contentPanel.addComponent(
            new Separator(Direction.HORIZONTAL)
                .setLayoutData(
                    GridLayout.createHorizontallyFilledLayoutData(2)));
        
        LayoutData leftJustify = GridLayout.createLayoutData(
            GridLayout.Alignment.BEGINNING,
            GridLayout.Alignment.BEGINNING,
            true,
            false,
            2,
            1);

        // Añadimos los botones creados en el constructor
        contentPanel.addComponent(
            transferButton
                .setLayoutData(leftJustify));
        contentPanel.addComponent(
            historyButton
                .setLayoutData(leftJustify));
        contentPanel.addComponent(
            loanButton
                .setLayoutData(leftJustify));
        contentPanel.addComponent(
            adviceButton
                .setLayoutData(leftJustify));
        contentPanel.addComponent(
            exitButton
                .setLayoutData(leftJustify));

        // Agregar ventana a la gui
        gui.addWindowAndWait(window);
    }

    public void bindTransferButton(Runnable action) {
        transferButton.addListener(transferButton -> action.run());
    }

    public void bindHistoryButton(Runnable action) {
        historyButton.addListener(historyButton -> action.run());
    }

    public void bindLoanButton(Runnable action) {
        loanButton.addListener(loanButton -> action.run());
    }

    public void bindAdviceButton(Runnable action) {
        adviceButton.addListener(adviceButton -> action.run());
    }

    public void bindExitButton(Runnable action) {
        exitButton.addListener(closeButton -> action.run());
    }
}
