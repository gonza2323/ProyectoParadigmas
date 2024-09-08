package bancolafamilia.banco;

public class Gerente extends Empleado implements IOpBcoEmpleado {


    public Gerente(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }

    @Override
    public void receptSolicitud(Operacion operacion) {
        if (operacion.isAprobadaPor(this)){
            this.aprobarOperacion(operacion);
        }

    }


    public void aprobarOperacion(Operacion operacion) {
        if (operacion instanceof Transferencia){ //si llego esta solicitud es porque la transferencia supera el monto de una transaferias común
            //el gerente verifica que el monto no supere el monto diario
            if (operacion.getMonto() > Transferencia.montoMax){
                operacion.denegar();
            }else{
                operacion.aprobar();
            }

        }
    }
}
