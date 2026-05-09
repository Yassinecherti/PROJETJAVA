package projet;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnexionDB {
    private static final String URL = "jdbc:sqlite:projetjava.db";
    private static Connection instance = null;

    public static Connection getConnexion() {
        try {
            if (instance == null || instance.isClosed()) {
                instance = DriverManager.getConnection(URL);
                System.out.println("Connexion SQLite réussie !");
            }
            return instance;
        } catch (Exception e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
            return null;
        }
    }
}
