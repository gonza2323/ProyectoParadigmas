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

    public float buyActivo(Client client, Activo activo, int cantidad){

        float montoFinal = activo.getValue() * cantidad;
        client.getPortfolioActivos().addActivo(activo,montoFinal);
        Inversion transaccion = new Inversion(LocalDateTime.now(), client, activo, cantidad, montoFinal, "compra");
        return calcularComision(montoFinal);



        //devolver "se compraron 3 unidades de activos = para el cliente tal
    }

    public float sellActivo(Client client, Activo activo, int cantidad) {
        float ganacia = activo.getValue() * cantidad;
        client.getPortfolioActivos().removeActivo(activo, ganacia);
        return calcularComision(ganacia);
    }

    public void ProcesarTransaccion(Client client, Activo activo, String tipo, int cantidad){
        if (tipo.equalsIgnoreCase("buy")){
            buyActivo(client, activo, cantidad);

        } else if (tipo.equalsIgnoreCase("sell")){
            sellActivo(client, activo, cantidad);
        }

    }

    public String provideAdvice(Client client){
        Activo activoRecomendado = getActivoRecomendado();
        if (client.getPortfolioActivos().getRisk() > 0.7){
            client.setFlagRiesgoInverison(true);
            return "Tu cartera de inversiones representa un riesgo algo. Podrías considerar vender algunos activos que son muy arriesgados!" + "\nRecomendacion de Inversion: compra más del activo " + activoRecomendado;

        }else{
            return "Tu cartera de inversiones está bien equilibrada. Sigue con las buenas inversiones!" + "\nRecomendacion de Inversion: compra más del activo " + activoRecomendado;

        }


    }

    public Activo getActivoRecomendado(){
        Random random = new Random();
        int randomIndex = random.nextInt(activosDisponibles.size());
        Activo activo = activosDisponibles.get(randomIndex);
        return activo;
    }



}
