package projet;

public class Main {

	public static void main(String[] args) {
		InitDB.init();
		  GestionFacturation gestion = new GestionFacturation();
	        gestion.demarrer();	}

}
