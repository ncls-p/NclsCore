package fr.nclsp.commands.moderation;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nclsp.Main;

import java.sql.SQLException;
import java.util.Objects;

public class Kick implements CommandExecutor {
    private final Main main;

    public Kick(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&', '§');
        if (sender.hasPermission("nclsp.moderation.kick")) { // Check Perm

            if (args.length >= 1) { // Check Args
                String targetName = args[0];
                if (Bukkit.getServer().getPlayerExact(targetName) != null) // Check if target is online
                {
                    Player target = Bukkit.getServer().getPlayerExact(targetName);
                    assert target != null;
                    if (!(target.hasPermission("nclsp.moderation.kick.bypass"))) { // Check if the target bypass
                        String reason = Objects
                                .requireNonNull(main.getConfig().getString("messages.kick.defaultReason"))
                                .replace('&', '§');
                        if (args.length > 1) { // Reason specified
                            StringBuilder sb = new StringBuilder();
                            for (int i = 1; i < args.length; i++) {
                                sb.append(args[i]).append(" ");
                            }
                            reason = sb.toString().replace('&', '§'); // If the reason is specified we replace de
                                                                      // default reason
                        }
                        // Kick the player for the reason
                        target.kickPlayer(Objects
                                .requireNonNull(main.getConfig().getString("messages.kick.kickMessage"))
                                .replace('&', '§').replace("%staff%", sender.getName()).replace("%reason%", reason));
                        for (Player tempPlayer : Bukkit.getServer().getOnlinePlayers()) {
                            if (tempPlayer.hasPermission("nclsp.moderation.kick.see"))
                                tempPlayer.sendMessage(
                                        prefix + Objects.requireNonNull(main.getConfig().getString("messages.kick.see"))
                                                .replace('&', '§').replace("%sender%", sender.getName())
                                                .replace("%target%", targetName).replace("%reason%", reason));
                        }
                        String staffUuid = "";
                        if (sender instanceof Player) { // set the staff staffUuid if the sender is a player
                            Player p = (Player) sender;
                            staffUuid = p.getUniqueId().toString();
                        }
                        try {
                            main.dbModeration.insertKickDatabase(main.statementMod, targetName,
                                    target.getUniqueId().toString(), reason, sender.getName(), staffUuid);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    } else // bypass
                        sender.sendMessage(prefix + Objects
                                .requireNonNull(main.getConfig().getString("messages.kick.bypass")).replace('&', '§'));
                } else // TARGET NOT ONLINE
                    sender.sendMessage(prefix + Objects
                            .requireNonNull(main.getConfig().getString("messages.kick.notOnline")).replace('&', '§'));
            } else // NO ARGS
                sender.sendMessage(prefix
                        + Objects.requireNonNull(main.getConfig().getString("messages.kick.noArgs")).replace('&', '§'));
        } else // NO PERM
            sender.sendMessage(prefix
                    + Objects.requireNonNull(main.getConfig().getString("messages.kick.noperm")).replace('&', '§'));
        return false;
    }
}