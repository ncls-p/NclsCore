package fr.fazdix.nclscore.commands.environement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.fazdix.nclscore.Main;

public class Times implements CommandExecutor {
    private final Main main;

    public Times(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("ncls.environement.times")) {
                if (label.equalsIgnoreCase("night")) {
                    p.getWorld().setTime(13000);
                    p.sendMessage(main.getConfig().getString("messages.times.setNight").replace('&', '§'));
                } else if (label.equalsIgnoreCase("day")) {
                    p.getWorld().setTime(0);
                    p.sendMessage(main.getConfig().getString("messages.times.setDay").replace('&', '§'));
                }
            } else
                p.sendMessage(main.getConfig().getString("messages.times.noPerm").replace('&', '§'));

        } else
            sender.sendMessage(main.getConfig().getString("messages.times.noPlayer").replace('&', '§'));

        return false;
    }
}
