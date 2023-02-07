package fr.nclsp;

import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.nclsp.database.DBModeration;
import fr.nclsp.utils.loading.PluginInitializer;

public class Main extends JavaPlugin {
    public DBModeration dbModeration;
    public Statement statementMod;
    // BANNED PLAYER & UNBAN DATETIME
    public HashMap<UUID, LocalDateTime> hMBannedDatetimeunban = new HashMap<>();
    // Muted PLAYER & UNBAN DATETIME
    public HashMap<UUID, LocalDateTime> hMMutedDatetimeunban = new HashMap<>();
    // TPA HASHMAP ASKED AS KEY ASKER AS VALUE
    public HashMap<Player, Player> hMTpAskedAsker = new HashMap<>();
    // RTP player waiting for rtp
    public HashMap<Player, Boolean> hMRtp = new HashMap<>();
    public boolean blockChat = false;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getConsoleSender().sendMessage("§5§lNCLS§6§lCore§a : Enabled"); 
        PluginInitializer pluginInitializer = new PluginInitializer(this);
        pluginInitializer.initializeCommands();
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§5§lNCLS§6§lCore§c : Disabled");
    }
}