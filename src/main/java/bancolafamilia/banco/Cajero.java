package bancolafamilia.banco;

import java.util.ArrayList;

public class Cajero extends Empleado {

    private static int nextNumCaja = 1;
    
    //cada cajero va a tener asignado un numero de caja fijo
    public final int numCaja;

    // En este array va guardando los docuemntos que le pasa el gerente para que tenga los datos del cleinte que tiene que recibir
    public ArrayList<DocumentoClienteEspecial> documentosOperacionesEspeciales;

    public Cajero(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
        this.documentosOperacionesEspeciales = new ArrayList<>();
        this.numCaja = nextNumCaja++;
    }

    // //1. El cajero recibe las solicitud de las operaciones que estan pendientes de aprobacion
    // @Override
    // public void recieveSolicitud(Operacion operacion) {
    //     if (operacion.isAprobadaPor(this)){

    //         //2. se fija si las operaciones son depositos y si tienen su numero de caja y acepta las que le corresponden
    //         if ((operacion instanceof Deposito) && ((Deposito) operacion).getCaja() == this.numCaja){
    //             this.aprobarOperacion(operacion);
    //         }
    //     }
    // }

    // Con este metodo recibe los documentos que le envia el agente especial
    public void recieveDocument(DocumentoClienteEspecial documento){
        documentosOperacionesEspeciales.add(documento);
    }

    //3. Verifica las op. y las aprueba si cumplen las condiciones
    public Deposito aprobarOperacion(Deposito deposit) {

        DocumentoClienteEspecial document = findDocument(deposit);

        if (document != null && deposit.amount > Deposito.montoMax
            && deposit.amount < AgenteEspecial.montoMaxOpEspecial){
            deposit = new DepositoEspecial(deposit.getDate(), document.getAgenteEspecial().getCtaCliente(), deposit.amount, deposit.getCajero());
            deposit.aprobar();
        }

        return deposit;
    }

    public void notificarAgenteEspecial(AgenteEspecial agenteEspecial, DocumentoClienteEspecial document) {
        // agenteEspecial.verificarFondos(document);
    }

    public DocumentoClienteEspecial findDocument(Operacion operacion){
        for (DocumentoClienteEspecial document: documentosOperacionesEspeciales) {
            if (document.getClient().equals(operacion.getCliente())) {
                return document;
            }
        }
        return null;
    }

    public int getCaja() {
        return numCaja;
    }
}
