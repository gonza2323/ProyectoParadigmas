package bancolafamilia.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import bancolafamilia.TimeSimulation;
import bancolafamilia.banco.AgenteEspecial;
import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Client;
import bancolafamilia.banco.Operacion;
import bancolafamilia.banco.Gerente;

public class AgenteEspecialMenuPage extends PageController<AgenteEspecialMenuView>{
    private AgenteEspecial agente;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public AgenteEspecialMenuPage(Banco banco, WindowBasedTextGUI gui, AgenteEspecial agente, TimeSimulation timeSim) {
        super(banco, new AgenteEspecialMenuView(gui), gui, timeSim);

        this.agente = agente;
        
        view.updateName(agente.getNombre());
        view.updateBalance(agente.getBalance());

        view.bindPendingClientsButton(() -> handlePendingClientsButton());
        view.bindCurrentClientsButton(() -> handleCurrentClientsButton());
        view.bindExitButton(() -> handleExitButton());

        view.bindSelectPendingClientButton(handleSelectPendingClient);
    }

    private void handlePendingClientsButton() {
        List<Client> clients = new ArrayList<>(banco.getPendingPremiumClients());
        view.showPendingClients(clients);
    }

    private void handleCurrentClientsButton() {
        List<Client> clients = new ArrayList<>(agente.getAssignedClients());
        view.showCurrentClients(clients);
    };

    private Function<Client, Boolean> handleSelectPendingClient = (Client client) -> {
        Boolean shouldApprove = view.requestConfirmClient();

        if (!shouldApprove)
            return false;
        
        agente.acceptClient(client, banco.getPendingPremiumClients(), banco.getCajeros());
        return true;
    };

    private Function<Client, Boolean> handleSelectDocument = (Client client) -> {
        return false;
    };

    private void handleExitButton() {
        CambiarPagina(new LoginPage(banco, gui, timeSim));;
    }
}
