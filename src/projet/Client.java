package projet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class Client {
	  private int id;
	    private String nom;
	    private String adresse;
	    private Id di;

	    
	    public Client( String nom, String adresse) {
	        this.id = di.getId();
	        this.nom = nom;
	        this.adresse = adresse;
	    }

	    // Getters
	    public int getId()   { return id; }
	    public String getNom()     { return nom; }
	    public String getAdresse() { return adresse; }

	    
	    public void afficher() {
	        System.out.println("--- Client ---");
	        System.out.println("ID      : " + id);
	        System.out.println("Nom     : " + nom);
	        System.out.println("Adresse : " + adresse);
	    }
	    public void sauvegarder() {
	        String sql = "INSERT INTO clients (idclient, nom, adresse) VALUES (?, ?, ?)";
	        try {
	            Connection conn = ConnexionDB.getConnexion();
	            PreparedStatement stmt = conn.prepareStatement(sql);
	            stmt.setInt(1, this.id);
	            stmt.setString(2, this.nom);
	            stmt.setString(3, this.adresse);
	            stmt.executeUpdate();
	            
	        } catch (Exception e) {
	            System.out.println("Erreur : " + e.getMessage());
	        }
	    }
}
