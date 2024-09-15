package bancolafamilia.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import bancolafamilia.banco.AgenteEspecial;
import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Client;
import bancolafamilia.banco.Operacion;
import bancolafamilia.banco.Gerente;

public class AgenteEspecialMenuPage extends PageController<AgenteEspecialMenuView>{
    private AgenteEspecial agente;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public AgenteEspecialMenuPage(Banco banco, WindowBasedTextGUI gui, AgenteEspecial agente) {
        super(banco, new AgenteEspecialMenuView(gui), gui);

        this.agente = agente;
        
        view.updateName(agente.getNombre());
        view.updateBalance(agente.getBalance());

        view.bindPendingClientsButton(() -> handlePendingClientsButton());
        view.bindExitButton(() -> handleExitButton());

        view.bindSelectPendingClientButton(handleSelectPendingClient);
    }

    private void handlePendingClientsButton() {
        List<Client> clients = new ArrayList<>(banco.getPendingPremiumClients());
        view.showPendingClients(clients);
    }

    private Function<Client, Boolean> handleSelectPendingClient = (Client client) -> {
        Boolean shouldApprove = view.requestConfirmClient();

        if (!shouldApprove)
            return false;
        
        agente.assignTellerToPremiumClient(client, banco.getPendingPremiumClients(), banco.getCajeros());
        return true;
    };


    private void handleExitButton() {
        CambiarPagina(new LoginPage(banco, gui));;
    }
}
