package com.newrent;

import com.newrent.operazioni.Op1;
import com.newrent.operazioni.Op2;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

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
                        String tipo = (String) tipoBox.getSelectedItem();
                        String dataImm = dataImmField.getText();
                        String dataAcq = dataAcqField.getText();
                        String stato = (String) statoBox.getSelectedItem();
                        String targa = targaField.getText();
                        String concessionaria = concessionariaField.getText();

                        Op1.inserisciVeicolo(
                                databaseConnection,
                                numeroTelaio, marca, modello, anno, km, prezzo,
                                tipo, dataImm, dataAcq, stato, targa, concessionaria,
                                logTextArea
                        );
                    } catch (Exception ex) {
                        log("❌ Errore inserimento veicolo: " + ex.getMessage());
                    }
                });

                dialog.add(new JLabel());
                dialog.add(conferma);
                dialog.setVisible(true);
                return;
            }

            case 2 -> {
                JTextField idCliente = aggiungiCampo(dialog, "ID Cliente:");
                JTextField telaio = aggiungiCampo(dialog, "Numero Telaio:");
                JTextField data = aggiungiCampo(dialog, "Data Transazione (YYYY-MM-DD):");
                JTextField importo = aggiungiCampo(dialog, "Importo:");
                JTextField pagamento = aggiungiCampo(dialog, "Tipo Pagamento:");

                JComboBox<String> tipoTrans = new JComboBox<>(new String[]{"Vendita", "Noleggio"});
                dialog.add(new JLabel("Tipo Transazione:"));
                dialog.add(tipoTrans);

                JTextField durata = aggiungiCampo(dialog, "Durata (solo noleggio):");

                JButton conferma2 = new JButton("Conferma");
                conferma2.addActionListener(ev -> {
                    try {
                        String tipo = (String) tipoTrans.getSelectedItem();
                        Integer durataVal = durata.getText().isBlank() ? null : Integer.parseInt(durata.getText());

                        Op2.registraTransazione(
                                databaseConnection,
                                idCliente.getText(),
                                telaio.getText(),
                                LocalDate.parse(data.getText()),
                                Double.parseDouble(importo.getText()),
                                pagamento.getText(),
                                tipo,
                                durataVal,
                                logTextArea
                        );
                    } catch (Exception ex) {
                        log("❌ Errore registrazione transazione: " + ex.getMessage());
                    }
                });

                dialog.add(new JLabel());
                dialog.add(conferma2);
                dialog.setVisible(true);
                return;
            }

            case 3 -> {
                JTextField fornitoreField = aggiungiCampo(dialog, "Nome Fornitore:");

                JButton conferma = new JButton("Genera Report");
                conferma.addActionListener(ev -> {
                    try {
                        String nomeFornitore = fornitoreField.getText();
                        if (nomeFornitore.isBlank()) {
                            log("⚠️ Inserisci il nome del fornitore.");
                            return;
                        }

                        com.newrent.operazioni.Op3.stampaAccessoriPerFornitore(
                                databaseConnection,
                                nomeFornitore,
                                logTextArea
                        );
                        dialog.dispose();
                    } catch (Exception ex) {
                        log("❌ Errore OP3: " + ex.getMessage());
                    }
                });

                dialog.add(new JLabel());
                dialog.add(conferma);
                dialog.setVisible(true);
                return;
            }

            case 4 -> {
                JButton esegui = new JButton("Mostra Statistiche");
                esegui.addActionListener(ev -> {
                    try {
                        com.newrent.operazioni.Op4.mostraStatisticheTrimestrali(
                                databaseConnection,
                                logTextArea
                        );
                        dialog.dispose();
                    } catch (Exception ex) {
                        log("❌ Errore OP4: " + ex.getMessage());
                    }
                });

                dialog.add(new JLabel()); // Spazio vuoto layout
                dialog.add(esegui);
                dialog.setVisible(true);
                return;
            }

            case 5 -> {
                JButton esegui = new JButton("Mostra Veicoli");
                esegui.addActionListener(ev -> {
                    try {
                        com.newrent.operazioni.Op5.mostraVeicoliConAccessori(
                                databaseConnection,
                                logTextArea
                        );
                        dialog.dispose();
                    } catch (Exception ex) {
                        log("❌ Errore OP5: " + ex.getMessage());
                    }
                });

                dialog.add(new JLabel()); // spazio vuoto per layout
                dialog.add(esegui);
                dialog.setVisible(true);
                return;
            }

            case 6 -> {
                JTextField clienteField = aggiungiCampo(dialog, "ID Cliente:");

                JButton conferma = new JButton("Visualizza Fatture");
                conferma.addActionListener(ev -> {
                    try {
                        String idCliente = clienteField.getText();
                        if (idCliente.isBlank()) {
                            log("⚠️ Inserisci un ID cliente valido.");
                            return;
                        }

                        com.newrent.operazioni.Op6.mostraFattureCliente(
                                databaseConnection,
                                idCliente,
                                logTextArea
                        );
                        dialog.dispose();
                    } catch (Exception ex) {
                        log("❌ Errore OP6: " + ex.getMessage());
                    }
                });

                dialog.add(new JLabel()); // layout filler
                dialog.add(conferma);
                dialog.setVisible(true);
                return;
            }

            case 7 -> {
                JButton esegui = new JButton("Mostra Statistiche Vendite");
                esegui.addActionListener(ev -> {
                    try {
                        com.newrent.operazioni.Op7.mostraStatisticheVenditePerMarca(
                                databaseConnection,
                                logTextArea
                        );
                        dialog.dispose();
                    } catch (Exception ex) {
                        log("❌ Errore OP7: " + ex.getMessage());
                    }
                });

                dialog.add(new JLabel()); // layout filler
                dialog.add(esegui);
                dialog.setVisible(true);
                return;
            }

            case 8 -> {
                JTextField marcaField = aggiungiCampo(dialog, "Marca (opzionale):");

                JComboBox<String> tipoBox = new JComboBox<>(new String[]{"", "Nuovo", "Usato"});
                dialog.add(new JLabel("Tipo (opzionale):"));
                dialog.add(tipoBox);

                JComboBox<String> statoBox = new JComboBox<>(new String[]{"", "Disponibile", "Venduto", "Noleggiato"});
                dialog.add(new JLabel("Stato (opzionale):"));
                dialog.add(statoBox);

                JTextField prezzoMinField = aggiungiCampo(dialog, "Prezzo Min (opzionale):");
                JTextField prezzoMaxField = aggiungiCampo(dialog, "Prezzo Max (opzionale):");

                JButton conferma = new JButton("Cerca");
                conferma.addActionListener(ev -> {
                    try {
                        String marca = marcaField.getText().trim();
                        String tipo = tipoBox.getSelectedItem() != null ? tipoBox.getSelectedItem().toString() : "";
                        String stato = statoBox.getSelectedItem() != null ? statoBox.getSelectedItem().toString() : "";
                        Double prezzoMin = null;
                        Double prezzoMax = null;

                        if (!prezzoMinField.getText().isBlank())
                            prezzoMin = Double.parseDouble(prezzoMinField.getText().trim());
                        if (!prezzoMaxField.getText().isBlank())
                            prezzoMax = Double.parseDouble(prezzoMaxField.getText().trim());

                        com.newrent.operazioni.Op8.ricercaAvanzataVeicoli(
                                databaseConnection,
                                marca,
                                tipo,
                                stato,
                                prezzoMin,
                                prezzoMax,
                                logTextArea
                        );
                        dialog.dispose();

                    } catch (NumberFormatException nfe) {
                        log("⚠️ Inserisci valori numerici validi per i prezzi.");
                    } catch (Exception ex) {
                        log("❌ Errore OP8: " + ex.getMessage());
                    }
                });

                dialog.add(new JLabel()); // vuoto
                dialog.add(conferma);
                dialog.setVisible(true);
                return;
            }

            default -> log("🔧 Operazione non ancora implementata.");
        }
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
