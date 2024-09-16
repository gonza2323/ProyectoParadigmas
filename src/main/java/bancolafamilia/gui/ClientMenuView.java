package bancolafamilia.gui;

import bancolafamilia.banco.Activo;
import bancolafamilia.banco.DocumentoInversionBolsa;
import bancolafamilia.banco.Operacion;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.table.Table;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ClientMenuView extends PageView {
    
    private final String name;
    private final Label balanceIndicator;
    private final Label deudaIndicator;

    private final Button transferButton;
    private final Button historyButton;
    private final Button aliasButton;
    private final Button loanButton;
    private final Button brokerButton;
    private final Button adviceButton;
    private final Button exitButton;
    //private final Button changePasswordButton;
    

    public ClientMenuView(WindowBasedTextGUI gui, String name) {
        super(gui);
        this.name = name;
        this.balanceIndicator = new Label("");
        this.deudaIndicator = new Label("");
        
        transferButton = new Button("Transferencia");
        historyButton = new Button("Movimientos");
        aliasButton = new Button("Consultar alias");
        loanButton = new Button("Préstamos");
        brokerButton = new Button("Operar en bolsa");
        adviceButton = new Button("Asesoramiento");
        exitButton = new Button("Salir");
    }

    public void startUI() {
        // crea una ventana y le dice que se centre
        BasicWindow window = new BasicWindow("BANCO LA FAMILIA");
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN,
                                      Window.Hint.FIT_TERMINAL_WINDOW,
                                      Window.Hint.NO_DECORATIONS));

        // panel
        Panel panel = new Panel(
            new GridLayout(2)
                .setHorizontalSpacing(1)
                .setVerticalSpacing(1)
                .setTopMarginSize(1)
                .setLeftMarginSize(1));
        window.setComponent(panel); // IMPORTANTE, si no, no se va a dibujar nada y termina el programa.
        
        // configuración layout
        LayoutData horizontalFill = GridLayout.createHorizontallyFilledLayoutData(2);
        
        // Mensaje de bienvenida
        Label welcomeMsg = new Label("Bienvenido: " + name);
        welcomeMsg.setLayoutData(horizontalFill);
        panel.addComponent(welcomeMsg);

        // Balance y deuda
        balanceIndicator.setLayoutData(horizontalFill);
        panel.addComponent(balanceIndicator);

        deudaIndicator.setLayoutData(horizontalFill);
        panel.addComponent(deudaIndicator);

        // Añadimos linea separadora horizontal
        panel.addComponent(
            new Separator(Direction.HORIZONTAL)
                .setLayoutData(horizontalFill));
        
        // layout botones
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
            aliasButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            loanButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            brokerButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            adviceButton
                .setLayoutData(leftJustify));
        panel.addComponent(
            exitButton
                .setLayoutData(leftJustify));

        // Agregar ventana a la gui
        gui.addWindowAndWait(window);
    }

    public void showHistory(List<Operacion> operaciones){

        BasicWindow window = new BasicWindow("Historial de Operaciones");
        window.setHints(
            Arrays.asList(
                Window.Hint.CENTERED,
                Window.Hint.FIT_TERMINAL_WINDOW));
        window.setCloseWindowWithEscape(true);
        
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        window.setComponent(panel);
        
        Table<Object> table = new Table<>("Fecha", "Tipo", "Monto", "Detalle");
        panel.addComponent(table);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);

        for (Operacion op : operaciones) {
            String clase = op.getClass().getSimpleName();
            String fecha = op.getDate().format(formatter);
            String description = op.getDescription();
            String monto = decimalFormat.format(op.getAmount());

            table.getTableModel().addRow(fecha, clase, monto, description);
        }

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

    public void updateBalance(float balance) {
        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);
        balanceIndicator.setText("Saldo: " + decimalFormat.format(balance));
    }

    public void updateDeuda(float deuda) {
        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);
        deudaIndicator.setText("Deuda: " + decimalFormat.format(deuda));
    }

    public String requestAlias() {
        return new TextInputDialogBuilder()
            .setTitle("TRANSFERENCIA")
            .setDescription("Ingrese el alias del destinatario")
            .setValidationPattern(Pattern.compile("^[a-zA-Z]+\\.[a-zA-Z]+\\.[a-zA-Z]+$"), "Ingrese un alias válido")
            .build()
            .showDialog(gui);
    }

    public String requestMotivo() {
        return new TextInputDialogBuilder()
                .setTitle("TRANSFERENCIA")
                .setDescription("Ingrese una breve descripción")
                .setValidationPattern(Pattern.compile("^([a-zA-Z]+\\s?){1,5}$"), "Máximo 5 palabras")
                .build()
                .showDialog(gui);
    }    

    public String requestTransferAmount() {
        return new TextInputDialogBuilder()
            .setTitle("TRANSFERENCIA")
            .setDescription("Ingrese el monto a transferir")
            .setValidationPattern(Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$"), "Ingrese un monto válido")
            .build()
            .showDialog(gui);
    }

    public void showTransferSuccessMsg() {
        showMessageDialog("", "Transferencia exitosa");
    }

    public void showUserAlias(String alias) {
        showMessageDialog("", "Su alias es:\n" + alias);
    }

    public String requestLoanAmount(float maxLoanAmount, float anualInterestRate) {
        NumberFormat currencyFormater = NumberFormat.getCurrencyInstance(Locale.US);
        DecimalFormat percentageFormater = new DecimalFormat("0.00%");

        return new TextInputDialogBuilder()
            .setTitle("SOLICITUD DE PRÉSTAMO")
            .setDescription("Tasa de interés anual: " + percentageFormater.format(anualInterestRate)
                        + "\nFinanciación máxima disponible: " + currencyFormater.format(maxLoanAmount))
            .setValidationPattern(Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$"), "Ingrese un monto válido")
            .build()
            .showDialog(gui);
    }

    public String requestLoanMonths() {
        return new TextInputDialogBuilder()
            .setTitle("SOLICITUD DE PRÉSTAMO")
            .setDescription("Ingrese periodo del préstamo en meses (mínimo 1)")
            .setValidationPattern(Pattern.compile("^[1-9][0-9]*$"), "Ingrese una cantidad de meses válida")
            .build()
            .showDialog(gui);
    }

    public Activo showActivosDisponibles(List<Activo> activos) {
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(3));  // 3 columnas: Nombre, Precio, Botón

        // Agregar encabezados de la tabla
        panel.addComponent(new Label("Activo"));
        panel.addComponent(new Label("Precio"));
        panel.addComponent(new Label("Simular"));

        final Activo[] selectedActivo = {null};
        final String[] amountStr = {null};

        // Crear una ventana que contenga el panel
        BasicWindow window = new BasicWindow("Acciones Disponibles Para Compra");

        // Agregar las filas de la tabla con el botón "Simular"
        for (Activo activo : activos) {
            // Columna 1: Nombre del activo
            panel.addComponent(new Label(activo.getName()));

            // Columna 2: Precio del activo
            panel.addComponent(new Label(String.valueOf(activo.getValue())));

            // Columna 3: Botón "Simular"
            Button simularButton = new Button("Simular", () -> { selectedActivo[0] = activo;
                window.close();

                // Acción a realizar cuando se presiona el botón
                //System.out.println("Simulando la compra de: " + activo.getName());
            });
            panel.addComponent(simularButton);
        }

        window.setComponent(panel);

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
        return selectedActivo[0];
    }

    public Activo showPortfolioActivos(List<Activo> activos) {
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(3));  // 3 columnas: Nombre, Precio, Botón

        // Agregar encabezados de la tabla
        panel.addComponent(new Label("Activo"));
        panel.addComponent(new Label("Precio"));
        panel.addComponent(new Label("Simular Venta"));

        final Activo[] selectedActivo = {null};
        final String[] amountStr = {null};

        // Crear una ventana que contenga el panel
        BasicWindow window = new BasicWindow("Resumen de Activos");

        // Agregar las filas de la tabla con el botón "Simular"
        for (Activo activo : activos) {
            // Columna 1: Nombre del activo
            panel.addComponent(new Label(activo.getName()));

            // Columna 2: Precio del activo
            panel.addComponent(new Label(String.valueOf(activo.getValue())));

            // Columna 3: Botón "Simular"
            Button simularButton = new Button("Simular Venta", () -> { selectedActivo[0] = activo;
                window.close();

                // Acción a realizar cuando se presiona el botón
                //System.out.println("Simulando la compra de: " + activo.getName());
            });
            panel.addComponent(simularButton);
        }

        window.setComponent(panel);

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
        return selectedActivo[0];
    }

    public boolean showSimulationCompra(DocumentoInversionBolsa doc){
        BasicWindow window = new BasicWindow("Simulacion de Inversión de Activos");
        window.setHints(
                Arrays.asList(
                        Window.Hint.CENTERED,
                        Window.Hint.FIT_TERMINAL_WINDOW));
        window.setCloseWindowWithEscape(true);

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));


        Table<Object> table = new Table<>("Activo", "PrecioUnitario", "Cantidad", "Comisiones", "PrecioFinal", "Riesgo" );

        panel.addComponent(table);

        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String comisiones = decimalFormat.format(doc.getComisiones());
        String precioUnitario = decimalFormat.format(doc.getActivo().getValue());
        float precio = doc.getAmount() + doc.getComisiones();
        String precioFinal = decimalFormat.format(precio);



        table.getTableModel().addRow(new Object[]{doc.getActivo().getName(), precioUnitario, doc.getCantidad(), comisiones, precioFinal, doc.getActivo().getRiesgoAsociado()});


