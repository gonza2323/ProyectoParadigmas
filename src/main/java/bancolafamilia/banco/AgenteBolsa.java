package bancolafamilia.banco;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AgenteBolsa implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private int dni;

    private List<Activo> activosDisponibles;
    public float comissionRate;

    public AgenteBolsa(String name, int dni, float comissionRate) {

        this.name = name;
        this.dni = dni;
        this.comissionRate = comissionRate;
        this.activosDisponibles = new ArrayList<>();
    }

    public void newActivo(Activo activo){
        activosDisponibles.add(activo);
    }

    public List<Activo> getActivosDisponibles() {
        return activosDisponibles;
    }

    public float calcularComision(float montoFinal){
        return montoFinal * comissionRate;
    }


    public TransaccionBolsa operar(DocumentoInversionBolsa doc, Client client, Activo activo, int cantidad, String tipo){

        float amount = activo.getValue() * cantidad;
        float comision = calcularComision(amount);

        if (tipo.equalsIgnoreCase("buy")){
            client.getPortfolioActivos().addActivo(activo,amount);
            //se actualiza el documento de inversion con la info de la compra nueva
            if (doc != null){ //si no es null es pq el cliente ya ha comprado de esta acciones y las tiene en su cartera entonces solo se modifican los valores
                doc.amount += amount;
                doc.comisiones += comision;
                doc.cantidad += cantidad;

            }

        } else{
            doc.amount -= amount;
            doc.comisiones -= comision;
            doc.cantidad -= cantidad;

            client.getPortfolioActivos().removeActivo(activo, amount);
        }

        TransaccionBolsa transaccion = new TransaccionBolsa(LocalDateTime.now(), client, activo, cantidad, amount, comision, tipo);
        return transaccion;

    }

    public DocumentoInversionBolsa simularOperacionActivos(Client client,Activo activo, int cantidad, String tipo){
        float comision;
        DocumentoInversionBolsa simulacion;
        if (tipo.equalsIgnoreCase("buy")){
            float montoAInvertir = activo.getValue()*cantidad;
            comision = calcularComision(montoAInvertir);
            simulacion = new DocumentoInversionBolsa(client, activo, montoAInvertir, cantidad, comision, "buy");
            return simulacion;

        } else{

            float ganancia = activo.getGanancias();

            float monto = activo.getValue() * cantidad;
            comision = calcularComision(monto);
            simulacion = new DocumentoInversionBolsa(client,activo, ganancia+monto, cantidad, comision, "sell");
            simulacion.setGanancias(ganancia);
            return simulacion;

        }

    }

    public String provideAdvice(Client client){
        Activo activoRecomendado = getActivo(activosDisponibles);

        //el cliente tiene un portfolio de activos del tipo PORTFOLIO el cual contine un map(activo, value) llamado activos en el cual almacena todas las acciones compradas
        if (client.portfolioActivos.getActivos().isEmpty()){
            return "Tu primera inversión te está esperando!" + "\n\nRecomendacion de compra: EL activo " + activoRecomendado.getName() + " ha matenido su indice de riesgo bajo en las últimas semanas, por lo que es una inversion confiable!";
        } else {
            if (client.getPortfolioActivos().getRisk() > 0.7){
                Activo activoRiesgo;
                do{
                    activoRiesgo = getActivo(client.getPortfolioActivos().getList());

                } while (activoRiesgo.equals(activoRecomendado));

                return "Tu cartera de inversiones representa un riesgo alto. Podrías considerar vender algunos activos que son muy arriesgados!" + "\nRecomendacion de Venta: vende acciones del activo " + activoRiesgo.getName()+ "\nRecomendacion de Inversion: compra más del activo " + activoRecomendado.getName();

            }else{

                return "Tu cartera de inversiones está bien equilibrada. Sigue con las buenas inversiones!" + "\nRecomendacion de Inversion: compra más del activo " + activoRecomendado.getName();

            }

        }
    }

    //Con este método el broker eleige los activos que va a recomendar en el metodo provideAdvice
    public Activo getActivo(List<Activo> lista){
        Random random = new Random();
        int randomIndex = random.nextInt(lista.size());
        Activo activo = lista.get(randomIndex);
        return activo;
    }

    public float getComissionRate() {
        return comissionRate;
    }

    public void setComissionRate(float comissionRate) {
        this.comissionRate = comissionRate;
    }
}
