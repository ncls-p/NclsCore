package fr.fazdix.nclscore.commands.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.fazdix.nclscore.Main;

public class BlockChat implements CommandExecutor {
    private Main main;

    public BlockChat(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("ncls.chat.blockChat")) {
            if (!main.blockChat) {
                main.blockChat = true;
                Bukkit.getServer()
                        .broadcastMessage(main.getConfig().getString("messages.blockChat.blockBroadcast.enable")
                                .replace("&", "§").replace("%prefix%", main.getConfig().getString("messages.prefix")));
            } else {
                main.blockChat = false;
                Bukkit.getServer()
                        .broadcastMessage(main.getConfig().getString("messages.blockChat.blockBroadcast.disable")
                                .replace("&", "§").replace("%prefix%", main.getConfig().getString("messages.prefix")));
            }
        } else
            sender.sendMessage(
                    main.getConfig().getString("messages.blockChat.blockBroadcast.noPerm").replace("&", "§"));
        return false;
    }
}
