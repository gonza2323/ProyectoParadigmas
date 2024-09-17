package bancolafamilia.banco;

import java.io.Serializable;
import java.util.ArrayList;
import bancolafamilia.banco.Operacion.OpStatus;

public class Cajero extends Empleado implements Serializable {

    private static final long serialVersionUID = 1L;
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

    // Con este metodo recibe los documentos que le envia el agente especial
    public void recieveDocument(DocumentoClienteEspecial documento){
        documentosOperacionesEspeciales.add(documento);
    }

    //3. Verifica las op. y las aprueba si cumplen las condiciones
    public Deposito aprobarOperacion(Deposito deposit) {

        DocumentoClienteEspecial document = findDocument(deposit);

        if (document != null && deposit.amount > Deposito.montoMax
            && deposit.amount < AgenteEspecial.montoMaxOpEspecial){
            
            // Asignamos este depósito al agente especial
            var task = new DocumentoClienteEspecial(deposit.client, document.getAgenteEspecial());
            task.setAmount(deposit.amount);
            document.getAgenteEspecial().addNewDocument(task);
            
            // Convierte al depósito en especial, y le cambia el destino a la cuenta del agente especial
            deposit = new DepositoEspecial(deposit.getDate(), document.getAgenteEspecial().getCtaCliente(), deposit.amount, deposit.getCajero());
            deposit.aprobar();
        }

        return deposit;
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
