package com.newrent.operazioni;

import javax.swing.JTextArea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Op7 {

    public static void mostraStatisticheVenditePerMarca(Connection conn, JTextArea logArea) {
        String sql = """
            SELECT 
                V.marca, 
                COUNT(F.id) AS numero_vendite,
                SUM(F.importo) AS totale_incassato
            FROM Fattura F
            JOIN Veicolo V ON F.numero_telaio = V.numero_telaio
            WHERE F.tipo_transazione = 'Vendita'
            GROUP BY V.marca
            ORDER BY totale_incassato DESC;
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            logArea.append("📊 Statistiche vendite per marca:\n");
            logArea.append("----------------------------------------\n");

            boolean found = false;
            while (rs.next()) {
                found = true;
                String marca = rs.getString("marca");
                int numeroVendite = rs.getInt("numero_vendite");
                double incasso = rs.getDouble("totale_incassato");

                logArea.append("🚗 Marca: " + marca + "\n");
                logArea.append("   Vendite: " + numeroVendite + "\n");
                logArea.append("   Totale incassato: €" + incasso + "\n\n");
            }

            if (!found) {
                logArea.append("❗ Nessuna vendita trovata.\n");
            }

        } catch (SQLException e) {
            logArea.append("❌ Errore OP7: " + e.getMessage() + "\n");
        }
    }
}
