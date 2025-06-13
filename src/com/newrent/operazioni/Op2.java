package com.newrent.operazioni;

import javax.swing.JTextArea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class Op2 {

    public static void registraTransazione(
            Connection conn,
            String idCliente,
            String numeroTelaio,
            LocalDate data,
            double importo,
            String tipoPagamento,
            String tipoTransazione,
            Integer durata, // solo per noleggio, può essere null
            JTextArea logArea
    ) {
        try {
            // Inserisci nella tabella Acquista
            String acquistoSQL = "INSERT INTO Acquista (id_cliente, numero_telaio) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(acquistoSQL)) {
                stmt.setString(1, idCliente);
                stmt.setString(2, numeroTelaio);
                stmt.executeUpdate();
            }

            // Inserisci nella tabella Fattura
            String fatturaSQL = """
                INSERT INTO Fattura (
                    id_cliente, numero_telaio, data, importo,
                    tipo_pagamento, tipo_transazione, durata
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
            try (PreparedStatement stmt = conn.prepareStatement(fatturaSQL)) {
                stmt.setString(1, idCliente);
                stmt.setString(2, numeroTelaio);
                stmt.setDate(3, java.sql.Date.valueOf(data));
                stmt.setDouble(4, importo);
                stmt.setString(5, tipoPagamento);
                stmt.setString(6, tipoTransazione);
                if ("Noleggio".equalsIgnoreCase(tipoTransazione)) {
                    stmt.setInt(7, durata != null ? durata : 0);
                } else {
                    stmt.setNull(7, java.sql.Types.INTEGER);
                }

                stmt.executeUpdate();
            }

            logArea.append("✅ Transazione registrata con successo per cliente " + idCliente + "\n");

        } catch (SQLException e) {
            logArea.append("❌ Errore registrazione transazione: " + e.getMessage() + "\n");
        }
    }
}
