package bancolafamilia.gui;

import java.util.Arrays;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Separator;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

import bancolafamilia.banco.Banco;
import bancolafamilia.banco.User;

public class LoginPage extends PaginaInterfaz<LoginView>{
    
    public LoginPage(Banco banco, WindowBasedTextGUI gui) {
        super(banco, new LoginView(gui), gui);
    }

    public void handleLoginButton() {
        final String username = view.getUsername();
        final String password = view.getPassword();
        
        if (username.isEmpty()) {
            view.showNoUsernameError();
            return;
        }

        if (password.isEmpty()) {
            view.showNoPasswordError();
            return;
        }
        
        final User user = banco.findUserByUsername(username);
        
        if (user == null || !user.getPassword().equals(password)) {
            view.showIncorrectUserOrPasswordError();
            return;
        }
        
        CambiarPagina(new UserMenuPage(banco, gui));
    }

    public void handleCloseButton() {
        CambiarPagina(null);
    }

    public void procesar() {
        this.view.start_ui(this);
    }
}


class LoginView extends View {
    private final TextBox username;
    private final TextBox password;

    public LoginView(WindowBasedTextGUI gui) {
        super(gui);
        this.username = new TextBox();
        this.password = new TextBox();
    }

    public void showIncorrectUserOrPasswordError() {
        MessageDialog.showMessageDialog(gui, "ERROR", "Usuario o contraseña incorrectos");
    }

    public void showNoPasswordError() {
        MessageDialog.showMessageDialog(gui, "ERROR", "Ingrese una contraseña");
    }

    public void showNoUsernameError() {
        MessageDialog.showMessageDialog(gui, "ERROR", "Ingrese un nombre de usuario");
    }

    public void start_ui(LoginPage presenter) {
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.CENTERED));

        Panel contentPanel = new Panel(new GridLayout(2));
        GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);
        gridLayout.setVerticalSpacing(1);

        Label title = new Label("Ingrese sus datos:");
        title.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.BEGINNING,
                GridLayout.Alignment.BEGINNING,
                true,
                false,
                2,
                1));
        contentPanel.addComponent(title);

        contentPanel.addComponent(
            new Label("Username:")
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));
        contentPanel.addComponent(username
                .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)));

        contentPanel.addComponent(
            new Label("Password:")
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));
        contentPanel.addComponent(password
                .setMask('*')
                .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1))
                .setPreferredSize(new TerminalSize(30, 1)));

        contentPanel.addComponent(
            new Separator(Direction.HORIZONTAL)
                .setLayoutData(
                    GridLayout.createHorizontallyFilledLayoutData(2)));
        contentPanel.addComponent(
            new Button("Close", () -> presenter.handleCloseButton())
                .setLayoutData(
                    GridLayout.createHorizontallyEndAlignedLayoutData(1)));
        contentPanel.addComponent(
            new Button("Log In", () -> presenter.handleLoginButton())
                .setLayoutData(
                    GridLayout.createHorizontallyEndAlignedLayoutData(1)));

        window.setComponent(contentPanel);
        gui.addWindowAndWait(window);
    }

    public String getUsername() {
        return username.getText();
    }

    public String getPassword() {
        return password.getText();
    }
}