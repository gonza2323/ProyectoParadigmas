package bancolafamilia.banco;

public class DocumentoClienteEspecial {
    private Cliente client;
    private float amount;
    private int caja;

    public DocumentoClienteEspecial(Cliente client, float amount, int caja) {
        this.client = client;
        this.amount = amount;
        this.caja = caja;

    }

    public float getAmount() {
        return amount;
    }

    public Cliente getClient() {
        return client;
    }

    public int getCaja() {
        return caja;
    }
}