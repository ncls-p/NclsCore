package fr.fazdix.nclscore.commands.moderation;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.fazdix.nclscore.Main;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class Unmute implements CommandExecutor {
    private final Main main;

    public Unmute(Main main) {
        this.main = main;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("ncls.moderation.unmute")) {
            if (args.length >= 1) { // If /unmute {target}
                String targetName = args[0];
                UUID targetUUID;
                if (Bukkit.getPlayer(targetName) != null) // If target is online (only here to prevents bugs)
                    targetUUID = Objects.requireNonNull(Bukkit.getPlayer(targetName)).getUniqueId();
                else // if target is offline
                    targetUUID = Bukkit.getOfflinePlayer(targetName).getUniqueId();
                try {
                    if (main.dbModeration.isMuted(main.statementMod, targetUUID)) // check if the player is banned
                    {
                        main.dbModeration.mutedActual(main.statementMod, targetUUID);
                        for (Player tempPlayer : Bukkit.getOnlinePlayers()) {
                            if (tempPlayer.hasPermission("ncls.moderation.unmute.see"))
                                tempPlayer.sendMessage(
                                        Objects.requireNonNull(main.getConfig().getString("messages.unmute.see"))
                                                .replace('&', '§').replace("%sender%", sender.getName())
                                                .replace("%target%", targetName));
                        }
                    } else // Player isn't muted
                        sender.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.unmute.notBan"))
                                .replace('&', '§'));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else // If just /unmute
                sender.sendMessage(
                        Objects.requireNonNull(main.getConfig().getString("messages.unmute.noArgs")).replace('&', '§'));
        } else // No perm
            sender.sendMessage(
                    Objects.requireNonNull(main.getConfig().getString("messages.unmute.noperm")).replace('&', '§'));
        return false;
    }
}