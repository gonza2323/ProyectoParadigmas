package bancolafamilia.gui;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.*;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.dialogs.*;

import bancolafamilia.banco.Client;
import bancolafamilia.banco.Empleado;
import bancolafamilia.banco.Operacion;

class AgenteEspecialMenuView extends PageView {
    private final Label welcomeMessageLabel = new Label("");
    
    private final Label balanceIndicator = new Label("");

    private final Button pendingDocumentsButton = new Button("Documentos Pendientes");
    private final Button finishedDocumentsButton = new Button("Documentos Ejecutados");
    private final Button pendingClientsButton = new Button("Clientes disponibles");
    private final Button currentClientsButton = new Button("Clientes actuales");
    private final Button exitButton = new Button("Salir");

    private Function<Client, Boolean> selectPendingClient;

    public AgenteEspecialMenuView(WindowBasedTextGUI gui) {
        super(gui);
    }

    public Boolean requestConfirmClient() {
        var response = new MessageDialogBuilder()
            .setTitle("ACEPTAR CLIENTE")
            .setText("Confirme que desea aceptar al cliente")
            .addButton(MessageDialogButton.No)
            .addButton(MessageDialogButton.Yes)
            .build()
            .showDialog(gui);
        
        switch (response) {
            case Yes:
                return true;
            case No:
                return false;
            default:
                return false;
        }
    }

    public void startUI() {
        // Crea una ventana y le dice que se centre
        BasicWindow window = new BasicWindow("BANCO LA FAMILIA");
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN,
                                      Window.Hint.FIT_TERMINAL_WINDOW,
                                      Window.Hint.NO_DECORATIONS));

        // Panel
        Panel panel = new Panel(
            new GridLayout(1)
                .setVerticalSpacing(1)
                .setTopMarginSize(1)
                .setBottomMarginSize(1)
                .setLeftMarginSize(2)
                .setRightMarginSize(1));
        window.setComponent(panel); // IMPORTANTE, si no, no se va a dibujar nada y termina el programa.
        
        // Layout
        
        LayoutData leftJustify = GridLayout.createLayoutData(
            GridLayout.Alignment.BEGINNING,
            GridLayout.Alignment.CENTER);
        
        LayoutData horizontalFill = GridLayout.createHorizontallyFilledLayoutData(2);
        
        // Mensaje de bienvenida
        welcomeMessageLabel.setLayoutData(leftJustify);                   // Ocupar 1 fila
        panel.addComponent(welcomeMessageLabel);

        // Balance
        balanceIndicator.setLayoutData(leftJustify);                   // Ocupar 1 fila
        panel.addComponent(balanceIndicator);

        // Separador
        panel.addComponent(
            new Separator(Direction.HORIZONTAL)
                .setLayoutData(horizontalFill));
        
        // Botones
        panel.addComponent(
            pendingDocumentsButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            finishedDocumentsButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            pendingClientsButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            currentClientsButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            exitButton
                .setLayoutData(leftJustify));

        
        gui.addWindowAndWait(window);
    }

    public void showPendingClients(List<Client> clients) {
        BasicWindow window = new BasicWindow("OPERACIONES POR APROBAR");
        window.setHints(
            Arrays.asList(
                Window.Hint.CENTERED,
                Window.Hint.FIT_TERMINAL_WINDOW));
        window.setCloseWindowWithEscape(true);
        
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        window.setComponent(panel);
        
        Table<Object> table = new Table<>("Id", "Nombre", "DNI", "Usuario", "Alias", "Balance", "Deuda");
        panel.addComponent(table);

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

        for (Client c : clients) {
            String id = Integer.toString(c.getId());
            String name = c.getNombre();
            String dni = numberFormat.format(c.getDNI());
            String username = c.getUsername();
            String alias = c.getAlias();
            String balance = currencyFormatter.format(c.getBalance());
            String debt = currencyFormatter.format(c.getDebt());

            table.getTableModel().addRow(id, name, dni, username, alias, balance, debt);
        }

        table.setSelectAction(() -> {
            if (table.getTableModel().getRowCount() == 0)
                return;
            
            String idStr = (String)table.getTableModel().getCell(0, table.getSelectedRow());
            int id = Integer.parseInt(idStr);

            for (Client c : clients) {
                if (c.getId() == id) {
                    Boolean shouldRemoveRow = selectPendingClient.apply(c);

                    if (shouldRemoveRow) {
                        table.getTableModel().removeRow(table.getSelectedRow());
                    }
                }
            }
        });

        panel.addComponent(new Button("Cerrar",
            new Runnable() {
                @Override
                public void run() {
                    window.close();
                }
        }).setLayoutData(
            GridLayout.createLayoutData(
                GridLayout.Alignment.END,
                GridLayout.Alignment.CENTER))
        );

        gui.addWindowAndWait(window);
    }

    public void updateName(String name) {
        welcomeMessageLabel.setText("Bienvenido: " + name);
    }
    
    public void updateBalance(float balance) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        balanceIndicator.setText("Balance " + currencyFormatter.format(balance));
    }

    public void bindPendingDocumentsButton(Runnable action) {
        this.pendingDocumentsButton.addListener(b -> action.run());
    }

    public void bindFinishedDocumentsButton(Runnable action) {
        this.finishedDocumentsButton.addListener(b -> action.run());
    }

    public void bindPendingClientsButton(Runnable action) {
        this.pendingClientsButton.addListener(b -> action.run());
    }

    public void bindCurrentClientsButton(Runnable action) {
        this.currentClientsButton.addListener(b -> action.run());
    }

    public void bindExitButton(Runnable action) {
        this.exitButton.addListener(b -> action.run());
    }

    public void bindSelectPendingClientButton(Function<Client, Boolean> action) {
        this.selectPendingClient = action;
    }
}
