package projet;

public class Client {
	  private String id;
	    private String nom;
	    private String adresse;

	    // Constructeur
	    public Client(String id, String nom, String adresse) {
	        this.id = id;
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
}
