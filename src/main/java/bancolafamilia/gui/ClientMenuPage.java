package bancolafamilia.gui;

import java.text.DecimalFormat;
import java.util.Arrays;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LayoutData;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Separator;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Cliente;

public class ClientMenuPage extends PageController<UserMenuView>{
    Cliente client;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public ClientMenuPage(Banco banco, WindowBasedTextGUI gui, Cliente client) {
        super(banco, new UserMenuView(gui, client.getNombre(), client.getBalance()), gui);

        this.client = client;
    }
}

class UserMenuView extends PageView {
    private final String name;
    private final float balance; 

    public UserMenuView(WindowBasedTextGUI gui, String name, float balance) {
        super(gui);
        this.name = name;
        this.balance = balance;
    }

    public void startUI() {
        // Crea una ventana y le dice que se centre
        // Siempre hace falta una ventana (preferentemente solo 1)
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN)); // Centrada, pero hay más opciones

        // La ventana solo puede contener un elemento, entonces
        // creamos un "panel", que puede tener muchos objectos organizados de distintas formas
        // En este caso, que organice todo en 2 columnas
        Panel contentPanel = new Panel(new GridLayout(2));
        window.setComponent(contentPanel); // IMPORTANTE, si no, no se va a dibujar nada y termina el programa.
        
        // Configuramos la separación entre columnas y filas pa que quede lindo
        GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);
        gridLayout.setVerticalSpacing(1);
        
        // Mensaje de bienvenida
        Label welcomeMsg = new Label("Bienvenido: " + name);
        welcomeMsg.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.BEGINNING,     // Alinear izquierda
                GridLayout.Alignment.BEGINNING,     // Alinear arriba
                true,      // Expandirse lo que pueda horzontalmente
                false,       // Expandirse lo que pueda verticalmente
                2,                   // Ocupar 2 columnas
                1));                   // Ocupar 1 fila
        contentPanel.addComponent(welcomeMsg);           // Añadir el componente al panel

        // Balance
        Label balanceIndicator = new Label("$ " + new DecimalFormat("#.##").format(balance));
        balanceIndicator.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.BEGINNING,     // Alinear izquierda
                GridLayout.Alignment.BEGINNING,     // Alinear arriba
                true,      // Expandirse lo que pueda horzontalmente
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
            GridLayout.Alignment.CENTER,
            true,
            true,
            2,
            1);

        // Añadimos los botones creados en el constructor
        contentPanel.addComponent(
            new Button("Transferencia")
                .setLayoutData(leftJustify));
        contentPanel.addComponent(
            new Button("Préstamos")
                .setLayoutData(leftJustify));
        contentPanel.addComponent(
            new Button("Movimientos")
                .setLayoutData(leftJustify));
        contentPanel.addComponent(
            new Button("Salir")
                .setLayoutData(leftJustify));

        // LO MÁS IMPROTANTE. Finalmente se añade la ventana a la pantalla y espera el input
        // del usuario. Si no el programa continua y se cierra, ya que la página siguiente
        // es "null" por defecto.
        gui.addWindowAndWait(window);

        // La ejecución no continúa hasta que se cierre la ventana, que ocurrirá en la lógica
        // del controlador cuando este llame a cambiarPagina(PageController). Mientras tanto,
        // seguirá renderizando la página actual.
    }
}
