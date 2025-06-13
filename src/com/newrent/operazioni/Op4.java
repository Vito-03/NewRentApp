package com.newrent.operazioni;

import javax.swing.JTextArea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Op4 {

    public static void mostraStatisticheTrimestrali(Connection conn, JTextArea logArea) {
        String sql = """
            SELECT 
                V.concessionaria_nome AS Concessionaria,
                CONCAT('Q', QUARTER(F.data)) AS Trimestre,
                YEAR(F.data) AS Anno,
                SUM(CASE WHEN F.tipo_transazione = 'Vendita' THEN 1 ELSE 0 END) AS NumVendite,
                SUM(CASE WHEN F.tipo_transazione = 'Noleggio' THEN 1 ELSE 0 END) AS NumNoleggi,
                SUM(F.importo) AS TotaleIncasso
            FROM Fattura F
            JOIN Veicolo V ON F.numero_telaio = V.numero_telaio
            GROUP BY V.concessionaria_nome, YEAR(F.data), QUARTER(F.data)
            ORDER BY Anno DESC, Trimestre;
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            logArea.append("📊 Statistiche Trimestrali Concessionarie:\n");
            logArea.append("--------------------------------------------------\n");

            while (rs.next()) {
                String conc = rs.getString("Concessionaria");
                String trimestre = rs.getString("Trimestre");
                int anno = rs.getInt("Anno");
                int vendite = rs.getInt("NumVendite");
                int noleggi = rs.getInt("NumNoleggi");
                double totale = rs.getDouble("TotaleIncasso");

                logArea.append("🏢 " + conc + " | " + trimestre + " " + anno + "\n");
                logArea.append("   🚗 Vendite: " + vendite + "\n");
                logArea.append("   🔄 Noleggi: " + noleggi + "\n");
                logArea.append("   💰 Incasso Totale: €" + totale + "\n\n");
            }

        } catch (SQLException e) {
            logArea.append("❌ Errore OP4: " + e.getMessage() + "\n");
        }
    }
}
