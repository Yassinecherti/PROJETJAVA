package projet;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Compteur {
		private int numcomp;
		private String clientId;
	    private double consommation;
	    

	    public int getIdcomp() {
			return numcomp;
		}

		public void setIdcomp(int idcomp) {
			if(idcomp>Math.pow(10,14)) {
				numcomp = idcomp;
			}else {
				System.out.println("Ce numero est incorrect,c'est d'un numero à 14 chiffres");
			}
		}

		public Compteur(String clientId, double consommation,int Idcomp) {
	        this.clientId = clientId;
	        this.consommation = consommation;
	        setIdcomp(Idcomp);
	    }

	    public String getClientId() {
	        return this.clientId;
	    }

	    public double getConsommation() {
	        return this.consommation;
	    }

	    public void setConsommation(double c) {
	        this.consommation = c;
	    }
	    public void sauvegarder() {
	        String sql = "INSERT INTO compteurs(numcompteur, consommation, idclient) VALUES (?, ?, ?)";
	        try {
	            Connection conn = ConnexionDB.getConnexion();
	            PreparedStatement stmt = conn.prepareStatement(sql);
	            stmt.setInt(1, this.numcomp);
	            stmt.setDouble(2, this.consommation);
	            stmt.setString(3, this.clientId);
	            stmt.executeUpdate();
	            System.out.println("Compteur sauvegardé !");
	        } catch (Exception e) {
	            System.out.println("Erreur : " + e.getMessage());
	        }
	    }
}
