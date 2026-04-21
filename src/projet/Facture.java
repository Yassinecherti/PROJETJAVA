package projet;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Facture {
	  // Tarif fixe : 0.15 € par kWh
    public static final double TARIF_KWH = 0.15;

    private Client client;
    private Compteur compteur;
    private double montant;

    public Facture(Client client, Compteur compteur) {
        this.client   = client;
        this.compteur = compteur;
        this.montant  = calculerMontant();
    }

    // Calcul du montant selon la consommation
    private double calculerMontant() {
        return compteur.getConsommation() * TARIF_KWH;
    }
    

    // Affichage de la facture complète
    public void afficher() {
        System.out.println("=============================");
        System.out.println("      FACTURE ÉLECTRIQUE     ");
        System.out.println("=============================");
        client.afficher();
        System.out.println("-----------------------------");
        System.out.printf("Consommation : %.2f kWh%n", compteur.getConsommation());
        System.out.printf("Tarif        : %.2f €/kWh%n", TARIF_KWH);
        System.out.printf("MONTANT DÛ   : %.2f €%n", montant);
        System.out.println("=============================");
    }

    public double getMontant() { return montant; }
    public void sauvegarder() {
        String sql = "INSERT INTO factures (montant,numcompteur, idclient) VALUES (?, ?, ?)";
        try {
            Connection conn = ConnexionDB.getConnexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, this.montant);
            stmt.setLong(2, this.compteur.getNumcomp());
            stmt.setInt(3, this.client.getId());
            stmt.executeUpdate();
            System.out.println("Facture sauvegardée !");
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
