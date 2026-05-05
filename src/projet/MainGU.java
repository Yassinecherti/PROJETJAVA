package projet;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.print.*;
import java.sql.*;

// ── iText PDF ────────────────────────────────────────────────

public class MainGU extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color BG      = new Color(15,  17,  23);
    private static final Color CARD    = new Color(26,  29,  39);
    private static final Color CARD2   = new Color(20,  23,  33);
    private static final Color ACCENT  = new Color(0,   212, 170);
    private static final Color ACCENT2 = new Color(0,   153, 255);
    private static final Color GOLD    = new Color(245, 158, 11);
    private static final Color TEXT    = new Color(232, 234, 240);
    private static final Color MUTED   = new Color(140, 145, 160);
    private static final Color BORDER  = new Color(42,  45,  58);
    private static final Color DANGER  = new Color(255, 77,  109);

    private JTextField        tfRecherche;
    private JLabel            lblNom, lblAdresse, lblIdClient, lblNbFactures, lblTotalClient;
    private DefaultTableModel tableModel;
    private JLabel            statusLabel;
    private JPanel            panelRecu;

    private int    currentClientId   = -1;
    private String currentClientNom  = "";
    private String currentClientAddr = "";

    // Données de la facture sélectionnée (pour le PDF)
    private String selectedIdFact  = "";
    private String selectedNumComp = "";
    private String selectedConso   = "";
    private String selectedMontant = "";
    private String selectedDate    = "";

    public static void main(String[] args) {
        	 EventQueue.invokeLater(() -> {
        	        try { new ConnexionGU().setVisible(true); }
        	        catch (Exception e) { e.printStackTrace(); }
        	    });
        	 
    }

    public MainGU() {
        setTitle("GestionFacturation — Factures Client");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildSidebar(),   BorderLayout.WEST);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── SIDEBAR ───────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(CARD);
        side.setPreferredSize(new Dimension(260, 0));
        side.setBorder(new MatteBorder(0, 0, 0, 1, BORDER));

        JPanel logo = new JPanel();
        logo.setLayout(new BoxLayout(logo, BoxLayout.Y_AXIS));
        logo.setBackground(CARD);
        logo.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(22, 20, 18, 20)));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t1 = new JLabel("GestionFactu");
        t1.setFont(new Font("Monospaced", Font.BOLD, 17));
        t1.setForeground(TEXT);
        t1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t2 = new JLabel("Factures par client");
        t2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        t2.setForeground(MUTED);
        t2.setAlignmentX(Component.LEFT_ALIGNMENT);

        logo.add(t1);
        logo.add(Box.createVerticalStrut(4));
        logo.add(t2);

        // Recherche
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(CARD);
        searchPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(16, 14, 16, 14)));
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lSearch = new JLabel("RECHERCHER UN CLIENT");
        lSearch.setFont(new Font("Monospaced", Font.BOLD, 10));
        lSearch.setForeground(MUTED);
        lSearch.setAlignmentX(Component.LEFT_ALIGNMENT);

        tfRecherche = new JTextField();
        tfRecherche.setBackground(CARD2);
        tfRecherche.setForeground(TEXT);
        tfRecherche.setCaretColor(ACCENT);
        tfRecherche.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tfRecherche.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)));
        tfRecherche.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfRecherche.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tfRecherche.addActionListener(e -> rechercherClient());

        JButton bSearch = new JButton("Rechercher");
        bSearch.setFont(new Font("SansSerif", Font.BOLD, 12));
        bSearch.setForeground(Color.BLACK);
        bSearch.setBackground(ACCENT);
        bSearch.setFocusPainted(false);
        bSearch.setBorderPainted(false);
        bSearch.setOpaque(true);
        bSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bSearch.setBorder(new EmptyBorder(9, 14, 9, 14));
        bSearch.setAlignmentX(Component.LEFT_ALIGNMENT);
        bSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        bSearch.addActionListener(e -> rechercherClient());

        searchPanel.add(lSearch);
        searchPanel.add(Box.createVerticalStrut(8));
        searchPanel.add(tfRecherche);
        searchPanel.add(Box.createVerticalStrut(8));
        searchPanel.add(bSearch);

        JPanel fichePanel = buildFicheClient();

        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(CARD);
        nav.setBorder(new EmptyBorder(10, 10, 10, 10));
        nav.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton bQuit = navBtn("Quitter", DANGER);
        bQuit.addActionListener(e -> System.exit(0));
        nav.add(bQuit);

        side.add(logo);
        side.add(searchPanel);
        side.add(fichePanel);
        side.add(Box.createVerticalGlue());
        side.add(nav);
        return side;
    }

    private JPanel buildFicheClient() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD2);
        card.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(16, 16, 16, 16)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titre = new JLabel("FICHE CLIENT");
        titre.setFont(new Font("Monospaced", Font.BOLD, 10));
        titre.setForeground(ACCENT);
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblIdClient    = ficheLabel("ID : —");
        lblNom         = ficheLabel("Nom : —");
        lblAdresse     = ficheLabel("Adresse : —");
        lblNbFactures  = ficheLabel("Factures : —");
        lblTotalClient = ficheLabel("Total : —");
        lblTotalClient.setForeground(GOLD);

        card.add(titre);
        card.add(Box.createVerticalStrut(10));
        card.add(lblIdClient);
        card.add(Box.createVerticalStrut(5));
        card.add(lblNom);
        card.add(Box.createVerticalStrut(5));
        card.add(lblAdresse);
        card.add(Box.createVerticalStrut(5));
        card.add(lblNbFactures);
        card.add(Box.createVerticalStrut(5));
        card.add(lblTotalClient);
        return card;
    }

    private JLabel ficheLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JButton navBtn(String text, Color accent) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setForeground(MUTED);
        b.setBackground(CARD);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(240, 38));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(6, 14, 6, 14));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setForeground(accent); }
            public void mouseExited (java.awt.event.MouseEvent e) { b.setForeground(MUTED);  }
        });
        return b;
    }

    // ── CENTER ────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(20, 28, 18, 28)));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setBackground(BG);

        JLabel h1 = new JLabel("Factures du Client");
        h1.setFont(new Font("Monospaced", Font.BOLD, 22));
        h1.setForeground(TEXT);
        h1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel h2 = new JLabel("Recherchez un client pour voir ses factures et générer un reçu");
        h2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        h2.setForeground(MUTED);
        h2.setAlignmentX(Component.LEFT_ALIGNMENT);

        titles.add(h1);
        titles.add(Box.createVerticalStrut(4));
        titles.add(h2);

        // ── Boutons header : Imprimer + Exporter PDF ──────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(BG);

        JButton bImprimer = headerBtn("Imprimer",     GOLD);
        JButton bPdf      = headerBtn("Exporter PDF", ACCENT2);

        bImprimer.addActionListener(e -> imprimerRecu());
        bPdf.addActionListener(e -> exporterPDF());

        btnPanel.add(bImprimer);
        btnPanel.add(bPdf);

        header.add(titles,   BorderLayout.WEST);
        header.add(btnPanel, BorderLayout.EAST);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildTableArea(), buildRecuPanel());
        split.setDividerLocation(320);
        split.setDividerSize(6);
        split.setBackground(BG);
        split.setBorder(null);
        split.setResizeWeight(0.55);

        center.add(header, BorderLayout.NORTH);
        center.add(split,  BorderLayout.CENTER);
        return center;
    }

    private JButton headerBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setForeground(Color.BLACK);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 16, 10, 16));
        return b;
    }

    // ── TABLEAU ───────────────────────────────────────────────
    private JPanel buildTableArea() {
        JPanel area = new JPanel(new BorderLayout(0, 8));
        area.setBackground(BG);
        area.setBorder(new EmptyBorder(16, 28, 8, 28));

        JLabel title = new JLabel("LISTE DES FACTURES");
        title.setFont(new Font("Monospaced", Font.BOLD, 11));
        title.setForeground(MUTED);

        String[] cols = {"ID Facture", "N° Compteur", "Consommation (kWh)", "Montant (EUR)", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? CARD : CARD2);
                c.setForeground(col == 3 ? ACCENT : col == 4 ? MUTED : TEXT);
                c.setFont(col == 3
                        ? new Font("Monospaced", Font.BOLD, 13)
                        : new Font("SansSerif",  Font.PLAIN, 13));
                return c;
            }
        };
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0, 212, 170, 50));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0)
                afficherRecu(table.getSelectedRow());
        });

        JTableHeader h = table.getTableHeader();
        h.setBackground(CARD2);
        h.setForeground(MUTED);
        h.setFont(new Font("Monospaced", Font.BOLD, 11));
        h.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 34));

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(170);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER, 1));
        scroll.getViewport().setBackground(CARD);
        scroll.setBackground(CARD);

        area.add(title,  BorderLayout.NORTH);
        area.add(scroll, BorderLayout.CENTER);
        return area;
    }

    // ── REÇU ─────────────────────────────────────────────────
    private JPanel buildRecuPanel() {
        panelRecu = new JPanel(new BorderLayout());
        panelRecu.setBackground(BG);
        panelRecu.setBorder(new EmptyBorder(8, 28, 16, 28));

        JLabel titre = new JLabel("APERÇU DU REÇU");
        titre.setFont(new Font("Monospaced", Font.BOLD, 11));
        titre.setForeground(MUTED);
        titre.setBorder(new EmptyBorder(0, 0, 8, 0));

        panelRecu.add(titre,      BorderLayout.NORTH);
        panelRecu.add(recuVide(), BorderLayout.CENTER);
        return panelRecu;
    }

    private JPanel recuVide() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setBorder(new LineBorder(BORDER, 1));
        JLabel msg = new JLabel("Sélectionnez une facture pour afficher le reçu",
                SwingConstants.CENTER);
        msg.setFont(new Font("SansSerif", Font.PLAIN, 13));
        msg.setForeground(MUTED);
        p.add(msg, BorderLayout.CENTER);
        return p;
    }

    // ── STATUS BAR ────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        bar.setBackground(CARD);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER));
        statusLabel = new JLabel("Entrez un nom ou un ID pour rechercher un client.");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(MUTED);
        bar.add(statusLabel);
        return bar;
    }

    // ══════════════════════════════════════════════════════════
    // LOGIQUE
    // ══════════════════════════════════════════════════════════
    private void rechercherClient() {
        String q = tfRecherche.getText().trim();
        if (q.isEmpty()) { setStatus("Saisissez un nom ou un ID."); return; }

        try {
            Connection conn = ConnexionDB.getConnexion();
            PreparedStatement ps;

            if (q.matches("\\d+")) {
                ps = conn.prepareStatement(
                    "SELECT idclient, nom, adresse FROM clients WHERE idclient = ?");
                ps.setInt(1, Integer.parseInt(q));
            } else {
                ps = conn.prepareStatement(
                    "SELECT idclient, nom, adresse FROM clients WHERE nom LIKE ?");
                ps.setString(1, "%" + q + "%");
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currentClientId   = rs.getInt("idclient");
                currentClientNom  = rs.getString("nom");
                currentClientAddr = rs.getString("adresse");
                mettreAJourFiche();
                chargerFacturesClient();
            } else {
                setStatus("Aucun client trouvé pour : " + q);
                viderFiche();
            }
        } catch (Exception e) {
            setStatus("Erreur : " + e.getMessage());
        }
    }

    private void mettreAJourFiche() {
        lblIdClient.setText("ID : "      + currentClientId);
        lblNom.setText("Nom : "          + currentClientNom);
        lblAdresse.setText("Adresse : "  + currentClientAddr);
    }

    private void viderFiche() {
        currentClientId = -1;
        lblIdClient.setText("ID : —");
        lblNom.setText("Nom : —");
        lblAdresse.setText("Adresse : —");
        lblNbFactures.setText("Factures : —");
        lblTotalClient.setText("Total : —");
        tableModel.setRowCount(0);
    }

    private void chargerFacturesClient() {
        tableModel.setRowCount(0);
        panelRecu.removeAll();
        panelRecu.add(recuVide(), BorderLayout.CENTER);
        panelRecu.revalidate();
        panelRecu.repaint();

        try {
            Connection conn = ConnexionDB.getConnexion();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT f.idfacture, f.numcompteur, co.consommation, f.montant, f.datefacture " +
                "FROM factures f " +
                "JOIN compteurs co ON f.numcompteur = co.numcompteur " +
                "WHERE f.idclient = ? ORDER BY f.idfacture DESC");
            ps.setInt(1, currentClientId);

            ResultSet rs = ps.executeQuery();
            int count = 0; double total = 0;

            while (rs.next()) {
                double montant = rs.getDouble("montant");
                total += montant; count++;
                tableModel.addRow(new Object[]{
                    rs.getInt("idfacture"),
                    rs.getString("numcompteur"),
                    String.format("%.2f", rs.getDouble("consommation")),
                    String.format("%.2f", montant),
                    rs.getString("datefacture") != null ? rs.getString("datefacture") : "—"
                });
            }
            lblNbFactures.setText("Factures : " + count);
            lblTotalClient.setText(String.format("Total : %.2f EUR", total));
            setStatus(count + " facture(s) trouvée(s) pour " + currentClientNom);

        } catch (Exception e) {
            setStatus("Erreur chargement : " + e.getMessage());
        }
    }

    private void afficherRecu(int row) {
        // Sauvegarder les données de la ligne sélectionnée pour le PDF
        selectedIdFact  = tableModel.getValueAt(row, 0).toString();
        selectedNumComp = tableModel.getValueAt(row, 1).toString();
        selectedConso   = tableModel.getValueAt(row, 2).toString();
        selectedMontant = tableModel.getValueAt(row, 3).toString();
        selectedDate    = tableModel.getValueAt(row, 4).toString();

        JPanel recu = new JPanel();
        recu.setLayout(new BoxLayout(recu, BoxLayout.Y_AXIS));
        recu.setBackground(CARD);
        recu.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(20, 28, 20, 28)));

        recu.add(recuLigne("FACTURE ÉLECTRIQUE",               true,  ACCENT));
        recu.add(Box.createVerticalStrut(2));
        recu.add(recuSep());
        recu.add(Box.createVerticalStrut(10));
        recu.add(recuLigne("N° Facture  : " + selectedIdFact,  false, TEXT));
        recu.add(recuLigne("Date        : " + selectedDate,    false, TEXT));
        recu.add(Box.createVerticalStrut(8));
        recu.add(recuSep());
        recu.add(Box.createVerticalStrut(8));
        recu.add(recuLigne("CLIENT",                           true,  MUTED));
        recu.add(recuLigne("Nom         : " + currentClientNom,  false, TEXT));
        recu.add(recuLigne("Adresse     : " + currentClientAddr, false, TEXT));
        recu.add(Box.createVerticalStrut(8));
        recu.add(recuSep());
        recu.add(Box.createVerticalStrut(8));
        recu.add(recuLigne("DÉTAIL",                           true,  MUTED));
        recu.add(recuLigne("N° Compteur : " + selectedNumComp, false, TEXT));
        recu.add(recuLigne("Consommation: " + selectedConso + " kWh", false, TEXT));
        recu.add(Box.createVerticalStrut(8));
        recu.add(recuSep());
        recu.add(Box.createVerticalStrut(8));
        recu.add(recuLigne("MONTANT TOTAL : " + selectedMontant + " EUR", true, GOLD));

        JScrollPane scroll = new JScrollPane(recu);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        JLabel titre = new JLabel("APERÇU DU REÇU");
        titre.setFont(new Font("Monospaced", Font.BOLD, 11));
        titre.setForeground(MUTED);
        titre.setBorder(new EmptyBorder(0, 0, 8, 0));

        panelRecu.removeAll();
        panelRecu.add(titre,  BorderLayout.NORTH);
        panelRecu.add(scroll, BorderLayout.CENTER);
        panelRecu.revalidate();
        panelRecu.repaint();
    }

    private JLabel recuLigne(String t, boolean bold, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Monospaced", bold ? Font.BOLD : Font.PLAIN, 13));
        l.setForeground(c);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JSeparator recuSep() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        s.setBackground(BORDER);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        return s;
    }

    // ── EXPORT PDF (iText) ────────────────────────────────────
    private void exporterPDF() {
        if (currentClientId == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez d'abord rechercher un client.",
                "Aucun client", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedIdFact.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une facture dans le tableau.",
                "Aucune facture sélectionnée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Choisir où sauvegarder
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(
            "Facture_" + currentClientNom.replaceAll("\\s+", "_") + "_" + selectedIdFact + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String chemin = chooser.getSelectedFile().getAbsolutePath();
        if (!chemin.endsWith(".pdf")) chemin += ".pdf";

        try {
            // ── Construction du PDF en pur Java ──────────────────

            // Contenu texte de la facture
            StringBuilder contenu = new StringBuilder();
            contenu.append("================================================\n");
            contenu.append("          FACTURE D'ELECTRICITE                 \n");
            contenu.append("================================================\n\n");
            contenu.append("N Facture   : ").append(selectedIdFact).append("\n");
            contenu.append("Date        : ").append(selectedDate).append("\n\n");
            contenu.append("------------------------------------------------\n");
            contenu.append("CLIENT\n");
            contenu.append("------------------------------------------------\n");
            contenu.append("Nom         : ").append(currentClientNom).append("\n");
            contenu.append("Adresse     : ").append(currentClientAddr).append("\n\n");
            contenu.append("------------------------------------------------\n");
            contenu.append("DETAIL\n");
            contenu.append("------------------------------------------------\n");
            contenu.append("N Compteur  : ").append(selectedNumComp).append("\n");
            contenu.append("Consommation: ").append(selectedConso).append(" kWh\n\n");
            contenu.append("================================================\n");
            contenu.append("MONTANT TOTAL : ").append(selectedMontant).append(" EUR\n");
            contenu.append("================================================\n");

            // ── Ecriture PDF manuel (format PDF minimal) ──────────
            writePDF(chemin, contenu.toString());

            setStatus("PDF genere : " + chemin);
            JOptionPane.showMessageDialog(this,
                "PDF exporte avec succes !\n" + chemin,
                "Export reussi", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            setStatus("Erreur PDF : " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erreur : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Générateur PDF minimal sans bibliothèque ─────────────────
    private void writePDF(String chemin, String texte) throws Exception {
        java.io.FileOutputStream fos = new java.io.FileOutputStream(chemin);

        // Découper le texte en lignes
        String[] lignes = texte.split("\n");

        // ── Préparer le flux de contenu ───────────────────────────
        StringBuilder stream = new StringBuilder();
        stream.append("BT\n");
        stream.append("/F1 11 Tf\n");

        int x = 50;
        int y = 750;

        for (String ligne : lignes) {
            // Echapper les parenthèses et antislash pour PDF
            String safe = ligne
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");

            // Titre principal en gras simulé (taille 14)
            if (ligne.contains("FACTURE D'ELECTRICITE")) {
                stream.append("/F1 14 Tf\n");
                stream.append(x).append(" ").append(y).append(" Td\n");
                stream.append("(").append(safe).append(") Tj\n");
                stream.append("0 -18 Td\n");
                stream.append("/F1 11 Tf\n");
            }
            // Ligne montant en gras simulé (taille 13)
            else if (ligne.contains("MONTANT TOTAL")) {
                stream.append("/F1 13 Tf\n");
                stream.append("(").append(safe).append(") Tj\n");
                stream.append("0 -16 Td\n");
                stream.append("/F1 11 Tf\n");
            }
            // Sections
            else if (ligne.equals("CLIENT") || ligne.equals("DETAIL")) {
                stream.append("/F1 12 Tf\n");
                stream.append("(").append(safe).append(") Tj\n");
                stream.append("0 -16 Td\n");
                stream.append("/F1 11 Tf\n");
            }
            // Ligne normale
            else {
                stream.append("(").append(safe).append(") Tj\n");
                stream.append("0 -16 Td\n");
            }
        }
        stream.append("ET\n");

        byte[] streamBytes = stream.toString().getBytes("ISO-8859-1");

        // ── Objets PDF ────────────────────────────────────────────
        int[] offsets = new int[7];
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();

        // Header PDF
        byte[] header = "%PDF-1.4\n".getBytes();
        bos.write(header);

        // Objet 1 : Catalogue
        offsets[1] = bos.size();
        String obj1 = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n";
        bos.write(obj1.getBytes());

        // Objet 2 : Pages
        offsets[2] = bos.size();
        String obj2 = "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n";
        bos.write(obj2.getBytes());

        // Objet 3 : Page
        offsets[3] = bos.size();
        String obj3 = "3 0 obj\n<< /Type /Page /Parent 2 0 R "
                + "/MediaBox [0 0 595 842] "
                + "/Contents 4 0 R "
                + "/Resources << /Font << /F1 5 0 R >> >> >>\nendobj\n";
        bos.write(obj3.getBytes());

        // Objet 4 : Contenu
        offsets[4] = bos.size();
        String obj4head = "4 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n";
        bos.write(obj4head.getBytes());
        bos.write(streamBytes);
        bos.write("\nendstream\nendobj\n".getBytes());

        // Objet 5 : Police Helvetica
        offsets[5] = bos.size();
        String obj5 = "5 0 obj\n<< /Type /Font /Subtype /Type1 "
                + "/BaseFont /Helvetica >>\nendobj\n";
        bos.write(obj5.getBytes());

        // ── Table de références croisées (xref) ───────────────────
        int xrefOffset = bos.size();
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n");
        xref.append("0 6\n");
        xref.append("0000000000 65535 f \n");
        for (int i = 1; i <= 5; i++) {
            xref.append(String.format("%010d 00000 n \n", offsets[i]));
        }
        bos.write(xref.toString().getBytes());

        // ── Trailer ───────────────────────────────────────────────
        String trailer = "trailer\n<< /Size 6 /Root 1 0 R >>\n"
                + "startxref\n" + xrefOffset + "\n%%EOF\n";
        bos.write(trailer.getBytes());

        fos.write(bos.toByteArray());
        fos.close();
    }
    // ── IMPRESSION ────────────────────────────────────────────
    private void imprimerRecu() {
        if (currentClientId == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez d'abord rechercher un client.",
                "Aucun client", JOptionPane.WARNING_MESSAGE);
            return;
        }
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Facture - " + currentClientNom);
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2.setColor(Color.BLACK);
            int y = 30, x = 40;
            g2.setFont(new Font("Monospaced", Font.BOLD, 16));
            g2.drawString("FACTURE ÉLECTRIQUE", x, y); y += 22;
            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.drawString("Client  : " + currentClientNom,  x, y); y += 18;
            g2.drawString("Adresse : " + currentClientAddr, x, y); y += 22;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                g2.drawString(String.format("Facture #%s | %s kWh | %s EUR | %s",
                    tableModel.getValueAt(i, 0), tableModel.getValueAt(i, 2),
                    tableModel.getValueAt(i, 3), tableModel.getValueAt(i, 4)),
                    x, y); y += 18;
            }
            g2.setFont(new Font("Monospaced", Font.BOLD, 13));
            g2.drawString("TOTAL : " + lblTotalClient.getText().replace("Total : ", ""), x, y);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try { job.print(); setStatus("Impression envoyée."); }
            catch (Exception ex) { setStatus("Erreur : " + ex.getMessage()); }
        }
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }
}

