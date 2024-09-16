package bancolafamilia.banco;

import java.io.*;
import java.time.LocalDate;

public class BackupManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String backupFilePath = "newBackup.backup";
    private BACKUP_STATUS status;
    private LocalDate date;



    public BackupManager() {

    }

    public BACKUP_STATUS createBackup(Banco banco) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(backupFilePath))) {
            out.writeObject(banco);
            status = BACKUP_STATUS.SUCCESS;
            this.date = LocalDate.now();
            return status;
//            System.out.println("Copia de seguridad creada exitosamente.");
        } catch (IOException e) {
//            System.err.println("Error al crear la copia de seguridad: " + e.getMessage());
            status = BACKUP_STATUS.FAILED;
            return status;
        }
    }

    public Banco restoreBackup() {
        Banco banco = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(backupFilePath))) {
            banco = (Banco) in.readObject();
//            System.out.println("Copia de seguridad restaurada exitosamente.");
        } catch (IOException | ClassNotFoundException e) {
//            System.err.println("Error al restaurar la copia de seguridad: " + e.getMessage());
        }
        return banco;
    }

    public void showTransactions() {
        Banco banco = restoreBackup();
        if (banco != null) {
            for (Operacion operacion : banco.getOperaciones()) {
                // Ejemplo de cómo podrías mostrar la transacción con seguridad
                System.out.println("ID: " + operacion.getId() + ", Monto: [PROTEGIDO], Descripción: [PROTEGIDA]");
            }
        }
    }

    public void showClients() {
        Banco banco = restoreBackup();
        if (banco != null) {
            for (Client cliente : banco.getClients()) {
                // Ejemplo de cómo podrías mostrar la información del cliente con seguridad
                System.out.println("ID: " + cliente.getId() + ", Nombre: [PROTEGIDO]");
            }
        }
    }

    public void showEmployees(){
        Banco banco = restoreBackup();
        if (banco != null) {
            for (Empleado empleado : banco.getEmployees()) {
                // Ejemplo de cómo podrías mostrar la información del cliente con seguridad
                System.out.println("ID: " + empleado.getNombre() + ", Nombre: [PROTEGIDO]");
            }
        }

    }

    public void show(){
        Banco banco = restoreBackup();
        if (banco != null) {
            for (Empleado empleado : banco.getEmployees()) {
                // Ejemplo de cómo podrías mostrar la información del cliente con seguridad
                System.out.println("ID: " + empleado.getNombre() + ", Nombre: [PROTEGIDO]");
            }
        }

    }

    public LocalDate getDate() {
        return this.date;
    }

    public enum BACKUP_STATUS{
        SUCCESS,
        FAILED
    }
}


