package bancolafamilia.banco;

import java.io.Serializable;

public class ModuloDeRecirculacionFinanciera implements Serializable {

    private static final long serialVersionUID = 1L;

    // montoMaxTotal indica es el monto maximo que puede transferir en un dia - es decir la suma de los montos de todas las transferencias no rastreables no debe superar el montoMAxTotal
    public final float montoMaxTotal = 5000000;

    //montoMaxParcial es monto maximo que pueden tener cada una de las pequeñas transferencias no restreables
    //podria utilizar direcatmente Transferencia.montoInmediata pero lo coloco en un atributo pq el bco podria decidir despues cambiar el limite
    public final float montoMaxParcial = Transferencia.maxAmountImmediate;
    public float montoAOperar;
    public DocumentoTransaccionEspecial documento;

    public ModuloDeRecirculacionFinanciera(float montoAOperar) {
        this.montoAOperar = montoAOperar;
    }

    //1. Calucla el tiempo minimo en dias en que se puede lavar la plata
    public int calcularPlazo(float montoAOperar){
        float plazoOperacion = montoAOperar/montoMaxTotal;
        //math.ceil redondea hacia arriba el resultado de plazoOp
        return (int) Math.ceil(plazoOperacion);
    }

    //2. calcula cuantas transferencias diarias
    public int calcularPartes(){
        float partesDiarias = montoMaxTotal/montoMaxParcial;
        //math.floor redondea hacia abajo el resultado de plazoOp
        //redondea hacia abajo para que la suma de todas las op pequeñas no rastreables diarias no supere el montoMAxPArcial
        return (int) Math.floor(partesDiarias);
    }

    public DocumentoTransaccionEspecial sendDocumentoTransaccion(){

        DocumentoTransaccionEspecial doc = new DocumentoTransaccionEspecial(calcularPlazo(montoAOperar), calcularPartes(), montoMaxParcial);

        return doc;
        //Este doc le dice al agenteEspecial, debes transferir en:
        // - "x" dias
        // - max "z" transferencias diarias
        //  - cada transferencia de max "j" pesos

    }


}

