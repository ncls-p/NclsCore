package fr.fazdix.nclscore.events.moderation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.fazdix.nclscore.Main;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

public class PlayerChatModeration implements Listener {
    private Main main;

    public PlayerChatModeration(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) throws SQLException {
        Player p = e.getPlayer();
        if (main.hMMutedDatetimeunban.containsKey(p.getUniqueId())) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime end = main.hMMutedDatetimeunban.get(p.getUniqueId());
            if (end == null) {
                String reason = main.dbModeration.getReasonMute(p, main.statementMod).replace('&', '§');
                String prefix = Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&', '§');
                String dateUnban = "Permanent";
                p.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.mute.mutedMessage"))
                        .replace('&', '§').replace("%reason%", reason).replace("%prefix%", prefix)
                        .replace("%DateTime%", dateUnban));
                e.setCancelled(true);
            } else if (now.isBefore(end)) {
                String reason = main.dbModeration.getReasonMute(p, main.statementMod).replace('&', '§');
                String prefix = Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&', '§');
                String dateUnban = end.toString();
                p.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.mute.mutedMessage"))
                        .replace('&', '§').replace("%reason%", reason).replace("%prefix%", prefix)
                        .replace("%DateTime%", dateUnban));
                e.setCancelled(true);
            } else {
                main.hMBannedDatetimeunban.remove(p.getUniqueId());
            }
        }
    }
}