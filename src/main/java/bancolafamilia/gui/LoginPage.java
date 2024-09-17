package bancolafamilia.gui;

import bancolafamilia.TimeSimulation;
import bancolafamilia.banco.*;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

import java.util.Arrays;


/*
 * Clase del menú de login. Hereda de PageController<LoginView>.
 * Maneja la lógica de la página
 * Tiene dos métodos, uno para manejar cuando se aprieta
 * el botón "cerrar" y otro para el botón "login".
 * Estos métodos se "atan" a los botones de la clase de la vista
 * (LoginView) en el constructor
*/
class LoginPage extends PageController<LoginView>{

    // Podríamos tener atributos si hiciesen falta
    // ...
    // En este caso, con "banco" es suficiente
    
    // El constructer super(), siempre requiere Banco, Vista y gui.
    // Otra página podría requerir más cosas.
    public LoginPage(Banco banco, WindowBasedTextGUI gui, TimeSimulation timeSim) {
        super(banco, new LoginView(gui), gui, timeSim); // Constructor clase base

        // Le decimos a LoginView, qué acciones ejecutar (de esta clase),
        // cuando se aprieta cada botón
        view.bindLoginButton(() -> handleLoginButton());
        view.bindCloseButton(() -> handleCloseButton());
    }

    // Método para manejar cuando el usuario aprieta Login
    public void handleLoginButton() {

        // Obtenemos lo escrito por el usuario en cada campo
        // Son métodos particulares de la LoginView
        final String username = view.getUsernameField();
        final String password = view.getPasswordField();
        
        if (username.isEmpty()) {
            view.showNoUsernameError(); // otro método de LoginView
            return;
        }

        if (password.isEmpty()) {
            view.showNoPasswordError(); // otro método de LoginView
            return;
        }
        
        // Buscamos si existe el usuario en el banco
        final User user = banco.findUserByUsername(username);
        
        // Si el usuario no existe o la contraseña es errónea, error
        if (user == null || !user.getPassword().equals(password)) {
            view.showIncorrectUserOrPasswordError(); // otro método de LoginView
            return;
        }
        
        // Si todo sale bien, pasamos a la siguiente página
        PageController<?> nextPage;
        
        if (user instanceof Client)
            nextPage = new ClientMenuPage(banco, gui, (Client)user, timeSim);
        else if (user instanceof Cajero)
            nextPage = new CajeroMenuPage(banco, gui, (Cajero)user, timeSim);
        else if (user instanceof AsesorFinanciero)
            nextPage = new AsesorFinancieroMenuPage(banco, gui, (AsesorFinanciero)user, timeSim);
        else if (user instanceof AgenteEspecial)
            nextPage = new AgenteEspecialMenuPage(banco, gui, (AgenteEspecial)user, timeSim);
        else if (user instanceof Gerente)
            nextPage = new ManagerMenuPage(banco, gui, (Gerente)user, timeSim);
        else
            nextPage = null;
        
        CambiarPagina(nextPage);
    }

    // Método para manejar cuando el usuario aprieta Cerrar
    public void handleCloseButton() {
        CambiarPagina(new StartMenuPage(banco, gui, timeSim)); // Cambiamos de página a null (termina el programa)
    }
}


/*
 * Clase de la vista del menú de Login. Hereda de PageView.
 * Maneja el dibujado de la interfaz y tiene métodos para
 * que el controlador (LoginPage) configure sus botones
 * y pueda leer los campos de texto (usuario y contraseña)
*/
class LoginView extends PageView {

    // Creamos atributos para los elementos de la interfaz
    // a los que luego debamos acceder. No hace falta para
    // elementos que no nos interesa leer o modificar como
    // texto estático de la interfaz.

    private final TextBox usernameField;     // input texto username
    private final TextBox passwordField;     // input texto contraseña
    private final Button loginButton;   // botón de login
    private final Button closeButton;   // botón de salir

    // Constructor, necesita siempre la gui. Puede que otras
    // paginas requieran más cosas
    public LoginView(WindowBasedTextGUI gui) {
        super(gui);
        this.usernameField = new TextBox();
        this.passwordField = new TextBox();
        this.loginButton = new Button("Login");
        this.closeButton = new Button("Close");
    }

