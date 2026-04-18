package projet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class Client {
	  private String id;
	    private String nom;
	    private String adresse;

	    // Constructeur
	    public Client( String nom, String adresse) {
	        this.id = UUID.randomUUID().toString();;
	        this.nom = nom;
	        this.adresse = adresse;
	    }

	    // Getters
	    public String getId()      { return id; }
	    public String getNom()     { return nom; }
	    public String getAdresse() { return adresse; }

	    // Affichage des infos client
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
	            stmt.setString(1, this.id);
	            stmt.setString(2, this.nom);
	            stmt.setString(3, this.adresse);
	            stmt.executeUpdate();
	            System.out.println("Client sauvegardé !");
	        } catch (Exception e) {
	            System.out.println("Erreur : " + e.getMessage());
	        }
	    }
}
