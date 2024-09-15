package bancolafamilia.banco;

import java.time.LocalDateTime;

public class TransaccionBolsa extends Operacion {

    public Activo activo;
    private String tipo;
    private int cantidad;
    private float comision;



    //tipo = comprar o vender

    public TransaccionBolsa(LocalDateTime date, Client client, Activo activo, int cantidad, float amount, float comision, String tipo) {
        super(date, client, amount);

        //amount va a contener el monto total de la inversion y activo.getValue() nos da el monto individual de cada activo
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.activo = activo;
        this.comision = comision;


    }

    @Override
    public String getDescription() { //mejorar
        String accion;
        if (tipo.equalsIgnoreCase("buy")){
            accion = "Compra";
        } else {
            accion = "Venta";
        }
        return accion + String.valueOf(cantidad) + activo.getName() + " (valor unitario)" + "\nComisión cobrada: " + comision;
    }

    @Override
    public void realizarOperacion() {

    }

    @Override
    public boolean isAprobadaPor(Empleado employee) {
        return false; //no la aprueba ningun empleado
    }

    @Override
    public float getAmount() {
        return super.getAmount();
    }

    public String getTipo() {
        return tipo;
    }

    public float getComision() {
        return comision;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Activo getActivo() {
        return activo;
    }

}
