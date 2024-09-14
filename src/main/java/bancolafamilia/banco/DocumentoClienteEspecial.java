package bancolafamilia.banco;

public class DocumentoClienteEspecial {
    private Client client;
    private float amount;
    private int caja;
    private DocumentoTransaccionEspecial documentoSimulacion;
    public boolean inProcess = false;

    public DocumentoClienteEspecial(Client client, float amount, int caja) {
        this.client = client;
        this.amount = amount;
        this.caja = caja;

    }

    public float getAmount() {
        return amount;
    }

    public Client getClient() {
        return client;
    }

    public int getCaja() {
        return caja;
    }

    //Asociamos al documento del clienta la info de la simulacion
    public void setDocumentoSimulacion(DocumentoTransaccionEspecial documentoSimulacion) {
        this.documentoSimulacion = documentoSimulacion;
    }

    public DocumentoTransaccionEspecial getDocumentoSimulacion() {
        return documentoSimulacion;
    }

    public void setInProcess(boolean inProcess) {
        this.inProcess = inProcess;
    }


}