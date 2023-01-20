package fr.fazdix.nclscore.database;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnect {
    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final String databaseName;

    public DatabaseConnect(String host, String port, String username, String password, String databaseName) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
    }

    public Statement Open() {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.databaseName, this.username,
                    this.password); // connection
            Bukkit.getConsoleSender()
                    .sendMessage("§5§lNCLS§6§lCore§a Connection to the moderation database successful !");
            return conn.createStatement();
        } catch (SQLException throwables) { // Can't connect to de database
            Bukkit.getConsoleSender().sendMessage(
                    "§5§lNCLS§6§lCore §c§lError while connecting to the database Moderation, please check your configuration file before asking for help, looking at the error bellow might help you:\n §6"
                            + throwables.getMessage());
        }
        return null;
    }
}