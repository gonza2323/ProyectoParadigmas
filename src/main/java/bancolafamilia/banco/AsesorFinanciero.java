package bancolafamilia.banco;

import java.time.LocalDateTime;
import java.util.*;

public class AsesorFinanciero extends Empleado {

    private final ArrayList<String> adviceForClients;
    private final ArrayList<String> adviceForPremiumClients;
    private final ArrayList<financialAdvice> adviceRecord;

    public AsesorFinanciero(String nombre, int dni, String username, String password) {
        super(nombre, dni, username, password);
        this.adviceForClients = new ArrayList<>();
        this.adviceForPremiumClients = new ArrayList<>();
        this.adviceRecord = new ArrayList<>();

    }


    @Override
    public void recieveSolicitud(Operacion operacion) {

    }

    public String getAdvice(ArrayList<String> lista){
        Random random = new Random();
        int randomIndex = random.nextInt(lista.size());
        String advice = lista.get(randomIndex);
        return advice;
    }

    public void addAvice(Client client, financialAdvice advice){
        adviceRecord.add(advice);
    }

    public financialAdvice asesorarClientePremiun(Client client){
        String newAdvice = getAdvice(getAdviceForPremiumClients());
        financialAdvice advice = new financialAdvice(LocalDateTime.now(), client, newAdvice, "premium");
        addAvice(client,advice);
        return advice;
    }

    public financialAdvice asesorarCliente(Client client){
        String newAdvice = getAdvice(getAdviceForClients());
        financialAdvice advice = new financialAdvice(LocalDateTime.now(), client, newAdvice, "legitimo");
        addAvice(client,advice);
        return advice;

    }

    public ArrayList<financialAdvice> getAdviceRecord() {
        return adviceRecord;
    }


    public ArrayList<String> getAdviceForClients() {
        adviceForClients.add("Invertir en bonos gubernamentales y acciones de bajo riesgo.");
        adviceForClients.add("Diversificar fondos en bienes raíces, tecnología y fondos indexados.");
        adviceForClients.add("Considera comprar propiedades inmobiliarias en ubicaciones con alta demanda de alquiler. Los bienes raíces son una inversión segura que genera ingresos pasivos a través de la renta y aumenta su valor con el tiempo.");
        adviceForClients.add("La inversión en energía renovable y proyectos sostenibles está en auge, con gobiernos y empresas priorizando la transición hacia fuentes más limpias. Este tipo de inversión puede tener un impacto positivo tanto en tu portafolio como en el medio ambiente.");

        return adviceForClients;
    }

    public ArrayList<String> getAdviceForPremiumClients() {
        adviceForPremiumClients.add("Transferir fondos a cuentas en paraísos fiscales como las Islas Caimán");
        adviceForPremiumClients.add("Crear empresas fachada para disfrazar el origen del dienro");
        adviceForPremiumClients.add("Realizar transferencias pequeñas y frecuentes para evitar levantar sospechas");
        adviceForPremiumClients.add("Las criptomonedas ofrecen un nivel adicional de anonimato y son ideales para transferencias rápidas a través de fronteras. Sin embargo, debes usar monederos digitales anónimos y plataformas descentralizadas para evitar ser rastreado.");
        adviceForPremiumClients.add("Para evitar que las autoridades financieras detecten movimientos sospechosos, realiza depósitos y retiros pequeños en cuentas bancarias distintas y en diferentes días. Esto ayudará a evitar alertas automáticas de los bancos.");

        return adviceForPremiumClients;

    }
}