//        panel.addComponent(new Button("Cerrar",
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        window.close();
//                    }
//                }).setLayoutData(
//                GridLayout.createLayoutData(
//                        GridLayout.Alignment.END,
//                        GridLayout.Alignment.CENTER))
//        );

        final boolean[] flag = {false};

        panel.addComponent(new Button("Comprar",
                new Runnable() {
                    @Override
                    public void run() {
                        flag[0] = true;
                        window.close();
                    }
                }).setLayoutData(
                GridLayout.createLayoutData(
                        GridLayout.Alignment.CENTER,
                        GridLayout.Alignment.END))
        );

        TerminalSize preferredSize = panel.calculatePreferredSize();
        panel.setPreferredSize(preferredSize);


        window.setComponent(panel);


        gui.addWindowAndWait(window);
        return flag[0];
    }

    public boolean showSimulationVenta(DocumentoInversionBolsa doc){
        BasicWindow window = new BasicWindow("Simulacion de Venta de Activos");
        window.setHints(
                Arrays.asList(
                        Window.Hint.CENTERED,
                        Window.Hint.FIT_TERMINAL_WINDOW));
        window.setCloseWindowWithEscape(true);

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));


        Table<Object> table = new Table<>("Activo", "PrecioUnitario", "Cantidad", "Ganancias", "PrecioFinal", "Comisiones", "Riesgo" );

        panel.addComponent(table);

        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String comisionesF = decimalFormat.format(doc.getComisiones());
        String precioUnitario = decimalFormat.format(doc.getActivo().getValue());
        String precioFinal = decimalFormat.format(doc.getAmount());
        String ganancias = decimalFormat.format(doc.getGanancias());




        table.getTableModel().addRow(new Object[]{doc.getActivo().getName(), precioUnitario, doc.getCantidad(), ganancias, precioFinal, comisionesF, doc.getActivo().getRiesgoAsociado()});


