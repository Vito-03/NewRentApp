package com.newrent.operazioni;

import javax.swing.JTextArea;
import java.sql.*;

public class Op5 {

    public static void mostraVeicoliConAccessori(Connection conn, JTextArea logArea) {
        String sql = """
            SELECT 
                V.numero_telaio,
                V.marca,
                V.modello,
                COUNT(I.codice_accessorio) AS num_accessori
            FROM Veicolo V
            JOIN Include I ON V.numero_telaio = I.numero_telaio
            GROUP BY V.numero_telaio, V.marca, V.modello
            HAVING COUNT(I.codice_accessorio) >= 2
            ORDER BY num_accessori DESC;
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            logArea.append("🔧 Veicoli con almeno 2 accessori:\n");
            logArea.append("-----------------------------------------\n");

            boolean found = false;
            while (rs.next()) {
                found = true;
                String telaio = rs.getString("numero_telaio");
                String marca = rs.getString("marca");
                String modello = rs.getString("modello");
                int numAccessori = rs.getInt("num_accessori");

                logArea.append("📌 Telaio: " + telaio + "\n");
                logArea.append("   Marca: " + marca + ", Modello: " + modello + "\n");
                logArea.append("   🧩 Accessori: " + numAccessori + "\n\n");
            }

            if (!found) {
                logArea.append("❗ Nessun veicolo trovato con almeno 2 accessori.\n");
            }

        } catch (SQLException e) {
            logArea.append("❌ Errore OP5: " + e.getMessage() + "\n");
        }
    }
}
