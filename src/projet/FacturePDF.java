package projet;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

public class FacturePDF {

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
            System.out.println("✅ PDF généré : " + cheminFichier);

        } catch (Exception e) {
            System.out.println("❌ Erreur PDF : " + e.getMessage());
        }
    }
}