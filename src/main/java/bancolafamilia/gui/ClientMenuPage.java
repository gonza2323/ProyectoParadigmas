package bancolafamilia.gui;


import bancolafamilia.banco.Activo;
import bancolafamilia.banco.Banco;
import bancolafamilia.banco.Client;
import bancolafamilia.banco.DocumentoInversionBolsa;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

public class ClientMenuPage extends PageController<ClientMenuView>{
    private Client client;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public ClientMenuPage(Banco banco, WindowBasedTextGUI gui, Client client) {
        super(banco, new ClientMenuView(gui, client.getNombre()), gui);

        this.client = client;

        view.updateBalance(client.getBalance());
        view.updateDeuda(client.getDebt());

        view.bindTransferButton(() -> handleTransferButton());
        view.bindHistoryButton(() -> handleHistoryButton());
        view.bindAliasButton(() -> handleAliasButton());
        view.bindLoanButton(() -> handleLoanButton());
        view.bindBrokerButton(() -> handleBrokerButton());
        view.bindAdviceButton(() -> handleAdviceButton());
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

        boolean success = banco.solicitudTransferencia(client, recipient, amount, motivo);

        if (success) {
            view.updateBalance(client.getBalance());
            view.showTransferSuccessMsg();
        } else {
            view.showTransferError();
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

        boolean success = banco.requestLoan(client, loanAmount, loanLengthInMonths);
        
        if (success) {
            view.updateBalance(client.getBalance());
            view.updateDeuda(client.getDebt());
            view.showLoanSuccessMsg();
        } else {
            view.showLoanError();
        }
    }

    private void handleBrokerButton() {
        // TODO Auto-generated method stub
//        List<String> acciones = new ArrayList<>();
//        acciones.add("Pedir consejo al agente de Bolsa");
//        acciones.add("Comprar acciones");
//        acciones.add("Vender acciones");
//
//        String accion = view.requestAction(acciones);
//
//        if (accion == null) {
//            return;
//        }


        OPERATION_TYPE_BROKER operationTypeBroker = view.requestOperationTypeBroker();

        if (operationTypeBroker == OPERATION_TYPE_BROKER.INVALID)
            return;

        String action = null;
        if (operationTypeBroker == OPERATION_TYPE_BROKER.ADVICE) {
            String msg = banco.broker.provideAdvice(client);
            view.showAdviceMsg(msg);
        } else if (operationTypeBroker == OPERATION_TYPE_BROKER.BUY){
            Activo activo = view.showActivosDisponibles(banco.broker.getActivosDisponibles());
            String amountStr = view.requestAmountAssets();
            int cantidad = Integer.parseInt(amountStr);
            DocumentoInversionBolsa doc = banco.broker.simularOperacionActivos(activo, cantidad, "buy");
            boolean comprar = view.showSimulationActivos(doc);

            if (comprar){
                if (banco.operarEnLaBolsa(client,activo,cantidad,"buy")){
                    view.showBuySuccessMsg(doc.getActivo().getName(), doc.getCantidad(), doc.getAmount(), doc.getComisiones());
                    view.updateBalance(client.getBalance());
                } else {
                    view.showInsufficientFundsError();
                }

            }

        }
    }

//        float amount;
//        try {
//            amount = Float.parseFloat(amountStr);
//        } catch (NumberFormatException e) {
//            view.showInvalidAmountError();
//            return;
//        } catch (NullPointerException e) {
//            return;
//        }
//
//        if (amount <= 0) {
//            view.showInvalidAmountError();
//            return;
//        }
//
//        if (operationType == OPERATION_TYPE.WITHDRAWAL
//                && amount > client.getBalance()) {
//            view.showInsufficientFundsError();
//            return;
//        }
//
//        Boolean success = false;
//        if (operationType == OPERATION_TYPE.DEPOSIT)
//            success =banco.solicitudDeposito(client, amount, caja, null);
//        else if (operationType == OPERATION_TYPE.WITHDRAWAL)
//            success = banco.withdrawFunds(client, amount, null); // TODO arreglar esto
//
//        if (!success) {
//            view.showError();
//            return;
//        }
//
//        view.showSuccessMsg();
//    }
//
//        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar BOLSA");*/
//    }

    private void handleAdviceButton() {
        // TODO Auto-generated method stub
        MessageDialog.showMessageDialog(gui, "ERROR", "Falta implementar ASESORAMIENTO");
    }

    private void handleExitButton() {
        CambiarPagina(new LoginPage(banco, gui));;
    }

}


