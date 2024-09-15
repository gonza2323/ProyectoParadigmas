package bancolafamilia.gui;


import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Client;
import bancolafamilia.banco.Operacion;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

public class ClientMenuPage extends PageController<ClientMenuView>{
    private Client client;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public ClientMenuPage(Banco banco, WindowBasedTextGUI gui, Client client) {
        super(banco, new ClientMenuView(gui), gui);

        this.client = client;

        view.updateName(client.getNombre());
        view.updateBalance(client.getBalance());
        view.updateDeuda(client.getDebt());
        view.updateNotificationsButton(client.hasNewNotifications());

        view.bindTransferButton(() -> handleTransferButton());
        view.bindHistoryButton(() -> handleHistoryButton());
        view.bindAliasButton(() -> handleAliasButton());
        view.bindLoanButton(() -> handleLoanButton());
        view.bindBrokerButton(() -> handleBrokerButton());
        view.bindAdviceButton(() -> handleAdviceButton());
        view.bindNotificationsButton(() -> handleNotificationsButton());
        view.bindExitButton(() -> handleExitButton());
    }

    private void handleTransferButton() {
        String alias = view.requestAlias();

        if (alias == null)
            return;

        Client recipient = banco.findClientByAlias(alias);

        if (recipient == null) {
            view.showNonExistantAliasError();
            return;
        }

        if (recipient == client) {
            view.showCantTransferToSelfError();
            return;
        }

        String motivo = view.requestMotivo();

        if (motivo == null)
            return;

        float amount;
        try {
            amount = Float.parseFloat(view.requestTransferAmount());
        } catch (NumberFormatException e) {
            view.showTransferError();
            return;
        } catch (NullPointerException e) {
            return;
        }

        if (amount == 0) {
            view.showTransferError();
            return;
        }
        if (client.getBalance() < amount) {
            view.showInsufficientFundsError();
            return;
        }

        Operacion.OpStatus status = banco.solicitudTransferencia(client, recipient, amount, motivo);

        switch (status) {
            case APPROVED:
                view.updateBalance(client.getBalance());
                view.showTransferSuccessMsg();    
                break;
            case MANUAL_APPROVAL_REQUIRED:
                view.showPendingApprovalMsg();
                break;
            case DENIED:
                view.showTransferError();
                break;
            default:
                break;
        }
    }

    private void handleHistoryButton() {
        view.showHistory(banco.getOperacionesCliente(client));
    }

    private void handleAliasButton() {
        view.showUserAlias(client.getAlias());
    }

    private void handleLoanButton() {

        float minimumLoanAmount = 1000;
        float maxLoanAmount = banco.getMaxClientLoan(client);
        String loanAmountStr = view.requestLoanAmount(maxLoanAmount, banco.getAnualInterestRate());

        if (loanAmountStr == null)
            return;

        float loanAmount;
        try {
            loanAmount = Float.parseFloat(loanAmountStr);
        } catch (NumberFormatException e) {
            view.showLoanError();
            return;
        }
        
        if (loanAmount > maxLoanAmount) {
            view.showNotEnoughCreditError();
            return;
        }

        if (loanAmount < minimumLoanAmount) {
            view.showLoanTooSmallError();
            return;
        }

        String loanLengthInMonthsStr = view.requestLoanMonths();

        if (loanLengthInMonthsStr == null)
            return;

        int loanLengthInMonths;
        try {
            loanLengthInMonths = Integer.parseInt(loanLengthInMonthsStr);
        } catch (NumberFormatException e) {
            view.showLoanError();
            return;
        }

        Operacion.OpStatus status = banco.solicitudPrestamo(client, loanAmount, loanLengthInMonths);

        switch (status) {
            case APPROVED:
                view.updateBalance(client.getBalance());
                view.updateDeuda(client.getDebt());    
                view.showLoanSuccessMsg();
                break;
            case DENIED:
                view.showLoanError();
            default:
                break;
        }
    }

    private void handleNotificationsButton() {
        client.markNotificationsRead();
        view.updateNotificationsButton(client.hasNewNotifications());
        view.showNotifications(client.getNotifications());
    }

    private void handleBrokerButton() {
        view.showErrorDialog("Falta implementar BOLSA");
    }

    private void handleAdviceButton() {
        view.showErrorDialog("Falta implementar ASESORAMIENTO");
    }

    private void handleExitButton() {
        CambiarPagina(new LoginPage(banco, gui));;
    }
}
