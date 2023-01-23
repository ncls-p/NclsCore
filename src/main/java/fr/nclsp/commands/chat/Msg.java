package fr.nclsp.commands.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nclsp.Main;

import java.util.Objects;

public class Msg implements CommandExecutor {
    private final Main main;

    public Msg(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("ncls.chat.msg")) // Check permission to /msg
        {
            if (!(args.length <= 1)) {
                String targetName = args[0];
                if (!(Bukkit.getServer().getPlayerExact(targetName) == null)) // Player is online
                {
                    Player target = Bukkit.getServer().getPlayerExact(targetName);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String sentence = sb.toString();
                    sender.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.msg.sender"))
                            .replace('&', '§').replace("%receiver%", targetName).replace("%message%", sentence));
                    assert target != null;
                    target.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.msg.receiver"))
                            .replace('&', '§').replace("%sender%", sender.getName()).replace("%message%", sentence));
                } else // PLAYER NOT ONLINE
                    sender.sendMessage(
                            Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&', '§')
                                    + Objects.requireNonNull(main.getConfig().getString("messages.msg.notOnline"))
                                            .replace('&', '§'));
            } else // NO ARGS
                sender.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&',
                        '§')
                        + Objects.requireNonNull(main.getConfig().getString("messages.msg.noArgs")).replace('&', '§'));
        } else // NO PERM
            sender.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&', '§')
                    + Objects.requireNonNull(main.getConfig().getString("messages.msg.noperm")).replace('&', '§'));
        return false;
    }
}