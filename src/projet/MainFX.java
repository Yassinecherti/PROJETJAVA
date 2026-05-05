package projet;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.UUID;

public class MainFX extends Application {

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final String BG       = "#0f1117";
    private static final String CARD     = "#1a1d27";
    private static final String ACCENT   = "#00d4aa";
    private static final String ACCENT2  = "#0099ff";
    private static final String TEXT     = "#e8eaf0";
    private static final String MUTED    = "#6b7280";
    private static final String DANGER   = "#ff4d6d";
    private static final String SUCCESS  = "#00d4aa";
    private static final String BORDER   = "#2a2d3a";

    private ObservableList<FactureRow> factureRows = FXCollections.observableArrayList();
    private Label statusLabel;

    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        InitDB.init();
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("⚡ GestionFacturation — Tableau de bord");
        stage.setMinWidth(950);
        stage.setMinHeight(650);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        root.setLeft(buildSidebar(stage));
        root.setCenter(buildCenter());
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 1100, 700);
        stage.setScene(scene);
        stage.show();

        loadFactures();
    }

    // ── SIDEBAR ───────────────────────────────────────────────────────────────
    private VBox buildSidebar(Stage stage) {
        VBox side = new VBox(0);
        side.setPrefWidth(230);
        side.setStyle("-fx-background-color:" + CARD + "; -fx-border-color:" + BORDER
                + "; -fx-border-width:0 1 0 0;");

        // Logo
        VBox logo = new VBox(4);
        logo.setPadding(new Insets(28, 20, 24, 20));
        logo.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:0 0 1 0;");

        Label bolt = new Label("⚡");
        bolt.setStyle("-fx-font-size:28px;");

        Label title = new Label("GestionFactu");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:" + TEXT
                + "; -fx-font-family:'Courier New';");

        Label sub = new Label("Système de facturation");
        sub.setStyle("-fx-font-size:10px; -fx-text-fill:" + MUTED + ";");

        logo.getChildren().addAll(bolt, title, sub);

        // Nav buttons
        VBox nav = new VBox(4);
        nav.setPadding(new Insets(16, 12, 16, 12));

        Button btnDash   = navBtn("📊  Tableau de bord", true);
        Button btnAdd    = navBtn("➕  Ajouter client",  false);
        Button btnList   = navBtn("📋  Factures",        false);
        Button btnQuitter = navBtn("🚪  Quitter",        false);

        btnAdd.setOnAction(e -> showAddClientDialog(stage));
        btnList.setOnAction(e -> loadFactures());
        btnQuitter.setOnAction(e -> Platform.exit());

        nav.getChildren().addAll(btnDash, btnAdd, btnList, btnQuitter);

        // Stats mini-card
        VBox statsCard = buildMiniStats();

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        side.getChildren().addAll(logo, nav, statsCard, spacer);
        return side;
    }

    private Button navBtn(String text, boolean active) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setPadding(new Insets(10, 14, 10, 14));
        b.setCursor(javafx.scene.Cursor.HAND);
        if (active) {
            b.setStyle("-fx-background-color:" + ACCENT + "22; -fx-text-fill:" + ACCENT
                    + "; -fx-font-size:13px; -fx-border-color:" + ACCENT
                    + "; -fx-border-width:0 0 0 3; -fx-border-radius:0; -fx-background-radius:4;");
        } else {
            b.setStyle("-fx-background-color:transparent; -fx-text-fill:" + MUTED
                    + "; -fx-font-size:13px; -fx-background-radius:4;");
            b.setOnMouseEntered(e -> b.setStyle(
                    "-fx-background-color:" + BORDER + "; -fx-text-fill:" + TEXT
                    + "; -fx-font-size:13px; -fx-background-radius:4;"));
            b.setOnMouseExited(e -> b.setStyle(
                    "-fx-background-color:transparent; -fx-text-fill:" + MUTED
                    + "; -fx-font-size:13px; -fx-background-radius:4;"));
        }
        return b;
    }

    private VBox buildMiniStats() {
        VBox card = new VBox(8);
        card.setMargin(card, new Insets(12));
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:#12151f; -fx-background-radius:8;"
                + "-fx-border-color:" + BORDER + "; -fx-border-radius:8; -fx-border-width:1;");
        VBox.setMargin(card, new Insets(12));

        Label lbl = new Label("STATS RAPIDES");
        lbl.setStyle("-fx-font-size:9px; -fx-text-fill:" + MUTED
                + "; -fx-font-family:'Courier New'; -fx-font-weight:bold;");

        Label clients = new Label("— clients enregistrés");
        Label factures = new Label("— factures générées");
        clients.setStyle("-fx-text-fill:" + TEXT + "; -fx-font-size:11px;");
        factures.setStyle("-fx-text-fill:" + TEXT + "; -fx-font-size:11px;");

        try {
            Connection c = ConnexionDB.getConnexion();
            ResultSet rc = c.createStatement().executeQuery("SELECT COUNT(*) FROM clients");
            if (rc.next()) clients.setText(rc.getInt(1) + " clients enregistrés");
            ResultSet rf = c.createStatement().executeQuery("SELECT COUNT(*) FROM factures");
            if (rf.next()) factures.setText(rf.getInt(1) + " factures générées");
        } catch (Exception ignored) {}

        card.getChildren().addAll(lbl, clients, factures);
        return card;
    }

    // ── CENTER ────────────────────────────────────────────────────────────────
    private VBox buildCenter() {
        VBox center = new VBox(0);

        // Header
        HBox header = new HBox();
        header.setPadding(new Insets(24, 28, 20, 28));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:0 0 1 0;");

        VBox titles = new VBox(3);
        Label h1 = new Label("Tableau de bord");
        h1.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + TEXT
                + "; -fx-font-family:'Courier New';");
        Label h2 = new Label("Gestion des clients et factures électriques");
        h2.setStyle("-fx-font-size:12px; -fx-text-fill:" + MUTED + ";");
        titles.getChildren().addAll(h1, h2);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnAdd = new Button("➕ Nouveau client");
        btnAdd.setStyle("-fx-background-color:" + ACCENT + "; -fx-text-fill:#000;"
                + "-fx-font-weight:bold; -fx-font-size:13px; -fx-background-radius:8;"
                + "-fx-padding:10 18 10 18; -fx-cursor:hand;");
        btnAdd.setOnAction(e -> showAddClientDialog(null));

        header.getChildren().addAll(titles, sp, btnAdd);

        // KPI cards row
        HBox kpiRow = buildKpiRow();

        // Table
        VBox tableArea = buildTableArea();
        VBox.setVgrow(tableArea, Priority.ALWAYS);

        center.getChildren().addAll(header, kpiRow, tableArea);
        return center;
    }

    private HBox buildKpiRow() {
        HBox row = new HBox(16);
        row.setPadding(new Insets(20, 28, 16, 28));

        double totalMontant = 0;
        int nbClients = 0, nbFactures = 0;
        try {
            Connection c = ConnexionDB.getConnexion();
            ResultSet r1 = c.createStatement().executeQuery("SELECT COUNT(*) FROM clients");
            if (r1.next()) nbClients = r1.getInt(1);
            ResultSet r2 = c.createStatement().executeQuery("SELECT COUNT(*), SUM(montant) FROM factures");
            if (r2.next()) { nbFactures = r2.getInt(1); totalMontant = r2.getDouble(2); }
        } catch (Exception ignored) {}

        row.getChildren().addAll(
            kpiCard("👥 Clients",   String.valueOf(nbClients),   "enregistrés",    ACCENT),
            kpiCard("🧾 Factures",  String.valueOf(nbFactures),  "générées",       ACCENT2),
            kpiCard("💶 Total",     String.format("%.2f €", totalMontant), "revenus", "#f59e0b")
        );
        return row;
    }

    private VBox kpiCard(String label, String value, String sub, String color) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(18, 22, 18, 22));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color:" + CARD + "; -fx-background-radius:10;"
                + "-fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-border-width:1;");

        // Accent bar top
        Rectangle bar = new Rectangle(36, 3);
        bar.setFill(Color.web(color));
        bar.setArcWidth(3); bar.setArcHeight(3);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:11px; -fx-text-fill:" + MUTED + ";");

        Label val = new Label(value);
        val.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:" + color
                + "; -fx-font-family:'Courier New';");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size:10px; -fx-text-fill:" + MUTED + ";");

        card.getChildren().addAll(bar, lbl, val, s);
        return card;
    }

    // ── TABLE ─────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildTableArea() {
        VBox area = new VBox(12);
        area.setPadding(new Insets(0, 28, 20, 28));

        Label title = new Label("HISTORIQUE DES FACTURES");
        title.setStyle("-fx-font-size:11px; -fx-text-fill:" + MUTED
                + "; -fx-font-family:'Courier New'; -fx-font-weight:bold;");

        TableView<FactureRow> table = new TableView<>(factureRows);
        table.setStyle(
            "-fx-background-color:" + CARD + ";"
            + "-fx-border-color:" + BORDER + ";"
            + "-fx-border-radius:10;"
            + "-fx-background-radius:10;"
            + "-fx-table-cell-border-color:transparent;"
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<FactureRow, String> colId   = col("ID Facture",  "idFacture",  "12%");
        TableColumn<FactureRow, String> colNom  = col("Nom Client",  "nomClient",  "22%");
        TableColumn<FactureRow, String> colAddr = col("Adresse",     "adresse",    "25%");
        TableColumn<FactureRow, String> colComp = col("N° Compteur", "numCompteur","20%");
        TableColumn<FactureRow, String> colMont = col("Montant (€)", "montant",    "15%");

        // Montant colored
        colMont.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item + " €");
                    setStyle("-fx-text-fill:" + SUCCESS + "; -fx-font-weight:bold;"
                            + "-fx-font-family:'Courier New';");
                }
            }
        });

        table.getColumns().addAll(colId, colNom, colAddr, colComp, colMont);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_RIGHT);
        Button btnRefresh = new Button("🔄 Actualiser");
        btnRefresh.setStyle("-fx-background-color:" + BORDER + "; -fx-text-fill:" + TEXT
                + "; -fx-background-radius:6; -fx-padding:7 14; -fx-cursor:hand;");
        btnRefresh.setOnAction(e -> loadFactures());
        toolbar.getChildren().add(btnRefresh);

        area.getChildren().addAll(title, toolbar, table);
        return area;
    }

    private <T> TableColumn<FactureRow, T> col(String header, String prop, String pctWidth) {
        TableColumn<FactureRow, T> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setStyle("-fx-alignment:CENTER-LEFT;");
        return c;
    }

    // ── STATUS BAR ────────────────────────────────────────────────────────────
    private HBox buildStatusBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(8, 16, 8, 16));
        bar.setStyle("-fx-background-color:" + CARD + "; -fx-border-color:" + BORDER
                + "; -fx-border-width:1 0 0 0;");
        statusLabel = new Label("✅ Système prêt");
        statusLabel.setStyle("-fx-text-fill:" + MUTED + "; -fx-font-size:11px;");
        bar.getChildren().add(statusLabel);
        return bar;
    }

    // ── ADD CLIENT DIALOG ─────────────────────────────────────────────────────
    private void showAddClientDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.setTitle("Nouveau client");
        dialog.setResizable(false);

        VBox root = new VBox(16);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setPrefWidth(420);

        Label title = new Label("➕ Ajouter un client");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:" + TEXT
                + "; -fx-font-family:'Courier New';");

        TextField tfNom   = styledField("Nom complet");
        TextField tfAddr  = styledField("Adresse");
        TextField tfComp  = styledField("Numéro compteur (14 chiffres)");
        TextField tfConso = styledField("Consommation (kWh)");

        Label errLabel = new Label("");
        errLabel.setStyle("-fx-text-fill:" + DANGER + "; -fx-font-size:12px;");

        Button btnOk = new Button("✔ Enregistrer");
        btnOk.setStyle("-fx-background-color:" + ACCENT + "; -fx-text-fill:#000;"
                + "-fx-font-weight:bold; -fx-font-size:13px; -fx-background-radius:8;"
                + "-fx-padding:10 22; -fx-cursor:hand;");
        btnOk.setMaxWidth(Double.MAX_VALUE);

        Button btnCancel = new Button("Annuler");
        btnCancel.setStyle("-fx-background-color:transparent; -fx-text-fill:" + MUTED
                + "; -fx-font-size:12px; -fx-border-color:" + BORDER
                + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:9 22;");
        btnCancel.setMaxWidth(Double.MAX_VALUE);
        btnCancel.setOnAction(e -> dialog.close());

        btnOk.setOnAction(e -> {
            String nom   = tfNom.getText().trim();
            String addr  = tfAddr.getText().trim();
            String compS = tfComp.getText().trim();
            String consoS = tfConso.getText().trim();

            if (nom.isEmpty() || addr.isEmpty() || compS.isEmpty() || consoS.isEmpty()) {
                errLabel.setText("⚠ Tous les champs sont obligatoires.");
                return;
            }
            if (compS.length() != 14 || !compS.matches("\\d+")) {
                errLabel.setText("❌ Numéro compteur invalide (14 chiffres requis).");
                return;
            }
            try {
                long numComp = Long.parseLong(compS);
                double conso = Double.parseDouble(consoS);

                Client client = new Client(nom, addr);
                client.sauvegarder();

                Compteur compteur = new Compteur(client.getId(), conso, numComp);
                compteur.sauvegarder();

                Facture facture = new Facture(client, compteur);
                facture.sauvegarder();

                setStatus("✅ Client \"" + nom + "\" ajouté avec succès !");
                loadFactures();
                dialog.close();
            } catch (NumberFormatException ex) {
                errLabel.setText("❌ Consommation invalide.");
            } catch (Exception ex) {
                errLabel.setText("❌ Erreur : " + ex.getMessage());
            }
        });

        HBox buttons = new HBox(10, btnOk, btnCancel);
        HBox.setHgrow(btnOk, Priority.ALWAYS);
        HBox.setHgrow(btnCancel, Priority.ALWAYS);

        root.getChildren().addAll(title,
            fieldGroup("Nom", tfNom),
            fieldGroup("Adresse", tfAddr),
            fieldGroup("N° Compteur", tfComp),
            fieldGroup("Consommation (kWh)", tfConso),
            errLabel, buttons);

        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private VBox fieldGroup(String label, TextField tf) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:" + MUTED + "; -fx-font-size:11px;");
        return new VBox(4, lbl, tf);
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color:#12151f; -fx-text-fill:" + TEXT
                + "; -fx-prompt-text-fill:" + MUTED
                + "; -fx-border-color:" + BORDER + "; -fx-border-radius:6;"
                + "-fx-background-radius:6; -fx-padding:10 12; -fx-font-size:13px;");
        return tf;
    }

    // ── DATA ──────────────────────────────────────────────────────────────────
    private void loadFactures() {
        factureRows.clear();
        try {
            Connection conn = ConnexionDB.getConnexion();
            String sql = "SELECT f.idfacture, c.nom, c.adresse, f.numcompteur, f.montant "
                    + "FROM factures f JOIN clients c ON f.idclient = c.idclient "
                    + "ORDER BY f.idfacture DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                factureRows.add(new FactureRow(
                    String.valueOf(rs.getInt("idfacture")),
                    rs.getString("nom"),
                    rs.getString("adresse"),
                    rs.getString("numcompteur"),
                    String.format("%.2f", rs.getDouble("montant"))
                ));
            }
            setStatus("✅ " + factureRows.size() + " facture(s) chargée(s)");
        } catch (Exception e) {
            setStatus("❌ Erreur chargement : " + e.getMessage());
        }
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }

    // ── MODEL ─────────────────────────────────────────────────────────────────
    public static class FactureRow {
        private final javafx.beans.property.SimpleStringProperty idFacture, nomClient,
                adresse, numCompteur, montant;

        public FactureRow(String id, String nom, String addr, String num, String mont) {
            idFacture   = new javafx.beans.property.SimpleStringProperty(id);
            nomClient   = new javafx.beans.property.SimpleStringProperty(nom);
            adresse     = new javafx.beans.property.SimpleStringProperty(addr);
            numCompteur = new javafx.beans.property.SimpleStringProperty(num);
            montant     = new javafx.beans.property.SimpleStringProperty(mont);
        }

        public String getIdFacture()   { return idFacture.get(); }
        public String getNomClient()   { return nomClient.get(); }
        public String getAdresse()     { return adresse.get(); }
        public String getNumCompteur() { return numCompteur.get(); }
        public String getMontant()     { return montant.get(); }
    }
}