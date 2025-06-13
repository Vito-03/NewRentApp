package com.newrent.operazioni;

import javax.swing.JTextArea;
import java.sql.*;

public class Op8 {

    public static void ricercaAvanzataVeicoli(
            Connection conn,
            String marca,
            String tipo,
            String stato,
            Double prezzoMin,
            Double prezzoMax,
            JTextArea logArea
    ) {
        StringBuilder query = new StringBuilder("SELECT * FROM Veicolo WHERE 1=1");
        if (!marca.isEmpty()) query.append(" AND marca = ?");
        if (!tipo.isEmpty()) query.append(" AND tipo = ?");
        if (!stato.isEmpty()) query.append(" AND stato = ?");
        if (prezzoMin != null) query.append(" AND prezzo >= ?");
        if (prezzoMax != null) query.append(" AND prezzo <= ?");

        try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (!marca.isEmpty()) stmt.setString(paramIndex++, marca);
            if (!tipo.isEmpty()) stmt.setString(paramIndex++, tipo);
            if (!stato.isEmpty()) stmt.setString(paramIndex++, stato);
            if (prezzoMin != null) stmt.setDouble(paramIndex++, prezzoMin);
            if (prezzoMax != null) stmt.setDouble(paramIndex++, prezzoMax);

            try (ResultSet rs = stmt.executeQuery()) {
                logArea.append("🔍 Risultati ricerca avanzata:\n");
                logArea.append("----------------------------------------\n");

                boolean trovato = false;
                while (rs.next()) {
                    trovato = true;
                    logArea.append("🚗 Telaio: " + rs.getString("numero_telaio") + "\n");
                    logArea.append("   Marca: " + rs.getString("marca") + ", Modello: " + rs.getString("modello") + "\n");
                    logArea.append("   Tipo: " + rs.getString("tipo") + ", Stato: " + rs.getString("stato") + "\n");
                    logArea.append("   Prezzo: €" + rs.getDouble("prezzo") + "\n\n");
                }

                if (!trovato) {
                    logArea.append("❗ Nessun veicolo corrispondente ai criteri.\n");
                }
            }

        } catch (SQLException e) {
            logArea.append("❌ Errore OP8: " + e.getMessage() + "\n");
        }
    }
}
