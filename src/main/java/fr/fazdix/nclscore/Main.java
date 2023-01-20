package fr.fazdix.nclscore;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.fazdix.nclscore.commands.chat.BlockChat;
import fr.fazdix.nclscore.commands.chat.Broadcast;
import fr.fazdix.nclscore.commands.chat.Clearchat;
import fr.fazdix.nclscore.commands.chat.Msg;
import fr.fazdix.nclscore.commands.environement.Times;
import fr.fazdix.nclscore.commands.environement.Weathers;
import fr.fazdix.nclscore.commands.moderation.Ban;
import fr.fazdix.nclscore.commands.moderation.Kick;
import fr.fazdix.nclscore.commands.moderation.Mute;
import fr.fazdix.nclscore.commands.moderation.Unban;
import fr.fazdix.nclscore.commands.moderation.Unmute;
import fr.fazdix.nclscore.commands.teleport.Rtp;
import fr.fazdix.nclscore.commands.teleport.TeleportRequest;
import fr.fazdix.nclscore.database.DBModeration;
import fr.fazdix.nclscore.events.chat.OnChatWhithBlockChat;
import fr.fazdix.nclscore.events.moderation.OnJoinModeration;
import fr.fazdix.nclscore.events.moderation.OnLeaveModeration;
import fr.fazdix.nclscore.events.moderation.PlayerChatModeration;

public class Main extends JavaPlugin {
    public DBModeration dbModeration;
    public Statement statementMod;
    public HashMap<UUID, LocalDateTime> hMBannedDatetimeunban = new HashMap<>(); // BANNED PLAYER & UNBAN DATETIME
    public HashMap<UUID, LocalDateTime> hMMutedDatetimeunban = new HashMap<>(); // Muted PLAYER & UNBAN DATETIME
    public HashMap<Player, Player> hMTpAskedAsker = new HashMap<>(); // TPA HASHMAP ASKED AS KEY ASKER AS VALUE
    public HashMap<Player, Boolean> hMRtp = new HashMap<>(); // RTP player waiting for rtp
    public boolean blockChat = false;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getConsoleSender().sendMessage("§5§lNCLS§6§lCore§a : Enabled");
        if (getConfig().getBoolean("config.activation.msg")) // /MSG COMMAND
            Objects.requireNonNull(getCommand("msg")).setExecutor(new Msg(this));
        if (getConfig().getBoolean("config.activation.clearchat")) // /CLEARCHAT COMMAND
            Objects.requireNonNull(getCommand("clearchat")).setExecutor(new Clearchat(this));
        if (getConfig().getBoolean("config.activation.blockchat")) // BLOCKCHAT COMMAND
        {
            getCommand("blockchat").setExecutor(new BlockChat(this));
            getServer().getPluginManager().registerEvents(new OnChatWhithBlockChat(this), this);
        }
        if (getConfig().getBoolean("config.activation.broadcast")) // /BROADCAST COMMAND
            Objects.requireNonNull(getCommand("broadcast")).setExecutor(new Broadcast(this));
        if (getConfig().getBoolean("config.activation.tpa")) {// /TPA /TPYES /TPNO COMMAND
            Objects.requireNonNull(getCommand("tpa")).setExecutor(new TeleportRequest(this));
            Objects.requireNonNull(getCommand("tpyes")).setExecutor(new TeleportRequest(this));
            Objects.requireNonNull(getCommand("tpno")).setExecutor(new TeleportRequest(this));
        }
        if (getConfig().getBoolean("config.activation.time")) {
            Objects.requireNonNull(getCommand("day")).setExecutor(new Times(this));
            Objects.requireNonNull(getCommand("night")).setExecutor(new Times(this));
        }
        if (getConfig().getBoolean("config.activation.weather")) {
            Objects.requireNonNull(getCommand("sun")).setExecutor(new Weathers(this));
            Objects.requireNonNull(getCommand("rain")).setExecutor(new Weathers(this));
        }

