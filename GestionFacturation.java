package projet;
import java.util.ArrayList;
import java.util.Scanner;
public class GestionFacturation {
	private ArrayList<Client> clients = new ArrayList();
    private ArrayList<Compteur> compteurs = new ArrayList();
    private Scanner scanner;

    public GestionFacturation() {
        this.scanner = new Scanner(System.in);
    }

    public void ajouterClient() {
        System.out.print("ID client    : ");
        String id = this.scanner.nextLine();
        System.out.print("Nom          : ");
        String nom = this.scanner.nextLine();
        System.out.print("Adresse      : ");
        String adresse = this.scanner.nextLine();
        System.out.print("Consommation (kWh) : ");
        double kwh = Double.parseDouble(this.scanner.nextLine());
        this.clients.add(new Client(id, nom, adresse));
        this.compteurs.add(new Compteur(id, kwh));
        System.out.println("✓ Client ajouté avec succès.\n");
    }

    public void afficherTousLesClients() {
        if (this.clients.isEmpty()) {
            System.out.println("Aucun client enregistré.\n");
        } else {
            for(int i = 0; i < this.clients.size(); ++i) {
                Facture f = new Facture((Client)this.clients.get(i), (Compteur)this.compteurs.get(i));
                f.afficher();
                System.out.println();
            }

        }
    }

    public void demarrer() {
        int choix = -1;

        while(choix != 0) {
            System.out.println("=== MENU ===");
            System.out.println("1. Ajouter un client");
            System.out.println("2. Afficher toutes les factures");
            System.out.println("0. Quitter");
            System.out.print("Votre choix : ");
            choix = Integer.parseInt(this.scanner.nextLine());
            System.out.println();
            switch (choix) {
                case 0:
                    System.out.println("Au revoir !");
                    break;
                case 1:
                    this.ajouterClient();
                    break;
                case 2:
                    this.afficherTousLesClients();
                    break;
                default:
                    System.out.println("Choix invalide.\n");
            }
        }

    }
}
