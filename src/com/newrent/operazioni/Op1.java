package com.newrent.operazioni;

import javax.swing.JTextArea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Op1 {

    public static boolean veicoloEsistente(Connection conn, String numeroTelaio) throws SQLException {
        String query = "SELECT 1 FROM Veicolo WHERE numero_telaio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, numeroTelaio);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean concessionariaEsistente(Connection conn, String nome) throws SQLException {
        String query = "SELECT 1 FROM Concessionaria WHERE nome = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static void inserisciVeicolo(
            Connection conn,
            String numeroTelaio,
            String marca,
            String modello,
            int anno,
            int chilometraggio,
            double prezzo,
            String tipo,
            String dataImmatricolazione,
            String dataAcquisto,
            String stato,
            String targa,
            String concessionaria,
            JTextArea logArea
    ) throws SQLException {

        if (veicoloEsistente(conn, numeroTelaio)) {
            logArea.append("⚠️ Il veicolo con numero telaio " + numeroTelaio + " esiste già.\n");
            return;
        }

        if (!concessionariaEsistente(conn, concessionaria)) {
            logArea.append("❌ La concessionaria '" + concessionaria + "' non esiste nel database.\n");
            return;
        }

        String sql = """
            INSERT INTO Veicolo (
                numero_telaio, marca, modello, anno, chilometraggio, prezzo, tipo,
                data_immatricolazione, data_acquisto, stato, targa, concessionaria_nome
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroTelaio);
            stmt.setString(2, marca);
            stmt.setString(3, modello);
            stmt.setInt(4, anno);
            stmt.setInt(5, chilometraggio);
            stmt.setDouble(6, prezzo);
            stmt.setString(7, tipo);
            stmt.setString(8, dataImmatricolazione);
            stmt.setString(9, dataAcquisto);
            stmt.setString(10, stato);
            stmt.setString(11, targa);
            stmt.setString(12, concessionaria);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logArea.append("✅ Veicolo inserito correttamente: " + numeroTelaio + "\n");
            } else {
                logArea.append("❌ Inserimento fallito.\n");
            }
        }
    }
}
