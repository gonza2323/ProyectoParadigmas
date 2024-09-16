package bancolafamilia.banco;

import java.io.Serializable;

public class DocumentoInversionBolsa implements Serializable {

    private static final long serialVersionUID = 1L;

    private Client client;
    private String tipo;
    private Activo activo;
    public float amount;
    public float comisiones;
    public int cantidad;
    public float ganancias;

    public DocumentoInversionBolsa(Client client, Activo activo, float amount, int cantidad, float comisiones, String tipo) {
        this.client = client;
        this.activo = activo;
        this.tipo = tipo;
        this.amount = amount;
        this.comisiones = comisiones;
        this.cantidad = cantidad;


    }

    public float getComisiones() {
        return comisiones;
    }

    public float getAmount() {
        return amount;
    }

    public Activo getActivo() {
        return activo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setGanancias(float ganancias) {
        this.ganancias = ganancias;
    }

    public float getGanancias() {
        return this.ganancias;
    }
}