    // Métodos para mostrar errores. Son llamados por el contorlador (LoginPage)
    public void showIncorrectUserOrPasswordError() {
        showErrorDialog("Usuario o contraseña incorrectos");
    }

    public void showNoPasswordError() {
        showErrorDialog("Ingrese una contraseña");
    }

    public void showNoUsernameError() {
        showErrorDialog("Ingrese un nombre de usuario");
    }

    // Métodos que nos permiten configurar que pasa cuando apretamos los botones
    // Ver el controlador para ver cómo se pasa un método.
    public void bindLoginButton(Runnable action) {
        loginButton.addListener(loginButton -> action.run());
    }

    public void bindCloseButton(Runnable action) {
        closeButton.addListener(closeButton -> action.run());
    }


    // Métodos para leer los campos username y password
    public String getUsernameField() {
        return usernameField.getText();
    }

    public String getPasswordField() {
        return passwordField.getText();
    }

    // Implementación de setupUI() para LoginView
    public void setupUI() {

        // Crea una ventana y le dice que se centre
        // Siempre hace falta una ventana (preferentemente solo 1)
        mainWindow = new BasicWindow("BANCO LA FAMILIA");
        mainWindow.setHints(Arrays.asList(Window.Hint.CENTERED)); // Centrada, pero hay más opciones
        mainWindow.setCloseWindowWithEscape(true);

        // La ventana solo puede contener un elemento, entonces
        // creamos un "panel", que puede tener muchos objectos organizados de distintas formas
        // En este caso, que organice todo en 2 columnas
        Panel contentPanel = new Panel(new GridLayout(2));
        mainWindow.setComponent(contentPanel); // IMPORTANTE, si no, no se va a dibujar nada y termina el programa.
        
        // Configuramos la separación entre columnas y filas pa que quede lindo
        GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(1);
        gridLayout.setVerticalSpacing(1);
        
        // Crea una etiqueta (texto) y configura como se va a organizar
        // dentro del panel
        Label title = new Label("Ingrese sus datos:");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.BEGINNING,     // Alinear izquierda
                GridLayout.Alignment.BEGINNING,     // Alinear arriba
                true,      // Expandirse lo que pueda horzontalmente
                false,       // Expandirse lo que pueda verticalmente
                2,                   // Ocupar 2 columnas
                1));                   // Ocupar 1 fila
        contentPanel.addComponent(title);           // Añadir el componente al panel

        // Etiqueta: username
        contentPanel.addComponent(
            new Label("Usuario:")
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));
        
        // Añadimos el campo username creado en el constructor
        contentPanel.addComponent(
            usernameField
                .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)));
                // Este layout hace que ocupe todo el espacio horizontalmente de la columna

        // Etiqueta: password
        contentPanel.addComponent(
            new Label("Contraseña:")
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));
        
        // Añadimos el campo password creado en el constructor
        contentPanel.addComponent(
            passwordField
                .setMask('*')
                .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1))
                .setPreferredSize(new TerminalSize(30, 1))); // Tamaño "preferido", puede que no lo cumpla

        // Añadimos linea separadora horizontal
        contentPanel.addComponent(
            new Separator(Direction.HORIZONTAL)
                .setLayoutData(
                    GridLayout.createHorizontallyFilledLayoutData(2)));
        
        // Añadimos los botones creados en el constructor
        contentPanel.addComponent(
            closeButton
                .setLayoutData(
                    GridLayout.createHorizontallyEndAlignedLayoutData(1)));
        contentPanel.addComponent(
            loginButton
                .setLayoutData(
                    GridLayout.createHorizontallyEndAlignedLayoutData(1)));

    }

    @Override
    public void setupClockUI() {
        super.setupClockUI();
        Window clockBarWindow = new BasicWindow();
        clockBarWindow.setHints(
            Arrays.asList(Window.Hint.FIT_TERMINAL_WINDOW,
                          Window.Hint.NO_DECORATIONS,
                          Window.Hint.NO_POST_RENDERING,
                          Window.Hint.NO_FOCUS,
                          Window.Hint.FIXED_POSITION));
                          
        clockBarWindow.setPosition(new TerminalPosition(0, 0));
        clockBarWindow.setComponent(clockBarPanel);
        gui.addWindow(clockBarWindow);
    }
}