        if (getConfig().getBoolean("config.activation.moderation")) { // MODERATION TOOLS
            String host = getConfig().getString("config.parameters.moderation.database.host"); // GET ALL DATABASE
                                                                                               // INFORMATIONS
            String port = getConfig().getString("config.parameters.moderation.database.port");
            String username = getConfig().getString("config.parameters.moderation.database.username");
            String password = getConfig().getString("config.parameters.moderation.database.password");
            String databaseName = getConfig().getString("config.parameters.moderation.database.databaseName");
            String tablePrefix = getConfig().getString("config.parameters.moderation.database.tablePrefix");
            dbModeration = new DBModeration(host, port, username, password, databaseName, tablePrefix); // INITIALIZE
                                                                                                        // CONNECTION
            statementMod = dbModeration.Open(); // OPEN CONNECTION to get statement
            try { // Create Database if she doesn't exist
                dbModeration.createDatabase(statementMod);
            } catch (SQLException throwables) {
                Bukkit.getConsoleSender().sendMessage(
                        "§5§lNCLS§6§lCore §c§lError while creating the database Moderation, please check your configuration file before asking for help, looking at the error bellow might help you:\n §6"
                                + throwables.getMessage());
            }
            Objects.requireNonNull(getCommand("kick")).setExecutor(new Kick(this)); // /kick command
            getServer().getConsoleSender()
                    .sendMessage("§6Adding a default kick into the database if he's not already added ..."); // Add
                                                                                                             // default
                                                                                                             // ban
            try {
                dbModeration.defaulKick(statementMod);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            getServer().getConsoleSender().sendMessage("§6Done !");
            /*
             * BAN COMMAND
             */
            Objects.requireNonNull(getCommand("ban")).setExecutor(new Ban(this)); // /BAN COMMAND
            getServer().getPluginManager().registerEvents(new OnJoinModeration(this), this); // KICK BANNED PLAYERS ON
                                                                                             // JOIN
            getServer().getPluginManager().registerEvents(new OnLeaveModeration(this), this); // Disallow leave message
                                                                                              // if the player is banned
            getServer().getConsoleSender()
                    .sendMessage("§6Getting all banned players from the database to prevent lags"); // ADD TO THE
                                                                                                    // HASHMAP UUID
                                                                                                    // BANNED AND END
                                                                                                    // BAN DATE
            ArrayList<UUID> allUUIDBan = new ArrayList<UUID>();
            LocalDateTime tempTimeBan = null;
            try {
                allUUIDBan = dbModeration.getAllUUIDBan(statementMod);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            for (UUID uuid : allUUIDBan) {
                try {
                    tempTimeBan = dbModeration.getEndBan(statementMod, uuid.toString());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                this.hMBannedDatetimeunban.put(uuid, tempTimeBan);
            }
            getServer().getConsoleSender().sendMessage("§6Done !"); // ADD TO THE HASHMAP UUID BANNED AND END BAN DATE
            getServer().getConsoleSender()
                    .sendMessage("§6Adding a default ban into the database if he's not already added ..."); // Add
                                                                                                            // default
                                                                                                            // ban
            try {
                dbModeration.defaultBan(statementMod);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            getServer().getConsoleSender().sendMessage("§6Done !");

            /*
             * MUTE COMMAND
             */
            Objects.requireNonNull(getCommand("mute")).setExecutor(new Mute(this)); // /MUTE COMMAND
            getServer().getPluginManager().registerEvents(new PlayerChatModeration(this), this); // KICK BANNED PLAYERS
                                                                                                 // ON JOIN
            getServer().getConsoleSender().sendMessage("§6Getting all muted players from the database to prevent lags");// ADD
                                                                                                                        // TO
                                                                                                                        // THE
                                                                                                                        // HASHMAP
                                                                                                                        // UUID
                                                                                                                        // BANNED
                                                                                                                        // AND
                                                                                                                        // END
                                                                                                                        // BAN
                                                                                                                        // DATE
            ArrayList<UUID> allUUIDMute = new ArrayList<UUID>();
            LocalDateTime tempTimeMute = null;
            try {
                allUUIDMute = dbModeration.getAllUUIDMute(statementMod);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            for (UUID uuid : allUUIDMute) {
                try {
                    tempTimeMute = dbModeration.getEndMute(statementMod, uuid.toString());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                this.hMMutedDatetimeunban.put(uuid, tempTimeMute);
            }
            getServer().getConsoleSender().sendMessage("§6Done !"); // ADD TO THE HASHMAP UUID BANNED AND END BAN DATE
            getServer().getConsoleSender()
                    .sendMessage("§6Adding a default mute into the database if he's not already added ..."); // Add
                                                                                                             // default
                                                                                                             // ban
            try {
                dbModeration.defaulMute(statementMod);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            getServer().getConsoleSender().sendMessage("§6Done !");
            // UNBAN
            Objects.requireNonNull(getCommand("unban")).setExecutor(new Unban(this));
            // unmute
            Objects.requireNonNull(getCommand("unmute")).setExecutor(new Unmute(this));

        }

        if (getConfig().getBoolean("config.activation.rtp")) {
            Objects.requireNonNull(getCommand("rtp")).setExecutor(new Rtp(this));
        }

    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§5§lNCLS§6§lCore§c : Disabled");
    }

}