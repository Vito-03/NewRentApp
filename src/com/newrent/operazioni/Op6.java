package com.newrent.operazioni;

import javax.swing.JTextArea;
import java.sql.*;

public class Op6 {

    public static void mostraFattureCliente(Connection conn, String idCliente, JTextArea logArea) {
        String sql = """
            SELECT 
                F.id, 
                F.numero_telaio,
                F.data,
                F.importo,
                F.tipo_pagamento,
                F.tipo_transazione,
                F.durata
            FROM Fattura F
            WHERE F.id_cliente = ?
            ORDER BY F.data DESC;
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                logArea.append("📄 Fatture per cliente: " + idCliente + "\n");
                logArea.append("--------------------------------------------------\n");

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    int id = rs.getInt("id");
                    String telaio = rs.getString("numero_telaio");
                    Date data = rs.getDate("data");
                    double importo = rs.getDouble("importo");
                    String pagamento = rs.getString("tipo_pagamento");
                    String transazione = rs.getString("tipo_transazione");
                    int durata = rs.getInt("durata");

                    logArea.append("🧾 ID Fattura: " + id + "\n");
                    logArea.append("   Telaio: " + telaio + "\n");
                    logArea.append("   Data: " + data + "\n");
                    logArea.append("   Importo: €" + importo + "\n");
                    logArea.append("   Pagamento: " + pagamento + "\n");
                    logArea.append("   Transazione: " + transazione + "\n");

                    if ("Noleggio".equalsIgnoreCase(transazione)) {
                        logArea.append("   ⏱ Durata noleggio: " + durata + " giorni\n");
                    }

                    logArea.append("\n");
                }

                if (!found) {
                    logArea.append("❗ Nessuna fattura trovata per questo cliente.\n");
                }
            }

        } catch (SQLException e) {
            logArea.append("❌ Errore OP6: " + e.getMessage() + "\n");
        }
    }
}
