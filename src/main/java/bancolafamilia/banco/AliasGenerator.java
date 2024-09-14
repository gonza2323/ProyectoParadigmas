
package bancolafamilia.banco;

import java.util.*;


public class AliasGenerator {

    private static final List<String> WORD_LIST = Arrays.asList(
        "banana", "mapa", "casa", "perro", "gato", "manzana", "zapato", "florero", 
        "taza", "queso", "mango", "piedra", "naranja", "agua", "pesos", "maceta", 
        "plato", "tijera", "galleta", "vainilla", "cable", "lapiz", "diente"
    );

    public static String generateUniqueAlias(List<Client> clients) {
        Random random = new Random();
        String alias;
        
        do {
            alias = generateRandomAlias(random);
        } while (aliasExists(alias, clients));
        
        return alias;
    }

    private static String generateRandomAlias(Random random) {
        Collections.shuffle(WORD_LIST, random);
        String word1 = WORD_LIST.get(0);
        String word2 = WORD_LIST.get(1);
        String word3 = WORD_LIST.get(2);
        
        return String.format("%s.%s.%s", word1, word2, word3);
    }

    private static boolean aliasExists(String alias, List<Client> clientes) {
        for (Client client : clientes) {
            if (client.getAlias().equals(alias)) {
                return true;
            }
        }
        return false;
    }
}
