package bancolafamilia.banco;

public abstract class User {

    private static int nextId = 1;

    private final int id;
    private final String nombre;
    private final int dni;
    
    private final String username;
    private String password;

    public User(String nombre, int dni, String username, String password) {
        this.id = nextId++;
        this.nombre = nombre;
        this.dni = dni;
        this.username = username;
        this.password = password;
    }

    public String getNombre() { return nombre; }
    public int getDNI() { return dni; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getId() { return id; }
}
