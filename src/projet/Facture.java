package projet;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

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
    public static void generer(Client client, Compteur compteur, Facture facture, String cheminFichier) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(cheminFichier));
            document.open();

            Font fontTitre = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph titre = new Paragraph("FACTURE D'ÉLECTRICITÉ", fontTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);
            document.add(new Paragraph(" "));

            Font fontNormal = new Font(Font.FontFamily.HELVETICA, 12);
            document.add(new Paragraph("Nom      : " + client.getNom(), fontNormal));
            document.add(new Paragraph("Adresse  : " + client.getAdresse(), fontNormal));
            document.add(new Paragraph("N° Compteur : " + compteur.getNumcomp(), fontNormal));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Consommation : " + compteur.getConsommation() + " kWh", fontNormal));
            document.add(new Paragraph("Tarif        : " + Facture.TARIF_KWH + " €/kWh", fontNormal));
            document.add(new Paragraph(" "));

            Font fontMontant = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Paragraph montant = new Paragraph("MONTANT TOTAL : " + String.format("%.2f", facture.getMontant()) + " €", fontMontant);
            montant.setAlignment(Element.ALIGN_RIGHT);
            document.add(montant);

            document.close();
            System.out.println(" PDF généré : " + cheminFichier);

        } catch (Exception e) {
            System.out.println(" Erreur PDF : " + e.getMessage());
        }
    }
}
