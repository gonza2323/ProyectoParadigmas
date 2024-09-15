package bancolafamilia.banco;

public class Gerente extends Empleado {

    public static final String motivoEspecial = "honorarios";
    public static final int montoEspecial = 20; //monto maximo que debe transferir el mafioso al agente especial
    private static AgenteEspecial asistente; //tiene un "asistente ejecutivo que es el que hace las tareas de lavado "


    public Gerente(String nombre, int dni, String username, String password, AgenteEspecial asistente) {
        super(nombre, dni, username, password);
        this.asistente = asistente; //El gerente tiene sus asistentes
    }

    public static AgenteEspecial getAsistente() {
        return asistente;
    }

    // @Override
    // public void recieveSolicitud(Operacion operacion) {
    //     if (operacion.isAprobadaPor(this)){
    //         this.aprobarOperacion(operacion);
    //     }
    // }

    // public void aprobarOperacion(Operacion operacion) {
    //     if (operacion instanceof Transferencia) { //si llego esta solicitud es porque la transferencia supera el monto de una transaferias común
    //         //el gerente verifica que el monto no supere el monto diario
    //         if (operacion.getAmount() > Transferencia.maxAmount) {
    //             operacion.denegar();
    //         } else if (operacion.getAmount() < Gerente.montoEspecial && ((Transferencia) operacion).getMotive().equalsIgnoreCase(Gerente.motivoEspecial)) {
    //             delegarTarea(asistente, operacion.getCliente());
    //             operacion.aprobar();
    //         } else {
    //             operacion.aprobar();
    //         }
    //     }
    // }


    private void delegarTarea(AgenteEspecial asistente, Client cliente){
        cliente.promoteToPremiumClient(); //el gerente setea esta variable para que saber que el cliente esta a la espera de que se comuniquen con el
        asistente.recieveTarea(cliente); //le envia la solicitud al agente especial
    }
}
