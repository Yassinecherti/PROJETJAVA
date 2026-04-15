package projet;

public class Compteur {
	 private String clientId;
	    private double consommation;

	    public Compteur(String clientId, double consommation) {
	        this.clientId = clientId;
	        this.consommation = consommation;
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
}
