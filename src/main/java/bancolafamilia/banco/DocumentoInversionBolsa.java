package bancolafamilia.banco;

public class DocumentoInversionBolsa {
    private String tipo;
    private Activo activo;
    private float amount;
    private float comisiones;

    public DocumentoInversionBolsa(Activo activo, float amount, float comisiones, String tipo) {
        this.activo = activo;
        this.tipo = tipo;
        this.amount = amount;
        this.comisiones = comisiones;

    }

    public float getComisiones() {
        return comisiones;
    }

    public float getAmount() {
        return amount;
    }

    public String getTipo() {
        return tipo;
    }

    public Activo getActivo() {
        return activo;
    }
}

