package fr.nclsp.commands.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nclsp.Main;

import java.util.Objects;

public class Clearchat implements CommandExecutor {
    private final Main main;

    public Clearchat(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("ncls.chat.clearChat")) // check perm
        {
            for (int i = 0; i < 100; i++) {
                for (Player tempPlayer : Bukkit.getOnlinePlayers()) // To don't spam console & logs, we only clear
                                                                    // player's chat
                {
                    tempPlayer.sendMessage("");
                }
            } // Broadcast Final Message
            Bukkit.getServer().broadcastMessage(
                    Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&', '§')
                            + Objects.requireNonNull(main.getConfig().getString("messages.clearChat.clearBroadcast"))
                                    .replace('&', '§').replace("%sender%", sender.getName()));
        } else // No Perm
            sender.sendMessage(
                    Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&', '§') + Objects
                            .requireNonNull(main.getConfig().getString("messages.clearChat.noperm")).replace('&', '§'));
        return false;
    }
}