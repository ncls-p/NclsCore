package fr.nclsp.events.chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.nclsp.Main;

public class OnChatWithBlockChat implements Listener {
    private Main main;

    public OnChatWithBlockChat(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onChatWithBlockChat(AsyncPlayerChatEvent e) {
        if (!e.getPlayer().hasPermission("ncls.chat.blockChat.bypass")) {
            if (main.blockChat) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(main.getConfig().getString("messages.blockChat.cantChat").replace("&", "§"));
            }
        }
    }

}