package projet;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class ConnexionGU extends JFrame {

    private static final long serialVersionUID = 1L;

    // ── PALETTE DARK ─────────────────────────────────────────
    private static final Color BG     = new Color(15,  17,  23);
    private static final Color CARD   = new Color(26,  29,  39);
    private static final Color CARD2  = new Color(20,  23,  33);
    private static final Color ACCENT = new Color(0,   212, 170);
    private static final Color TEXT   = new Color(232, 234, 240);
    private static final Color MUTED  = new Color(140, 145, 160);
    private static final Color BORDER = new Color(42,  45,  58);
    private static final Color DANGER = new Color(255, 77,  109);
    private static final Color GOLD   = new Color(245, 158, 11);

    // ── Fichier login ─────────────────────────────────────────
    private static final String FICHIER_LOGIN = "dernier_login.txt";

    // ── Connexion ─────────────────────────────────────────────
    private JTextField     tfLogin;
    private JPasswordField tfPassword;
    private JLabel         lblErreur;

    // ── Inscription ───────────────────────────────────────────
    private JTextField     tfNom;
    private JTextField     tfAdresse;
    private JPasswordField tfNouveauMdp;
    private JPasswordField tfConfirmMdp;
    private JLabel         lblErreurInscription;

    // ── Onglets ───────────────────────────────────────────────
    private JButton    btnOngletConnexion;
    private JButton    btnOngletInscription;
    private JPanel     panelContenu;
    private CardLayout cardLayout;

    // ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try { new ConnexionGU().setVisible(true); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    public ConnexionGU() {
        setTitle("GestionFacturation — Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());
        add(buildPanel(), BorderLayout.CENTER);

        // Charger le login sauvegardé après construction
        SwingUtilities.invokeLater(() -> chargerLoginSauvegarde());
    }

    // ══════════════════════════════════════════════════════════
    // PANEL PRINCIPAL
    // ══════════════════════════════════════════════════════════
    private JPanel buildPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(28, 36, 28, 36)));
        card.setPreferredSize(new Dimension(370, 540));

        // ── Logo ─────────────────────────────────────────────
        JLabel logo = new JLabel("⚡");
        logo.setFont(new Font("SansSerif", Font.PLAIN, 36));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titre = new JLabel("GestionFactu");
        titre.setFont(new Font("Monospaced", Font.BOLD, 20));
        titre.setForeground(TEXT);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Onglets ───────────────────────────────────────────
        JPanel onglets = buildOnglets();

        // ── Contenu switchable ────────────────────────────────
        cardLayout   = new CardLayout();
        panelContenu = new JPanel(cardLayout);
        panelContenu.setBackground(CARD);
        panelContenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelContenu.add(buildPanelConnexion(),   "connexion");
        panelContenu.add(buildPanelInscription(), "inscription");

        card.add(logo);
        card.add(Box.createVerticalStrut(4));
        card.add(titre);
        card.add(Box.createVerticalStrut(20));
        card.add(onglets);
        card.add(Box.createVerticalStrut(20));
        card.add(panelContenu);

        outer.add(card);
        return outer;
    }

    // ══════════════════════════════════════════════════════════
    // ONGLETS
    // ══════════════════════════════════════════════════════════
    private JPanel buildOnglets() {
        JPanel p = new JPanel(new GridLayout(1, 2, 0, 0));
        p.setBackground(CARD);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setBorder(new MatteBorder(0, 0, 2, 0, BORDER));

        btnOngletConnexion   = ongletBtn("Connexion",          true);
        btnOngletInscription = ongletBtn("Premiere connexion", false);

        btnOngletConnexion.addActionListener(e   -> switchOnglet("connexion"));
        btnOngletInscription.addActionListener(e -> switchOnglet("inscription"));

        p.add(btnOngletConnexion);
        p.add(btnOngletInscription);
        return p;
    }

    private JButton ongletBtn(String text, boolean actif) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", actif ? Font.BOLD : Font.PLAIN, 12));
        b.setForeground(actif ? ACCENT : MUTED);
        b.setBackground(CARD);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new CompoundBorder(
                actif ? new MatteBorder(0, 0, 2, 0, ACCENT)
                      : new MatteBorder(0, 0, 0, 0, CARD),
                new EmptyBorder(8, 0, 8, 0)));
        return b;
    }

    private void switchOnglet(String nom) {
        cardLayout.show(panelContenu, nom);
        boolean isConnexion = nom.equals("connexion");

        btnOngletConnexion.setForeground(isConnexion ? ACCENT : MUTED);
        btnOngletConnexion.setFont(new Font("SansSerif",
                isConnexion ? Font.BOLD : Font.PLAIN, 12));
        btnOngletConnexion.setBorder(new CompoundBorder(
                isConnexion ? new MatteBorder(0, 0, 2, 0, ACCENT)
                            : new MatteBorder(0, 0, 0, 0, CARD),
                new EmptyBorder(8, 0, 8, 0)));

        btnOngletInscription.setForeground(!isConnexion ? ACCENT : MUTED);
        btnOngletInscription.setFont(new Font("SansSerif",
                !isConnexion ? Font.BOLD : Font.PLAIN, 12));
        btnOngletInscription.setBorder(new CompoundBorder(
                !isConnexion ? new MatteBorder(0, 0, 2, 0, ACCENT)
                             : new MatteBorder(0, 0, 0, 0, CARD),
                new EmptyBorder(8, 0, 8, 0)));
    }

    // ══════════════════════════════════════════════════════════
    // PANEL CONNEXION
    // ══════════════════════════════════════════════════════════
    private JPanel buildPanelConnexion() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD);

        tfLogin    = styledField();
        tfPassword = new JPasswordField();
        styleChamp(tfPassword);

        lblErreur = new JLabel(" ");
        lblErreur.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblErreur.setForeground(DANGER);
        lblErreur.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton bConnexion = actionBtn("Se connecter", ACCENT);
        bConnexion.addActionListener(e -> tenterConnexion());
        tfPassword.addActionListener(e -> tenterConnexion());
        tfLogin.addActionListener(e -> tfPassword.requestFocus());

        p.add(fieldGroup("Votre ID de connexion", tfLogin));
        p.add(Box.createVerticalStrut(14));
        p.add(fieldGroup("Mot de passe", tfPassword));
        p.add(Box.createVerticalStrut(12));
        p.add(lblErreur);
        p.add(Box.createVerticalStrut(8));
        p.add(bConnexion);
        p.add(Box.createVerticalStrut(16));
        p.add(buildFooterConnexion());
        return p;
    }

    private JPanel buildFooterConnexion() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        f.setBackground(CARD);
        f.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel txt  = new JLabel("Premiere fois ?");
        txt.setFont(new Font("SansSerif", Font.PLAIN, 11));
        txt.setForeground(MUTED);

        JLabel lien = new JLabel("Creer un compte");
        lien.setFont(new Font("SansSerif", Font.BOLD, 11));
        lien.setForeground(ACCENT);
        lien.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                switchOnglet("inscription");
            }
        });

        f.add(txt);
        f.add(lien);
        return f;
    }

    // ══════════════════════════════════════════════════════════
    // PANEL INSCRIPTION
    // ══════════════════════════════════════════════════════════
    private JPanel buildPanelInscription() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD);

        tfNom        = styledField();
        tfAdresse    = styledField();
        tfNouveauMdp = new JPasswordField();
        tfConfirmMdp = new JPasswordField();
        styleChamp(tfNouveauMdp);
        styleChamp(tfConfirmMdp);

        lblErreurInscription = new JLabel(" ");
        lblErreurInscription.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblErreurInscription.setForeground(DANGER);
        lblErreurInscription.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton bCreer = actionBtn("Creer mon compte", GOLD);
        bCreer.setForeground(Color.BLACK);
        bCreer.addActionListener(e -> creerCompte());

        p.add(fieldGroup("Nom complet *",               tfNom));
        p.add(Box.createVerticalStrut(10));
        p.add(fieldGroup("Adresse *",                   tfAdresse));
        p.add(Box.createVerticalStrut(10));
        p.add(fieldGroup("Mot de passe *",              tfNouveauMdp));
        p.add(Box.createVerticalStrut(10));
        p.add(fieldGroup("Confirmer le mot de passe *", tfConfirmMdp));
        p.add(Box.createVerticalStrut(10));
        p.add(lblErreurInscription);
        p.add(Box.createVerticalStrut(8));
        p.add(bCreer);
        p.add(Box.createVerticalStrut(14));
        p.add(buildFooterInscription());
        return p;
    }

    private JPanel buildFooterInscription() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        f.setBackground(CARD);
        f.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel txt  = new JLabel("Deja un compte ?");
        txt.setFont(new Font("SansSerif", Font.PLAIN, 11));
        txt.setForeground(MUTED);

        JLabel lien = new JLabel("Se connecter");
        lien.setFont(new Font("SansSerif", Font.BOLD, 11));
        lien.setForeground(ACCENT);
        lien.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                switchOnglet("connexion");
            }
        });

        f.add(txt);
        f.add(lien);
        return f;
    }

    // ══════════════════════════════════════════════════════════
    // STYLES
    // ══════════════════════════════════════════════════════════
    private JTextField styledField() {
        JTextField tf = new JTextField();
        styleChamp(tf);
        return tf;
    }

    private void styleChamp(JComponent c) {
        c.setBackground(CARD2);
        c.setForeground(TEXT);
        c.setFont(new Font("SansSerif", Font.PLAIN, 13));
        c.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)));
        if (c instanceof JTextField)
            ((JTextField) c).setCaretColor(ACCENT);
        if (c instanceof JPasswordField)
            ((JPasswordField) c).setCaretColor(ACCENT);
    }

    private JPanel fieldGroup(String label, JComponent champ) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        champ.setAlignmentX(Component.LEFT_ALIGNMENT);
        champ.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        p.add(lbl);
        p.add(Box.createVerticalStrut(5));
        p.add(champ);
        return p;
    }

    private JButton actionBtn(String text, Color couleur) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setForeground(Color.BLACK);
        b.setBackground(couleur);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(12, 0, 12, 0));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    // ══════════════════════════════════════════════════════════
    // LOGIQUE CONNEXION
    // ══════════════════════════════════════════════════════════
    private void tenterConnexion() {
        String login = tfLogin.getText().trim();
        String mdp   = new String(tfPassword.getPassword()).trim();

        if (login.isEmpty() || mdp.isEmpty()) {
            lblErreur.setForeground(DANGER);
            lblErreur.setText("Veuillez remplir tous les champs.");
            return;
        }

        try {
            Connection conn = ConnexionDB.getConnexion();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT u.login, c.nom FROM utilisateurs u " +
                "JOIN clients c ON u.idclient = c.idclient " +
                "WHERE u.login = ? AND u.motdepasse = ?");
            ps.setString(1, login);
            ps.setString(2, mdp);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String nomClient = rs.getString("nom");
                ouvrirApplication(nomClient);
            } else {
                lblErreur.setForeground(DANGER);
                lblErreur.setText("ID ou mot de passe incorrect.");
                tfPassword.setText("");
            }
        } catch (Exception e) {
            lblErreur.setForeground(DANGER);
            lblErreur.setText("Erreur : " + e.getMessage());
        }
    }

    private void ouvrirApplication(String nomClient) {
        sauvegarderLogin(tfLogin.getText().trim());
        lblErreur.setForeground(ACCENT);
        lblErreur.setText("Bienvenue " + nomClient + " !");
        Timer timer = new Timer(800, e -> {
            new MainGU().setVisible(true);
            dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // ══════════════════════════════════════════════════════════
    // LOGIQUE CREATION DE COMPTE
    // ══════════════════════════════════════════════════════════
    private void creerCompte() {
        String nom     = tfNom.getText().trim();
        String adresse = tfAdresse.getText().trim();
        String mdp     = new String(tfNouveauMdp.getPassword()).trim();
        String confirm = new String(tfConfirmMdp.getPassword()).trim();

        // ── Validations ───────────────────────────────────────
        if (nom.isEmpty() || adresse.isEmpty() || mdp.isEmpty() || confirm.isEmpty()) {
            afficherErreurInscription("Tous les champs sont obligatoires.");
            return;
        }
        if (mdp.length() < 4) {
            afficherErreurInscription("Le mot de passe doit avoir au moins 4 caracteres.");
            return;
        }
        if (!mdp.equals(confirm)) {
            afficherErreurInscription("Les mots de passe ne correspondent pas.");
            tfConfirmMdp.setText("");
            return;
        }

        try {
            Connection conn = ConnexionDB.getConnexion();

            // ── Créer le client → ID auto via classe Id ───────
            Client client = new Client(nom, adresse);
            client.sauvegarder();
            int idGenere = client.getId();

            // ── Créer le compte utilisateur ───────────────────
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO utilisateurs (login, motdepasse, idclient) VALUES (?, ?, ?)");
            ps.setString(1, String.valueOf(idGenere));
            ps.setString(2, mdp);
            ps.setInt(3, idGenere);
            ps.executeUpdate();

            // ── Sauvegarder l'ID ──────────────────────────────
            sauvegarderLogin(String.valueOf(idGenere));

            // ── Vider le formulaire ───────────────────────────
            tfNom.setText("");
            tfAdresse.setText("");
            tfNouveauMdp.setText("");
            tfConfirmMdp.setText("");

            // ── Afficher la popup avec l'ID ───────────────────
            afficherPopupID(idGenere, nom);

        } catch (Exception e) {
            afficherErreurInscription("Erreur : " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // POPUP ID
    // ══════════════════════════════════════════════════════════
    private void afficherPopupID(int idGenere, String nom) {
        JDialog popup = new JDialog(this, "Votre identifiant", true);
        popup.setSize(380, 300);
        popup.setResizable(false);
        popup.setLocationRelativeTo(this);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(CARD);
        root.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel icone = new JLabel("✅");
        icone.setFont(new Font("SansSerif", Font.PLAIN, 32));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblBienvenue = new JLabel("Bienvenue " + nom + " !");
        lblBienvenue.setFont(new Font("Monospaced", Font.BOLD, 15));
        lblBienvenue.setForeground(TEXT);
        lblBienvenue.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMsg = new JLabel("Votre identifiant de connexion est :");
        lblMsg.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblMsg.setForeground(MUTED);
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── ID affiché en grand ───────────────────────────────
        JLabel lblId = new JLabel(String.valueOf(idGenere));
        lblId.setFont(new Font("Monospaced", Font.BOLD, 36));
        lblId.setForeground(ACCENT);
        lblId.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAvert = new JLabel("Notez bien cet ID, il vous servira a vous connecter !");
        lblAvert.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblAvert.setForeground(GOLD);
        lblAvert.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton bOk = actionBtn("J'ai note mon ID, continuer", ACCENT);
        bOk.addActionListener(e -> {
            tfLogin.setText(String.valueOf(idGenere));
            tfPassword.setText("");
            popup.dispose();
            switchOnglet("connexion");
            tfPassword.requestFocus();
        });

        root.add(icone);
        root.add(Box.createVerticalStrut(8));
        root.add(lblBienvenue);
        root.add(Box.createVerticalStrut(10));
        root.add(lblMsg);
        root.add(Box.createVerticalStrut(10));
        root.add(lblId);
        root.add(Box.createVerticalStrut(8));
        root.add(lblAvert);
        root.add(Box.createVerticalStrut(18));
        root.add(bOk);

        popup.setContentPane(root);
        popup.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════
    // SAUVEGARDE LOGIN
    // ══════════════════════════════════════════════════════════
    private void sauvegarderLogin(String login) {
        try {
            java.io.FileWriter fw = new java.io.FileWriter(FICHIER_LOGIN);
            fw.write(login);
            fw.close();
        } catch (Exception e) {
            System.out.println("Impossible de sauvegarder le login : " + e.getMessage());
        }
    }

    private void chargerLoginSauvegarde() {
        try {
            java.io.File fichier = new java.io.File(FICHIER_LOGIN);
            if (fichier.exists()) {
                java.util.Scanner sc = new java.util.Scanner(fichier);
                if (sc.hasNextLine()) {
                    String loginSauvegarde = sc.nextLine().trim();
                    if (!loginSauvegarde.isEmpty()) {
                        tfLogin.setText(loginSauvegarde);
                        tfPassword.requestFocusInWindow();
                    }
                }
                sc.close();
            }
        } catch (Exception e) {
            System.out.println("Impossible de charger le login : " + e.getMessage());
        }
    }

    private void afficherErreurInscription(String msg) {
        lblErreurInscription.setForeground(DANGER);
        lblErreurInscription.setText(msg);
    }
}


