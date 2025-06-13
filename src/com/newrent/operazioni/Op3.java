package com.newrent.operazioni;

import javax.swing.JTextArea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Op3 {

    public static void stampaAccessoriPerFornitore(Connection conn, String nomeFornitore, JTextArea logArea) {
        String sql = """
            SELECT A.codice, A.nome, A.categoria, A.costo, COUNT(I.numero_telaio) AS num_veicoli
            FROM Accessorio A
            LEFT JOIN Include I ON A.codice = I.codice_accessorio
            WHERE A.fornitore_nome = ?
            GROUP BY A.codice, A.nome, A.categoria, A.costo
            ORDER BY A.nome;
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeFornitore);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean trovato = false;
                logArea.append("📦 Accessori forniti da '" + nomeFornitore + "':\n");
                logArea.append("---------------------------------------------------\n");
                while (rs.next()) {
                    trovato = true;
                    String codice = rs.getString("codice");
                    String nome = rs.getString("nome");
                    String categoria = rs.getString("categoria");
                    double costo = rs.getDouble("costo");
                    int veicoli = rs.getInt("num_veicoli");

                    logArea.append("🔧 " + nome + " (" + codice + ")\n");
                    logArea.append("   Categoria: " + categoria + "\n");
                    logArea.append("   Costo: €" + costo + "\n");
                    logArea.append("   Montato su " + veicoli + " veicoli\n\n");
                }

                if (!trovato) {
                    logArea.append("⚠️ Nessun accessorio trovato per questo fornitore.\n");
                }
            }
        } catch (SQLException e) {
            logArea.append("❌ Errore query OP3: " + e.getMessage() + "\n");
        }
    }
}
