package fr.fazdix.nclscore.events.chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.fazdix.nclscore.Main;

public class OnChatWhithBlockChat implements Listener {
    private Main main;

    public OnChatWhithBlockChat(Main main) {
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