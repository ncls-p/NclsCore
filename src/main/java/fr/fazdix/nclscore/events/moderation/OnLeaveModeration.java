package fr.fazdix.nclscore.events.moderation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.fazdix.nclscore.Main;

public class OnLeaveModeration implements Listener {
    private Main main;

    public OnLeaveModeration(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onBanKick(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (main.hMBannedDatetimeunban.containsKey(p.getUniqueId())) {
            e.setQuitMessage(null);
        }
    }
}