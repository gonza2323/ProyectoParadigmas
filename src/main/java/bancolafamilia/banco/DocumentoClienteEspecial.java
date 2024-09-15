package bancolafamilia.banco;

public class DocumentoClienteEspecial {
    private Client client;
    private AgenteEspecial agent;
    private DocumentoTransaccionEspecial documentoSimulacion;
    public boolean inProcess = false;

    public DocumentoClienteEspecial(Client client, AgenteEspecial agent) {
        this.client = client;
        this.agent = agent;
    }

    public Client getClient() {
        return client;
    }

    public AgenteEspecial getAgenteEspecial() {
        return agent;
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