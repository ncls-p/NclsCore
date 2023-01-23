package fr.nclsp.commands.environement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nclsp.Main;

public class Weathers implements CommandExecutor {

    private final Main main;

    public Weathers(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("ncls.environement.weathers")) {
                if (label.equalsIgnoreCase("sun")) {
                    p.getWorld().setStorm(false);
                    p.sendMessage(main.getConfig().getString("messages.weathers.setStun"));
                } else if (label.equalsIgnoreCase("rain")) {
                    p.getWorld().setStorm(true);
                    p.sendMessage(main.getConfig().getString("messages.weathers.setRain"));
                }
            } else
                sender.sendMessage(main.getConfig().getString("messages.weathers.noPerm"));

        } else
            sender.sendMessage(main.getConfig().getString("messages.weathers.noPlayer"));
        return false;
    }
}