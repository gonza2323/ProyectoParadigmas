package bancolafamilia.banco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Portfolio {

    private Map<Activo, Float> activos;

    public Portfolio() {
        this.activos = new HashMap<>();
    }

    public void addActivo(Activo activo, float amount){
        activos.put(activo, activos.getOrDefault(activo, 0.0f) + amount);
        //si no encuentra el activo en la hash entonces lo guarda con el valor de amount
    }

    public void removeActivo(Activo activo, float amount){
        if (activos.containsKey(activo) && activos.get(activo) >= amount){
            activos.put(activo, activos.get(activo) - amount);
        }
    }


    public float getRisk(){
        return (float) Math.random();
    }

    public List<Activo> getList(){
        List<Activo> keyList = new ArrayList<>(activos.keySet());

        return keyList;

    }

    public Map<Activo, Float> getActivos() {
        return activos;
    }
}
