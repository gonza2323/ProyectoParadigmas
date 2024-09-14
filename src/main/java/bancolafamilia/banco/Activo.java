package bancolafamilia.banco;

public class Activo {

    private String name;
    private String descripcion;
    private float value;


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
}
