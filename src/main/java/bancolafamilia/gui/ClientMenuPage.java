package bancolafamilia.gui;

import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Cliente;
import bancolafamilia.banco.Operacion;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.table.Table;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Pattern;

public class ClientMenuPage extends PageController<ClientMenuView>{
    private Cliente client;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public ClientMenuPage(Banco banco, WindowBasedTextGUI gui, Cliente client) {
        super(banco, new ClientMenuView(gui, client.getNombre()), gui);

        this.client = client;

        view.updateBalance(client.getBalance());
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
        view.showHistory(client.getOperaciones());
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

//cuando la clase no tiene modificador de acceso, el acceso por defecto es el "acceso por paquete" o package private lo que quiere decir que la clase solo sera accesible dentro del mismo paquete. O sea, solo puede acceder a ella la clase clientMenuPage
class ClientMenuView extends PageView {
    
    private final String name;
    private final Label balanceIndicator;

    private final Button transferButton;
    private final Button historyButton;
    private final Button loanButton;
    private final Button adviceButton;
    private final Button exitButton;
    //private final Button changePasswordButton;
    

    public ClientMenuView(WindowBasedTextGUI gui, String name) {
        super(gui);
        this.name = name;
        this.balanceIndicator = new Label("");
        
        transferButton = new Button("Transferencia");
        historyButton = new Button("Movimientos");
        loanButton = new Button("Préstamos");
        adviceButton = new Button("Asesoramiento");
        //changePasswordButton = new Button("Cambiar contraseña");
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
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedAmount = currencyFormat.format(balance);
        balanceIndicator.setText("Saldo: " + formattedAmount);
    }

    public void showTransferError() {
        // TODO Auto-generated method stub
        new MessageDialogBuilder()
            .setTitle("ERROR")
            .setText("Transferencia denegada") //antes: "Se produjo un error desconocido"
            .build()
            .showDialog(gui);
    }

    public void showHistory(LinkedList<Operacion> operaciones){

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));
        Table<Object> table = new Table<>("Fecha", "Monto", "Tipo");

        int longitud = operaciones.size();

        for(int i = 0; i < longitud; i++){

            String clase = operaciones.get(i).getClass().getSimpleName();
            String fecha = operaciones.get(i).getFecha().toString();
            float monto = operaciones.get(i).getMonto();

            table.getTableModel().addRow(new Object[]{fecha,monto, clase});
        }


        panel.addComponent(table);


        BasicWindow window = new BasicWindow("Historial de Operaciones");
        window.setComponent(panel);
        Button closeButton = new Button("Cerrar", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        });

        panel.addComponent(closeButton);
        gui.addWindowAndWait(window);
    }

    public void setupUI() {
        // Crea una ventana y le dice que se centre
        mainWindow.setHints(Arrays.asList(Window.Hint.FIT_TERMINAL_WINDOW,
                                      Window.Hint.NO_DECORATIONS,
                                      Window.Hint.FULL_SCREEN));

        // Panel
        Panel panel = new Panel();
        mainWindow.setComponent(panel);
        
        // Configuramos la separación entre columnas y filas pa que quede lindo
        panel.setLayoutManager(new GridLayout(2)
            .setHorizontalSpacing(1)
            .setVerticalSpacing(1)
            .setTopMarginSize(1)
            .setLeftMarginSize(2)
            .setRightMarginSize(2));
        
        LayoutData horizontalFill2columns = GridLayout.createHorizontallyFilledLayoutData(2);
        
        
        // Mensaje de bienvenida
        panel.addComponent(
            new Label("Bienvenido: " + name)
            .setLayoutData(horizontalFill2columns));

        // Balance
        panel.addComponent(
            balanceIndicator
            .setLayoutData(horizontalFill2columns));

        // Añadimos linea separadora horizontal
        panel.addComponent(
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
        panel.addComponent(
            transferButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            historyButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            loanButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            adviceButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            exitButton
                .setLayoutData(leftJustify));
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
