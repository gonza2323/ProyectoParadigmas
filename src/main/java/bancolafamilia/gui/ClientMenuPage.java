package bancolafamilia.gui;


import bancolafamilia.TimeSimulation;
import bancolafamilia.banco.*;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

public class ClientMenuPage extends PageController<ClientMenuView>{
    private Client client;
    
    // En esta página, el constructor requiere también un User,
    // que fue el que se logueó, además del banco y la gui
    public ClientMenuPage(Banco banco, WindowBasedTextGUI gui, Client client, TimeSimulation timeSim) {
        super(banco, new ClientMenuView(gui), gui, timeSim);

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

    @Override
    public void update() {
        view.updateBalance(client.getBalance());
        view.updateDeuda(client.getDebt());
    }

    private void handleNotificationsButton() {
        client.markNotificationsRead();
        view.updateNotificationsButton(client.hasNewNotifications());
        view.showNotifications(client.getNotifications());
    }

    private void handleBrokerButton() {

        OPERATION_TYPE_BROKER operationTypeBroker = view.requestOperationTypeBroker();

        if (operationTypeBroker == OPERATION_TYPE_BROKER.INVALID)
            return;


        if (operationTypeBroker == OPERATION_TYPE_BROKER.ADVICE) {
            String msg = banco.broker.provideAdvice(client);
            view.showAdviceMsgBroker(msg);

        } else if (operationTypeBroker == OPERATION_TYPE_BROKER.BUY){
            Activo activo = view.showActivosDisponibles(banco.broker.getActivosDisponibles());

            if (activo == null){return;}

            String amountStr = view.requestAmountAssets();
            int cantidad = Integer.parseInt(amountStr);
            DocumentoInversionBolsa doc = banco.broker.simularOperacionActivos(client, activo, cantidad, "buy");
            boolean comprar = view.showSimulationCompra(doc);

            if (comprar){
                if (banco.operarEnLaBolsa(doc,client,activo,cantidad,"buy")){
                    view.showBuySuccessMsg(doc.getActivo().getName(), doc.getCantidad(), doc.getAmount(), doc.getComisiones());
                    view.updateBalance(client.getBalance());

                } else {
                    view.showInsufficientFundsError();
                }

            }

        } else if (operationTypeBroker == OPERATION_TYPE_BROKER.SELL) {
            Activo activo = view.showPortfolioActivos(client.portfolioActivos.getList());

            if (activo == null){return;}

            DocumentoInversionBolsa docAsociado = banco.getInversionPerClient(activo, client);

            int cantActivosEnCartera;

            if (docAsociado == null){
                cantActivosEnCartera = 0;}
            else{
                cantActivosEnCartera = docAsociado.getCantidad();;
            }

            //float monto = banco.getMontoInversionesAsociadas(activo, client);
            String amountStr = view.requestAmountAssetsVenta();
            int cantidad = Integer.parseInt(amountStr);

            if (cantidad > cantActivosEnCartera){
                view.showValueError();

            } else{

                //cliente, activo, ganancia+monto, cantidad, comision
                DocumentoInversionBolsa doc = banco.broker.simularOperacionActivos(client, activo, cantidad, "sell");

                boolean vender = view.showSimulationVenta(doc);

                if (vender){
                    if (banco.operarEnLaBolsa(doc, client,activo,cantidad,"sell")) {
                        view.showSellSuccessMsg(activo.getName(), cantidad, activo.getValue() * cantidad, doc.getGanancias(), doc.getComisiones());
                        view.updateBalance(client.getBalance());
                    }
                }
            }
        }
    }

    private void handleAdviceButton() {
        OPERATION_TYPE_ADVISOR operationTypeAdvisor = view.requestOperationTypeAdvisor();

        if (operationTypeAdvisor == OPERATION_TYPE_ADVISOR.INVALID)
            return;


        if (operationTypeAdvisor == OPERATION_TYPE_ADVISOR.LEGITIMO) {
            financialAdvice advice = banco.solicitudAsesoriaFinanciera(client, "legitimo");
            view.showAdviceMsgAsesorF(advice.getAdvice());

        } else if (operationTypeAdvisor == OPERATION_TYPE_ADVISOR.PREMIUM) {
            financialAdvice advice = banco.solicitudAsesoriaFinanciera(client, "premium");
            view.showAdviceMsgAsesorF(advice.getAdvice());
        }
    }

    private void handleExitButton() {
        CambiarPagina(new LoginPage(banco, gui, timeSim));;
    }

}


