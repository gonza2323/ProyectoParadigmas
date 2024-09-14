package bancolafamilia.banco;

public class Activo {

    private String name;
    private String descripcion;
    private float value;
    private float riesgoAsociado;


    public Activo(String name, float value) {
        this.name = name;
        this.value = value;
        this.descripcion = name + ": $" + value;

    }

    public String getName() {
        return name;
    }

    public float getValue() {
        return value;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public float getRiesgoAsociado() {
        return (float)Math.random();
    }
}
