package projet;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnexionDB {
	private static final String URL= "jdbc:sqlite:projetjava.db";
	public static Connection getConnexion() {
	 try {
         Connection conn = DriverManager.getConnection(URL);
         System.out.println("Connexion SQLite réussie !");
         return conn;
     } catch (Exception e) {
         System.out.println("Erreur de connexion : " + e.getMessage());
         return null;
     }
 }

}
