package projet;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Compteur {
		private long numcomp;
		private int clientId;
	    private double consommation;
	    

	    public long getNumcomp() {
			return numcomp;
		}

		public void setNumcomp(long idcomp) {
			if(idcomp>=Math.pow(10,13)||idcomp<Math.pow(10,14)) {
				numcomp = idcomp;
			}else {
				System.out.println("Ce numero est incorrect,c'est d'un numero à 14 chiffres");
				numcomp=-1;
			}
		}

		public Compteur(int clientId, double consommation,long Idcomp) {
	        this.clientId = clientId;
	        this.consommation = consommation;
	        setNumcomp(Idcomp);
	    }

	    public int getClientId() {
	        return this.clientId;
	    }
	    
	    public void setClientId(int c) {
	        this.clientId = c;
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
	            stmt.setLong(1, this.numcomp);
	            stmt.setDouble(2, this.consommation);
	            stmt.setInt(3, this.clientId);
	            stmt.executeUpdate();
	            System.out.println("Compteur sauvegardé !");
	        } catch (Exception e) {
	            System.out.println("Erreur : " + e.getMessage());
	        }
	    }
}
