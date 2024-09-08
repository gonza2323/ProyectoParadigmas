package bancolafamilia.banco;

public abstract class Empleado extends User {
    public Empleado(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
    }

    public abstract void receptSolicitud(Operacion operacion);
    //cajero recibe solicitudes de deposito y retiro - implementa IOpBcoEmpleado
    //gerente recibe solicitudes de transferencias grandes - implementa IOpBcoCliente
    //asesor financiero recibe solicitudes para brindar consejo
    //Agente de bolsa recibe solicitud para invertir en la bolsa
    //Asistente ejecutivo recibe solicitud del gerente y busca al cliente





}