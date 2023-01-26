package fr.nclsp.events.moderation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.nclsp.Main;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

public class OnJoinBannedPlayer implements Listener {
    private final Main main;

    public OnJoinBannedPlayer(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        Player p = e.getPlayer();
        if (main.hMBannedDatetimeunban.containsKey(p.getUniqueId())) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime end = main.hMBannedDatetimeunban.get(p.getUniqueId());
            if (end == null) {
                String reason = main.dbModeration.getReasonBan(p, main.statementMod).replace('&', '§');
                String prefix = Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&', '§');
                String dateUnban = "Permanent";
                p.kickPlayer(Objects.requireNonNull(main.getConfig().getString("messages.ban.kickBanMessage"))
                        .replace('&', '§').replace("%reason%", reason).replace("%prefix%", prefix)
                        .replace("%DateTime%", dateUnban));
                e.setJoinMessage(null);
            } else if (now.isBefore(end)) {
                String reason = main.dbModeration.getReasonBan(p, main.statementMod).replace('&', '§');
                String prefix = Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&', '§');
                String dateUnban = end.toString();
                p.kickPlayer(Objects.requireNonNull(main.getConfig().getString("messages.ban.kickBanMessage"))
                        .replace('&', '§').replace("%reason%", reason).replace("%prefix%", prefix)
                        .replace("%DateTime%", dateUnban));
                e.setJoinMessage(null);
            } else {
                main.hMBannedDatetimeunban.remove(p.getUniqueId());
            }
        }
    }

}