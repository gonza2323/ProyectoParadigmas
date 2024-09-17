package bancolafamilia.banco;

import java.io.Serializable;

/**
 * Representa un activo en la bolsa.
 */
public class Activo implements Serializable {

    private static final long serialVersionUID = 1L;

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
        this.ganancias = value * random;
        return this.ganancias;
    }
}
