package bancolafamilia.banco;

public class Activo {

    private String name;
    private String descripcion;
    private float value;
    private float riesgoAsociado;
    private float ganancias;


    public Activo(String name, float value, float riesgoAsociado) {
        this.name = name;
        this.value = value;
        this.riesgoAsociado = riesgoAsociado;
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

    public float getGanancias() {
        float random = (float)Math.random();
        return this.value *= random;
    }
}
