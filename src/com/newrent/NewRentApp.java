package com.newrent;

import com.newrent.operazioni.Op1;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NewRentApp {

    private JFrame frame;
    private JTextArea logTextArea;
    private Connection databaseConnection;
    private int selectedOperation;

    public NewRentApp() {
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        connectToDatabase();
        initializeUI();
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/NewRent";
        String user = "root";
        String password = "vito.mysql";

        try {
            databaseConnection = DriverManager.getConnection(url, user, password);
            log("✅ Connessione al database riuscita.");
        } catch (SQLException e) {
            log("❌ Errore connessione DB: " + e.getMessage());
        }
    }

    private void initializeUI() {
        frame = new JFrame("🚗 NewRentApp");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        String[] operazioni = {
                "1. Inserisci veicolo",
                "2. Registra acquisto/noleggio",
                "3. Report accessori per fornitore",
                "4. Statistiche trimestrali concessionarie",
                "5. Veicoli con almeno 2 accessori",
                "6. Visualizza fatture cliente",
                "7. Statistiche vendite per marca",
                "8. Ricerca avanzata veicoli"
        };

        for (int i = 0; i < operazioni.length; i++) {
            JButton button = new JButton(operazioni[i]);
            int op = i + 1;
            button.addActionListener(e -> {
                selectedOperation = op;
                openOperationDialog(op, operazioni[op - 1]);
            });
            leftPanel.add(button);
        }

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void openOperationDialog(int opNumber, String opName) {
        JDialog dialog = new JDialog(frame, opName, true);
        dialog.setLayout(new GridLayout(0, 2));
        dialog.setSize(400, 400);

        switch (opNumber) {
            case 1 -> {
                JTextField telaioField = aggiungiCampo(dialog, "Numero Telaio:");
                JTextField marcaField = aggiungiCampo(dialog, "Marca:");
                JTextField modelloField = aggiungiCampo(dialog, "Modello:");
                JTextField annoField = aggiungiCampo(dialog, "Anno:");
                JTextField kmField = aggiungiCampo(dialog, "Chilometraggio:");
                JTextField prezzoField = aggiungiCampo(dialog, "Prezzo:");

                JComboBox<String> tipoBox = new JComboBox<>(new String[]{"Nuovo", "Usato"});
                dialog.add(new JLabel("Tipo:"));
                dialog.add(tipoBox);

                JTextField dataImmField = aggiungiCampo(dialog, "Data Immatricolazione (YYYY-MM-DD):");
                JTextField dataAcqField = aggiungiCampo(dialog, "Data Acquisto (YYYY-MM-DD):");

                JComboBox<String> statoBox = new JComboBox<>(new String[]{"Disponibile", "Venduto", "Noleggiato"});
                dialog.add(new JLabel("Stato:"));
                dialog.add(statoBox);

                JTextField targaField = aggiungiCampo(dialog, "Targa:");
                JTextField concessionariaField = aggiungiCampo(dialog, "Nome Concessionaria:");

                JButton conferma = new JButton("Conferma");
                conferma.addActionListener(ev -> {
                    try {
                        String numeroTelaio = telaioField.getText();
                        String marca = marcaField.getText();
                        String modello = modelloField.getText();
                        int anno = Integer.parseInt(annoField.getText());
                        int km = Integer.parseInt(kmField.getText());
                        double prezzo = Double.parseDouble(prezzoField.getText());
                        String tipo = tipoBox.getSelectedItem().toString();
                        String dataImm = dataImmField.getText();
                        String dataAcq = dataAcqField.getText();
                        String stato = statoBox.getSelectedItem().toString();
                        String targa = targaField.getText();
                        String concessionaria = concessionariaField.getText();

                        Op1.inserisciVeicolo(
                                databaseConnection,
                                numeroTelaio, marca, modello, anno, km, prezzo,
                                tipo, dataImm, dataAcq, stato, targa, concessionaria,
                                logTextArea
                        );
                    } catch (Exception ex) {
                        log("❌ Errore inserimento: " + ex.getMessage());
                    }
                });

                dialog.add(new JLabel());
                dialog.add(conferma);
                dialog.setVisible(true);
                return;
            }

            case 2 -> log("🔧 Operazione 2: acquisto/noleggio");
            case 3 -> log("📊 Operazione 3: report accessori per fornitore");
            case 4 -> log("📈 Operazione 4: statistiche trimestrali");
            case 5 -> log("📦 Operazione 5: veicoli con ≥2 accessori");
            case 6 -> log("📄 Operazione 6: fatture cliente");
            case 7 -> log("📊 Operazione 7: vendite per marca");
            case 8 -> log("🔍 Operazione 8: ricerca veicoli avanzata");
        }

        dialog.add(new JLabel("💡 Questa finestra sarà personalizzata per l'operazione."));
        dialog.setVisible(true);
    }

    private JTextField aggiungiCampo(JDialog dialog, String nomeCampo) {
        JLabel label = new JLabel(nomeCampo);
        JTextField textField = new JTextField();
        dialog.add(label);
        dialog.add(textField);
        return textField;
    }

    private void log(String message) {
        logTextArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new NewRentApp();
                System.out.println("✅ NewRentApp creata correttamente");
            } catch (Exception e) {
                System.out.println("❌ Errore durante l'avvio: " + e.getMessage());
            }
        });
    }
}
