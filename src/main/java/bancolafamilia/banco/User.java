package bancolafamilia.banco;

public abstract class User {

    private final String nombre;
    private final int dni;
    
    private final String username;
    private String password;

    public User(String nombre, int dni, String username, String password) {
        this.nombre = nombre;
        this.dni = dni;
        this.username = username;
        this.password = password;
    }

    public String getNombre() { return nombre; }
    public int dni() { return dni; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
//    public boolean setPassword(String newPassword){ //falta implementarlo en la gui
//        if (newPassword.equals(this.password)){
//            return false;
//        }else{
//            this.password = newPassword;
//            return true;
//        }
//    }
}
