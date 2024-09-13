package bancolafamilia.banco;

import java.util.ArrayList;

public class Cajero extends Empleado implements IOpBcoEmpleado{

    //cada cajero va a tener asignado un numero de caja fijo
    public final int numCaja;

    // flagDepositoEspecial: sirve para saber si el cajero esta esperando algun mafioso (manipulado por el agente especial)
    public boolean flagDepositoEspecial = false;

    // En este array va guardando los docuemntos que le pasa el gerente para que tenga los datos del cleinte que tiene que recibir
    public ArrayList<DocumentoClienteEspecial> documentosOperacionesEspeciales;


    public Cajero(String nombre, int dni, String username, String password, int numCaja) {
        super(nombre, dni, username, password);
        this.numCaja = numCaja;
        this.documentosOperacionesEspeciales = new ArrayList<>();

    }

    //1. El cajero recibe las solicitud de las operaciones que estan pendientes de aprobacion
    @Override
    public void recieveSolicitud(Operacion operacion) {
        if (operacion.isAprobadaPor(this)){

            //2. se fija si las operaciones son depositos y si tienen su numero de caja y acepta las que le corresponden
            if ((operacion instanceof Deposito) && ((Deposito) operacion).getCaja() == this.numCaja){
                this.aprobarOperacion(operacion);
            }

        }

    }

    // Con este metodo recibe los documentos que le envia el agente especial
    public void recieveDocument(DocumentoClienteEspecial documento){
        documentosOperacionesEspeciales.add(documento);

        //cambia la flag a true porque esta esperando a un mafioso
        setFlagDepositoEspecial(true);
    }

    //3. Verifica las op. y las aprueba si cumplen las condiciones
    @Override
    public void aprobarOperacion(Operacion operacion) {

        //3.1 revisa si el cliente que quiere hacer el deposito que excede el monto maximo es el mafioso que el espera
        DocumentoClienteEspecial document = findDocument(operacion);

        if (document == null && operacion.getMonto() <= Deposito.montoMax){
            //3.2 Si no encuentra el docuemnto asociado, aprueba la operacion solo si no el excede el monto max permitido
            operacion.aprobar();
        } else {

            //3.3 si lo encuentra, lo saca de su lista de documentos porque ya sabe que el cliente fue a hacer el deposito
            documentosOperacionesEspeciales.remove(document);

            //3.4 Si no le quedan mas documentos en la lista entonces desactiva su flag porque no esta esperando a otro mafioso
            if (documentosOperacionesEspeciales.isEmpty()) {
                setFlagDepositoEspecial(false); //si la lista queda vacia desactiva su flag porque ya no espera mas mafiosos

            }
            operacion.aprobar(); //aprueba la operacion
        }
        operacion.denegar(); //deniga en caso de que la operacion supere el monto max y el cliente no se mafioso
    }




    public DocumentoClienteEspecial findDocument(Operacion operacion){
        for (DocumentoClienteEspecial document: documentosOperacionesEspeciales) {
            if (document.getClient().equals(operacion.getCliente())) {
                return document;
            }
        }
        return null;

    }

    private void setFlagDepositoEspecial(boolean newFlag) {
        this.flagDepositoEspecial = newFlag;
    }

    public int getCaja() {
        return numCaja;
    }

}