//        panel.addComponent(new Button("Cerrar",
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        window.close();
//                    }
//                }).setLayoutData(
//                GridLayout.createLayoutData(
//                        GridLayout.Alignment.END,
//                        GridLayout.Alignment.CENTER))
//        );

        final boolean[] flag = {false};

        panel.addComponent(new Button("Vender",
                new Runnable() {
                    @Override
                    public void run() {
                        flag[0] = true;
                        window.close();
                    }
                }).setLayoutData(
                GridLayout.createLayoutData(
                        GridLayout.Alignment.CENTER,
                        GridLayout.Alignment.END))
        );

        TerminalSize preferredSize = panel.calculatePreferredSize();
        panel.setPreferredSize(preferredSize);


        window.setComponent(panel);


        gui.addWindowAndWait(window);
        return flag[0];
    }



    public OPERATION_TYPE_BROKER requestOperationTypeBroker() {
        ActionListDialogBuilder builder = new ActionListDialogBuilder();

        final OPERATION_TYPE_BROKER[] selectedOperation = { OPERATION_TYPE_BROKER.INVALID };

        builder.addAction("Pedir Consejo al Agente de Bolsa", () -> {selectedOperation[0] = OPERATION_TYPE_BROKER.ADVICE;});
        builder.addAction("Comprar", () -> {selectedOperation[0] = OPERATION_TYPE_BROKER.BUY;});
        builder.addAction("Vender", () -> {selectedOperation[0] = OPERATION_TYPE_BROKER.SELL;});

        builder
                .setTitle("Qué operación desea realizar?")
                .build()
                .showDialog(gui);

        return selectedOperation[0];
    }

    public OPERATION_TYPE_ADVISOR requestOperationTypeAdvisor() {
        ActionListDialogBuilder builder = new ActionListDialogBuilder();

        final OPERATION_TYPE_ADVISOR[] selectedOperation = { OPERATION_TYPE_ADVISOR.INVALID};

        builder.addAction("Asesoría en Inversiones", () -> {selectedOperation[0] = OPERATION_TYPE_ADVISOR.LEGITIMO;});
        builder.addAction("Mover dinero discretamente", () -> {selectedOperation[0] = OPERATION_TYPE_ADVISOR.PREMIUM;});

        builder
                .setTitle("¿Qué tipo de asesoría financiera necesita?")
                .build()
                .showDialog(gui);

        return selectedOperation[0];
    }

    public String requestAmountAssets() {
        return new TextInputDialogBuilder()
                .setTitle("Compra de Activos")
                .setDescription("Ingrese la cantidad de activos que desea comprar")
                .setValidationPattern(Pattern.compile("^(10|[1-9])$"), "Puede comprar minimo una acción y máximo 10")
                .build()
                .showDialog(gui);
    }

    public String requestAmountAssetsVenta() {
        return new TextInputDialogBuilder()
                .setTitle("Venta de Activos")
                .setDescription("Ingrese la cantidad de activos que desea vender")
                .setValidationPattern(Pattern.compile("^(10|[1-9])$"), "Verifique el monto ingresado")
                .build()
                .showDialog(gui);
    }

    public void showBuySuccessMsg(String name, int cantidad, float monto, float comisiones) {
        showMessageDialog("¡Compra Exitosa!", "Has adquirido " + cantidad + " activo(s) de " + name + " por un costo total de " + monto + "\nComision cobrada: $" + comisiones);
    }

    public void showSellSuccessMsg(String name, int cantidad, float monto, float ganancias, float comisiones) {
        showMessageDialog("¡Venta Exitosa!", "Has vendido " + cantidad + " activo(s) de " + name + " por un costo total de " + monto + "\nTus ganancias han sido de $" + ganancias + "\nComision cobrada: $" + comisiones);
    }

    public void showValueError(){
        showErrorDialog("La cantidad ingresada supera la cantidad de activos en su cartera");
    }

    public void showNotEnoughCreditError() {
        showErrorDialog("No tiene acceso a préstamos tan grandes");
    }

    public void showLoanTooSmallError() {
        showErrorDialog("El préstamo debe ser de al menos $1,000");
    }

    public void showLoanError() {
        showErrorDialog("Se produjo un error");
    }

    public void showLoanSuccessMsg() {
        showMessageDialog("SOLICITUD DE PRÉSTAMO", "Se le ha otorgado el préstamo solicitado");
    }

    public void showNonExistantAliasError() {
        showErrorDialog("Alias inexistente");
    }

    public void showCantTransferToSelfError() {
        showErrorDialog("No puede transferirse a sí mismo");
    }

    public void showInsufficientFundsError() {
        showErrorDialog("Fondos insuficientes");
    }

    public void showTransferError() {
        showErrorDialog("Transferencia denegada");
    }

    public void showAdviceMsgBroker(String msg) {
        showMessageDialog("Agente de Bolsa", msg);
    }

    public void showAdviceMsgAsesorF(String msg) {
        showMessageDialog("Asesor Financiero", msg);
    }

    public void bindTransferButton(Runnable action) {
        transferButton.addListener(transferButton -> action.run());
    }

    public void bindHistoryButton(Runnable action) {
        historyButton.addListener(historyButton -> action.run());
    }

    public void bindAliasButton(Runnable action) {
        aliasButton.addListener(aliasButton -> action.run());
    }

    public void bindLoanButton(Runnable action) {
        loanButton.addListener(loanButton -> action.run());
    }

    public void bindBrokerButton(Runnable action) {
        brokerButton.addListener(brokerButton -> action.run());
    }

    public void bindAdviceButton(Runnable action) {
        adviceButton.addListener(adviceButton -> action.run());
    }

    public void bindExitButton(Runnable action) {
        exitButton.addListener(closeButton -> action.run());
    }




}
enum OPERATION_TYPE_BROKER {
    ADVICE,
    BUY,
    SELL,
    INVALID
}

enum OPERATION_TYPE_ADVISOR {
    LEGITIMO,
    PREMIUM,
    INVALID
}