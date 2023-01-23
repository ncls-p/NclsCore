package fr.nclsp.commands.moderation;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nclsp.Main;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class Unban implements CommandExecutor {
    private final Main main;

    public Unban(Main main) {
        this.main = main;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("ncls.moderation.unban")) {
            if (args.length >= 1) { // If /unban {target}
                String targetName = args[0];
                UUID targetUUID;
                if (Bukkit.getPlayer(targetName) != null) // If target is online (only here to prevents bugs)
                    targetUUID = Objects.requireNonNull(Bukkit.getPlayer(targetName)).getUniqueId();
                else // if target is offline
                    targetUUID = Bukkit.getOfflinePlayer(targetName).getUniqueId();

                try {
                    if (main.dbModeration.isBanned(main.statementMod, targetUUID)) // check if the player is banned
                    {
                        main.dbModeration.unbanActual(main.statementMod, targetUUID);
                        for (Player tempPlayer : Bukkit.getOnlinePlayers()) {
                            if (tempPlayer.hasPermission("ncls.moderation.unban.see"))
                                tempPlayer.sendMessage(
                                        Objects.requireNonNull(main.getConfig().getString("messages.unban.see"))
                                                .replace('&', '§').replace("%sender%", sender.getName())
                                                .replace("%target%", targetName));
                        }
                    } else // Player isn't banned
                        sender.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.unban.notBan"))
                                .replace('&', '§'));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else // If just /unban
                sender.sendMessage(
                        Objects.requireNonNull(main.getConfig().getString("messages.unban.noArgs")).replace('&', '§'));
        } else { // No perm
            sender.sendMessage(
                    Objects.requireNonNull(main.getConfig().getString("messages.unban.noperm")).replace('&', '§'));
        }

        return false;
    }
}