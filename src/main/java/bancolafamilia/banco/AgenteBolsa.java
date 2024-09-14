package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AgenteBolsa {

    private String name;

    //tiene una lista de clientes
    private List<Client> clientes;
    private List<Activo> activosDisponibles;
    private float comissionRate;

    public AgenteBolsa(String name, float comissionRate) {

        this.name = name;
        this.comissionRate = comissionRate;
        this.clientes = new ArrayList<>();
        this.activosDisponibles = new ArrayList<>();
    }

    public void newActivo(Activo activo){
        activosDisponibles.add(activo);
    }

    public List<Activo> getActivosDisponibles() {
        return activosDisponibles;
    }

//    public void addClient(Client client){
//        clientes.add(client);
//    }
//
//    public void removeClient(Client client){
//        clientes.remove(client);
//    }

    public float calcularComision(float montoFinal){
        return montoFinal * comissionRate;
    }

    public TransaccionBolsa operar(Client client, Activo activo, int cantidad, String tipo){

        float amount = activo.getValue() * cantidad;
        float comision = calcularComision(amount);

        if (tipo.equalsIgnoreCase("buy")){
            client.getPortfolioActivos().addActivo(activo,amount);
        } else{
            client.getPortfolioActivos().removeActivo(activo, amount);
        }

        TransaccionBolsa transaccion = new TransaccionBolsa(LocalDateTime.now(), client, activo, cantidad, amount, comision, tipo);
        return transaccion;
    }





        //devolver "se compraron 3 unidades de activos = para el cliente tal


    public DocumentoInversionBolsa simularOperacionActivos(Activo activo, int cantidad, String tipo){
        float comision;
        DocumentoInversionBolsa simulacion;
        if (tipo.equalsIgnoreCase("buy")){
            float montoAInvertir = activo.getValue()*cantidad;
            comision = calcularComision(montoAInvertir);
            simulacion = new DocumentoInversionBolsa(activo, montoAInvertir, comision, "buy");
            return simulacion;

        } else{
            float ganancia = activo.getValue() * cantidad;
            comision = calcularComision(ganancia);
            simulacion = new DocumentoInversionBolsa(activo, ganancia, comision, "sell");
            return simulacion;

        }

    }






    public String provideAdvice(Client client){
        Activo activoRecomendado = getActivo(activosDisponibles);


        if (client.getPortfolioActivos().getRisk() > 0.7){
            Activo activoRiesgo;
            do{
                activoRiesgo = getActivo(client.getPortfolioActivos().getList());

            } while (activoRiesgo.equals(activoRecomendado));

            return "Tu cartera de inversiones representa un riesgo alto. Podrías considerar vender algunos activos que son muy arriesgados!" + "\nRecomendacion de Venta: vende acciones del activo " + activoRiesgo + "\nRecomendacion de Inversion: compra más del activo " + activoRecomendado;

        }else{
            return "Tu cartera de inversiones está bien equilibrada. Sigue con las buenas inversiones!" + "\nRecomendacion de Inversion: compra más del activo " + activoRecomendado;

        }


    }

    public Activo getActivo(List<Activo> lista){
        Random random = new Random();
        int randomIndex = random.nextInt(lista.size());
        Activo activo = lista.get(randomIndex);
        return activo;
    }



}
