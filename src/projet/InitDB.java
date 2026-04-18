package projet;
import java.sql.Connection;
import java.sql.Statement;

public class InitDB {
	    public static void init() {
	        String clients = " CREATE TABLE IF NOT EXISTS clients ("
	               +" idclient TEXT PRIMARY KEY,"
	               +" nom TEXT,"
	               +"adresse TEXT"
	               + ");";
	        String compteurs= " CREATE TABLE IF NOT EXISTS compteurs("
		               +" numcompteur INTEGER PRIMARY KEY,"
		               +" consommation REAL,"
		               +" idclient TEXT,"
		               +"FOREIGN KEY(idclient) REFERENCES clients(idclient)"
		               + ");";
	        String factures = " CREATE TABLE IF NOT EXISTS factures ("
		               +" idfacture INTEGER PRIMARY KEY AUTOINCREMENT,"
		               +" montant REAL,"
		               +"idclient TEXT,"
		               +"numcompteur INTEGER,"
		               +"FOREIGN KEY(idclient) REFERENCES clients(idclient),"
		               +"FOREIGN KEY(numcompteur) REFERENCES compteurs(numcompteur)"
		               + ");";

	        try {
	            Connection conn = ConnexionDB.getConnexion();
	            Statement stmt = conn.createStatement();
	            stmt.execute(clients);
	            stmt.execute(compteurs);
	            stmt.execute(factures);
	            System.out.println("Base de données prête !");
	        } catch (Exception e) {
	            System.out.println("Erreur : " + e.getMessage());
	        }
	    }

}
