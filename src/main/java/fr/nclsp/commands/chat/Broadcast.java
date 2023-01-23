package fr.nclsp.commands.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.nclsp.Main;

import java.util.Objects;

@SuppressWarnings("NullableProblems")
public class Broadcast implements CommandExecutor {
    private final Main main;

    public Broadcast(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&', '§');
        if (sender.hasPermission("nclsp.chat.broadcast")) {
            if (args.length != 0) {
                StringBuilder sb = new StringBuilder();
                for (String arg : args) {
                    sb.append(arg.replace('&', '§')).append(" ");
                }
                String sentence = sb.toString();
                Bukkit.broadcastMessage(
                        Objects.requireNonNull(main.getConfig().getString("messages.broadcast.finalMessage"))
                                .replace('&', '§').replace("%message%", sentence).replace("%prefix%", prefix));
            } else // NO ARGS
                sender.sendMessage(prefix + Objects
                        .requireNonNull(main.getConfig().getString("messages.broadcast.noArgs")).replace('&', '§'));
        } else // NO PERM
            sender.sendMessage(prefix + Objects.requireNonNull(main.getConfig().getString("messages.broadcast.noperm"))
                    .replace('&', '§'));
        return false;
    }
}